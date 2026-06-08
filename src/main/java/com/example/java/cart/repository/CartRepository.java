package com.example.java.cart.repository;

import com.example.java.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByMember_Seq(Long memberSeq);
}
