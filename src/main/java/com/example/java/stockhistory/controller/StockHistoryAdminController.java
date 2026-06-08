package com.example.java.stockhistory.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.java.stockhistory.entity.StockHistory;
import com.example.java.stockhistory.service.StockHistoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class StockHistoryAdminController {

	private final StockHistoryService stockHistoryService;
	
	@GetMapping("/stock-histories")
	public String getList() {
		List<StockHistory> list = stockHistoryService.getList();
		
		for (StockHistory sh : list) {
		    System.out.println("type = " + sh.getType());
		    System.out.println("reason = " + sh.getReason());
		    System.out.println("quantity = " + sh.getQuantity());
		    System.out.println("beforeStock = " + sh.getBeforeStock());
		    System.out.println("afterStock = " + sh.getAfterStock());
		    System.out.println("sourceType = " + sh.getSourceType());
		    System.out.println("createdAt = " + sh.getCreatedAt());
		    System.out.println("optionsSeq = " + sh.getOptions().getSeq());
		}
		
		return "/admin/stock-history/list";
	}
}
