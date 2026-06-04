package com.example.java.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductCreateResponseDto {

    private Long productSeq;
    private Long productRequestSeq;
    private String message;
}