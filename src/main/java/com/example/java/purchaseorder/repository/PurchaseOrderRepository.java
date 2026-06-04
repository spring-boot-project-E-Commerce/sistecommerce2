package com.example.java.purchaseorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.java.purchaseorder.entity.PurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

}
