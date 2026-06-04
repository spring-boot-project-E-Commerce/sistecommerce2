package com.example.java.member.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coupon_seq")
	@SequenceGenerator(name = "coupon_seq", sequenceName = "coupon_seq",  allocationSize = 1)
	private Long seq;
	
	@Column(name = "name", nullable = false, length = 100)
	private String name;
	
	@Column(name = "discount_type", nullable = false)
	private String discountType;
	
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;
	
	@Column(name = "valid_days", nullable = false)
	private Integer validDays;
	
	@Column(name = "status", nullable = false)
	private Integer status;
	
	@Column(name = "discount_price")
	private Integer discountPrice;
	
	@Column(name = "discount_rate")
	private Integer discountRate;
	
	@Column(name = "expire_date")
	private LocalDate expireDate;
}
