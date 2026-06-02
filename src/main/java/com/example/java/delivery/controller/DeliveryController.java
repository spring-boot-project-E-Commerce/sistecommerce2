package com.example.java.delivery.controller;

import org.springframework.stereotype.Controller;

import com.example.java.delivery.service.DeliveryService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class DeliveryController {

	private final DeliveryService deliveryService;
	
	@GetMapping("/list")
	public String list() {
		
		return "list";
	}
	
}
