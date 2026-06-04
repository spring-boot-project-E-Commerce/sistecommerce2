package com.example.java.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImageDto {

    private Long seq;
    private Long reviewSeq;
    private String imageUrl;
    private String publicId;
    private Integer imageOrder;
    private String fileType;
    private Long fileSize;
    private String status;
}