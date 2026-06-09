package com.example.java.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.java.mypage.dto.MyReviewDto;
import com.example.java.product.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyReviewService {

    private final ReviewRepository reviewRepository;

    /**
     * 내가 쓴 리뷰 목록 조회
     * TODO: 구현 예정
     */
    @Transactional(readOnly = true)
    public List<MyReviewDto> getMyReviews(Long memberSeq) {
        // TODO: 구현 예정
        return reviewRepository.findMyReviews(memberSeq);
    }

    /**
     * 리뷰 수정
     * TODO: 구현 예정
     */
    @Transactional
    public void updateReview(Long reviewSeq, Long productSeq, Long memberSeq, Integer rating, String content) {
        // TODO: 구현 예정
        throw new UnsupportedOperationException("updateReview 미구현");
    }

    /**
     * 리뷰 삭제 (soft delete)
     * TODO: 구현 예정
     */
    @Transactional
    public void deleteReview(Long reviewSeq, Long productSeq, Long memberSeq) {
        // TODO: 구현 예정
        throw new UnsupportedOperationException("deleteReview 미구현");
    }
}
