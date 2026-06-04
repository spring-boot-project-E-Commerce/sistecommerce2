package com.example.java.product.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateRequestDto {

    // =========================
    // 상품 기본 정보 영역
    // =========================
    private Long sellerSeq;
    private Long categorySeq;

    private String productName;
    private Integer price;
    private String content;

    // =========================
    // 관리자 배정 영역
    // product_request.admin_seq에 들어감
    // 테스트할 때는 1 넣으면 됨
    // =========================
    private Long adminSeq;

    // =========================
    // 상품 이미지 영역
    // =========================
    private List<ProductImageRequestDto> imageList = new ArrayList<>();

    // =========================
    // 상품 옵션 영역
    // =========================
    private List<ProductOptionRequestDto> optionList = new ArrayList<>();


    @Getter
    @Setter
    public static class ProductImageRequestDto {

        private String imageUrl;
        private String publicId;

        // Y / N
        private String thumbnailYn;

        private Integer imageOrder;
    }


    @Getter
    @Setter
    public static class ProductOptionRequestDto {

        // =========================
        // 상품 옵션 기본 정보 영역
        // =========================
        private Long seq;
        private Long productSeq;

        // =========================
        // 옵션 항목 영역
        // =========================
        private String color;
        private String optionsSize;
        private String volumeWeight;
        private String taste;
        private String storageType;
        private String scentIngredient;
        private String voltage;
        private String quantitySet;
        private String sizeSpec;
        private String storageCapacity;
        private String memory;
        private String switchAxis;
        private String connectionType;
        private String wearableSpec;
        private String materialType;
        private String optionsType;

        // =========================
        // 재고 / 가격 영역
        // =========================
        private Integer stock;
        private Integer safetyStock;
        private Integer additionalPrice;
    }
}