package com.amar.fullstack.expanse_tracker_backend.repository;

import com.amar.fullstack.expanse_tracker_backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Payment findByOrderId(String orderId);
}
