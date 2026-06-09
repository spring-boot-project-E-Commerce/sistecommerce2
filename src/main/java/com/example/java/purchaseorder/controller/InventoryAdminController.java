package com.example.java.purchaseorder.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.java.purchaseorder.dto.InventoryListDTO;
import com.example.java.purchaseorder.service.InventoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class InventoryAdminController {

	private final InventoryService inventoryService;
	
	@GetMapping("/inventories")
	public String get(Model model) {
		List<InventoryListDTO> list = inventoryService.getInventoryList();
		
		model.addAttribute("list", list);
		
		return "admin/inventory/list";
	}
}
