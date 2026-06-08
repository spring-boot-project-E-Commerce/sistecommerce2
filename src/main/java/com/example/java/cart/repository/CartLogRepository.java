package com.example.java.cart.repository;

import com.example.java.cart.entity.CartLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartLogRepository extends JpaRepository<CartLog, Long> {
}
