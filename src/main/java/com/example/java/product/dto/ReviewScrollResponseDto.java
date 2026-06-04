package com.example.java.product.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewScrollResponseDto {

    private List<ReviewResponseDto> reviews;

    private Long nextLastReviewSeq;

    private boolean hasNext;
}