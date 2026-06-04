package com.example.java.purchaseorder.dto;

import java.sql.Date;

import com.example.java.purchaseorder.enums.PurchaseOrderStatus;
import com.example.java.purchaseorder.enums.PurchaseOrderType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderCreateDTO {

    private PurchaseOrderStatus status;
    private int quantity;
    private Long supplyPrice;
    private Long totalPrice;

    private Date orderDate;
    private Date expectedDate;
    private Date receivedDate;

    private PurchaseOrderType type;

    private Long optionsSeq;
    private Long groupBuyOptionsSeq;
}