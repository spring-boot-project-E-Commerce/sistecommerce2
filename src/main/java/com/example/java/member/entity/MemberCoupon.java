package com.example.java.member.entity;

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
@Table(name = "member_coupon")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCoupon {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_coupon_seq")
	@SequenceGenerator(name = "member_coupon_seq", sequenceName = "member_coupon_seq",  allocationSize = 1)
	private Long seq;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_seq", nullable = false)
	private Member memberSeq;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "coupon_seq", nullable = false)
	private Coupon couponSeq;
	
	@Column(name = "status", nullable = false)
	private Integer status;
	

}
