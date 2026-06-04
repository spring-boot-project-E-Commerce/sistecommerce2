package com.example.java.product.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product {

    @Id
    private Long seq;
    private Long sellerSeq;
    private Long categorySeq;
    private String productName;
    private Integer price;
    private String content;
    private String saleStatus;
    private String approvalStatus;
    private String hideYn;
    private Integer viewCount;
    private Double avgRating;
    private Integer reviewCount;
    private Integer salesCount;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String status;
}