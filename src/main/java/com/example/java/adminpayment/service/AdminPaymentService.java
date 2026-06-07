package com.example.java.adminpayment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.java.adminpayment.entity.AdminPayment;
import com.example.java.adminpayment.enums.PaymentType;
import com.example.java.adminpayment.repository.AdminPaymentRepository;
import com.example.java.purchaseorder.entity.PurchaseOrder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPaymentService {

	private final AdminPaymentRepository adminPaymentRepository;
	
	@Transactional
	public void createPurchasePayment(PurchaseOrder order) {

	    AdminPayment payment = AdminPayment.builder()
	            .type(PaymentType.발주)
	            .status(0)
	            .purchaseOrder(order)
	            .build();
	    adminPaymentRepository.save(payment);
	}
}
