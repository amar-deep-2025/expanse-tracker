package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.PaymentVerifyRequest;
import com.amar.fullstack.expanse_tracker_backend.entity.Payment;
import com.amar.fullstack.expanse_tracker_backend.entity.PaymentStatus;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.repository.PaymentRepository;
import com.amar.fullstack.expanse_tracker_backend.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Value("${razorpay.key.secret}")
    private String secret;

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public PaymentService(RazorpayClient razorpayClient,
                          PaymentRepository paymentRepository,
                          UserRepository userRepository){
        this.razorpayClient=razorpayClient;
        this.paymentRepository=paymentRepository;
        this.userRepository=userRepository;
    }

    public String createOrder(int amount, User user) throws Exception {

        JSONObject options = new JSONObject();
        options.put("amount", amount * 100);
        options.put("currency", "INR");

        options.put("receipt", "expanse_" + System.currentTimeMillis());

        Order order=razorpayClient.orders.create(options);
        String orderId=order.get("id").toString();

        Payment payment=new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setUser(user);
        payment.setStatus(PaymentStatus.CREATED);

        paymentRepository.save(payment);

        return order.toString();

    }


    public String verifyPayment(PaymentVerifyRequest request, User user) {

        String orderId=request.getRazorpay_order_id();
        String paymentId=request.getRazorpay_payment_id();
        String signature=request.getRazorpay_signature();


        String payload=orderId+"|"+paymentId;
        boolean isValid;
        try {
             isValid = Utils.verifySignature(payload, signature, secret);
        }catch(Exception e){
            return "Error during signature verification: "+e.getMessage();
        }
        Payment payment=paymentRepository.findByOrderId(orderId);

        if (payment==null){
            return "Payment record not found";
        }
        if (isValid){
            payment.setPaymentId(paymentId);
            payment.setSignature(signature);
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);

            user.setPremium(true);
            userRepository.save(user);
            return "Payment SUCCESS & user upgraded";
        }else{
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            return "Payment verification failed";

        }

    }
}
