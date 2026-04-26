package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.NotificationRequest;
import com.amar.fullstack.expanse_tracker_backend.dtos.PaymentVerifyRequest;
import com.amar.fullstack.expanse_tracker_backend.entity.NotificationType;
import com.amar.fullstack.expanse_tracker_backend.entity.Payment;
import com.amar.fullstack.expanse_tracker_backend.entity.PaymentStatus;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.notification.service.NotificationService;
import com.amar.fullstack.expanse_tracker_backend.repository.PaymentRepository;
import com.amar.fullstack.expanse_tracker_backend.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Value("${razorpay.key.secret}")
    private String secret;

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public PaymentService(RazorpayClient razorpayClient,
            PaymentRepository paymentRepository,
            UserRepository userRepository,
            NotificationService notificationService) {
        this.razorpayClient = razorpayClient;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public String createOrder(int amount, User user) throws Exception {

        JSONObject options = new JSONObject();
        options.put("amount", amount * 100);
        options.put("currency", "INR");
        options.put("receipt", "expanse_" + System.currentTimeMillis());

        Order order = razorpayClient.orders.create(options);
        String orderId = order.get("id").toString();

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setUser(user);
        payment.setStatus(PaymentStatus.CREATED);

        paymentRepository.save(payment);

        return order.toString();
    }

    public String verifyPayment(PaymentVerifyRequest request, User user) {

        String orderId = request.getRazorpay_order_id();
        String paymentId = request.getRazorpay_payment_id();
        String signature = request.getRazorpay_signature();

        String payload = orderId + "|" + paymentId;

        boolean isValid;
        try {
            isValid = Utils.verifySignature(payload, signature, secret);
        } catch (Exception e) {
            return "Error during verification";
        }

        Payment payment = paymentRepository.findByOrderId(orderId);

        if (payment == null) {
            return "Payment record not found";
        }

        if (isValid) {
            payment.setPaymentId(paymentId);
            payment.setSignature(signature);
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);

            user.setPremium(true);
            userRepository.save(user);

            // ✅ SUCCESS NOTIFICATION
            NotificationRequest notify = new NotificationRequest();
            notify.setEmail(user.getEmail());
            notify.setPhone(user.getPhone());
            notify.setSubject("Payment Successful");

            notify.setMessage(
                    "Hello " + user.getName() + ",\n\n" +
                            "Your payment has been successfully processed.\n\n" +
                            "Amount: ₹" + payment.getAmount() + "\n" +
                            "Transaction ID: " + paymentId + "\n\n" +
                            "Your account is now upgraded to premium.\n\n" +
                            "----------------------------------\n" +
                            "This is a system-generated email. Please do not reply.\n\n" +
                            "Regards,\n" +
                            "Expanse Tracker Team");

            notify.setSmsMessage(
                    "Payment successful! ₹" + payment.getAmount() +
                            " credited. Premium activated.");

            notify.setTypes(List.of(
                    NotificationType.EMAIL,
                    NotificationType.SMS));

            notificationService.send(notify);

            return "Payment SUCCESS";
        }

        // ❌ FAILED CASE
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        NotificationRequest notify = new NotificationRequest();
        notify.setEmail(user.getEmail());
        notify.setSubject("Payment Failed");

        notify.setMessage(
                "Hello " + user.getName() + ",\n\n" +
                        "Your payment could not be processed.\n\n" +
                        "If any amount was deducted, it will be refunded shortly.\n\n" +
                        "Please try again.\n\n" +
                        "----------------------------------\n" +
                        "This is a system-generated email.");

        notify.setTypes(List.of(
                NotificationType.EMAIL));

        notificationService.send(notify);

        return "Payment FAILED";
    }
}