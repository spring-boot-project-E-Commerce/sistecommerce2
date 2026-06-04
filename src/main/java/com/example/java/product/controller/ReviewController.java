package com.example.java.product.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.java.product.dto.ReviewScrollResponseDto;
import com.example.java.product.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    // 리뷰 관련 비즈니스 로직을 처리하는 Service
    private final ReviewService reviewService;

    // 전체 리뷰 무한스크롤 조회
    // 처음 조회: GET /api/reviews?size=10
    // 다음 조회: GET /api/reviews?lastReviewSeq=15&size=10
    @GetMapping("/api/reviews")
    public ResponseEntity<ReviewScrollResponseDto> getAllReviews(
            @RequestParam(name = "lastReviewSeq", required = false) Long lastReviewSeq,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        // 마지막으로 조회한 리뷰 번호를 기준으로 다음 리뷰 목록 조회
        ReviewScrollResponseDto result =
                reviewService.getAllReviewsByScroll(lastReviewSeq, size);

        // 조회 결과를 JSON으로 응답
        return ResponseEntity.ok(result);
    }

    // 특정 상품의 리뷰 무한스크롤 조회
    // 처음 조회: GET /api/product/1494/reviews?size=10
    // 다음 조회: GET /api/product/1494/reviews?lastReviewSeq=15&size=10
    @GetMapping("/api/product/{productSeq}/reviews")
    public ResponseEntity<ReviewScrollResponseDto> getProductReviews(
            @PathVariable(name = "productSeq") Long productSeq,
            @RequestParam(name = "lastReviewSeq", required = false) Long lastReviewSeq,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        // 상품 번호와 마지막 리뷰 번호를 기준으로 다음 리뷰 목록 조회
        ReviewScrollResponseDto result =
                reviewService.getProductReviewsByScroll(productSeq, lastReviewSeq, size);

        // 조회 결과를 JSON으로 응답
        return ResponseEntity.ok(result);
    }
}