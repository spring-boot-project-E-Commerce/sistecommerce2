package com.example.java.purchaseorder.service;

import org.springframework.stereotype.Service;

import com.example.java.groupbuy.entity.GroupBuyOptions;
import com.example.java.groupbuy.repository.GroupBuyOptionsRepository;
import com.example.java.product.entity.Options;
import com.example.java.product.repository.OptionsRepository;
import com.example.java.purchaseorder.dto.PurchaseOrderCreateDTO;
import com.example.java.purchaseorder.entity.PurchaseOrder;
import com.example.java.purchaseorder.enums.PurchaseOrderStatus;
import com.example.java.purchaseorder.enums.PurchaseOrderType;
import com.example.java.purchaseorder.repository.PurchaseOrderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderService {

	private final PurchaseOrderRepository purchaseOrderRepository;
	private final OptionsRepository optionsRepository;
	private final GroupBuyOptionsRepository groupBuyOptionsRepository;
	
	public Long create(PurchaseOrderCreateDTO dto) {
		Options options = getOptions(dto.getOptionsSeq());
		GroupBuyOptions group = getGroupBuyOptions(dto.getGroupBuyOptionsSeq());

	    PurchaseOrder order = PurchaseOrder.builder()
	    		.status(PurchaseOrderStatus.발주요청)
	            .quantity(dto.getQuantity())
	            .supplyPrice(dto.getSupplyPrice())
	            .totalPrice(dto.getTotalPrice())
	            .orderDate(dto.getOrderDate())
	            .expectedDate(dto.getExpectedDate())
	            .receivedDate(null)
	            .type(dto.getType() != null ? dto.getType() : PurchaseOrderType.일반)
	            .options(options)
	            .groupBuyOptions(group)
	            .build();
	    
	    // TODO 판매처에 알림 주는것 추가해야 함

	    return purchaseOrderRepository.save(order).getSeq();
	}
	
	private Options getOptions(Long seq) {
		if (seq == null) {
	        throw new IllegalArgumentException("optionsSeq is required");
	    }
	    return optionsRepository.findById(seq)
	            .orElseThrow(() -> new IllegalArgumentException("Options not found"));
	}
	
	private GroupBuyOptions getGroupBuyOptions(Long seq) {
	    if (seq == null) return null;
	    return groupBuyOptionsRepository.findById(seq)
	            .orElseThrow(() -> new IllegalArgumentException("GroupBuyOptions not found"));
	}
}
