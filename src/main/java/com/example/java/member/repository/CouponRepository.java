package com.example.java.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.java.member.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long>{

}
