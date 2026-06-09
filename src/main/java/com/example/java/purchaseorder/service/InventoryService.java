package com.example.java.purchaseorder.service;



import java.util.List;

import org.springframework.stereotype.Service;

import com.example.java.purchaseorder.dto.InventoryListDTO;
import com.example.java.purchaseorder.repository.InventoryQueryDslRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

	private final InventoryQueryDslRepository queryDslRepository;
	
	public List<InventoryListDTO> getInventoryList() {
		return queryDslRepository.findInventoryList();
    }
}
