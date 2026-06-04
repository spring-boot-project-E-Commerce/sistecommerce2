package com.example.java.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.java.product.dto.ProductCreateRequestDto;
import com.example.java.product.dto.ProductCreateRequestDto.ProductImageRequestDto;
import com.example.java.product.dto.ProductCreateRequestDto.ProductOptionRequestDto;
import com.example.java.product.dto.ProductCreateResponseDto;
import com.example.java.product.repository.ProductRegisterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductRegisterService {

    private final ProductRegisterRepository productRegisterRepository;


    /*
        상품 등록 처리

        한 번의 상품 등록에서 처리하는 작업:

        1. 입력값 검증
        2. product 테이블 INSERT
        3. product_image 테이블 INSERT
        4. options 테이블 INSERT
           - 옵션은 없어도 됨
           - 옵션이 있을 때만 저장
        5. product_request 테이블 INSERT

        @Transactional
        - 중간에 하나라도 실패하면 전체 INSERT가 취소됩니다.
    */
    @Transactional
    public ProductCreateResponseDto createProduct(ProductCreateRequestDto dto) {

        validateCreateProduct(dto);

        Long productSeq = productRegisterRepository.getNextSeq("product");

        /*
            현재 테스트에서는 사용하지 않음
            - admin 데이터가 없어서 product_request 등록을 임시 제외함
        */
        // Long requestSeq = productRegisterRepository.getNextSeq("product_request");

        productRegisterRepository.insertProduct(productSeq, dto);

        /*
            이미지 등록

            이미지는 상품 등록 화면에서 대표 이미지가 필요하므로
            최소 1개 이상 필요하게 유지합니다.
        */
        if (dto.getImageList() != null) {
            for (ProductImageRequestDto imageDto : dto.getImageList()) {

                Long imageSeq = productRegisterRepository.getNextSeq("product_image");

                productRegisterRepository.insertProductImage(
                        imageSeq,
                        productSeq,
                        imageDto
                );
            }
        }

        /*
            옵션 등록

            수정된 부분:
            옵션은 없어도 됩니다.

            dto.getOptionList()가 null이거나 비어 있으면
            옵션 저장 과정을 건너뜁니다.

            옵션 목록이 있을 때만 반복문을 돌면서 options 테이블에 저장합니다.
        */
        if (dto.getOptionList() != null && !dto.getOptionList().isEmpty()) {

            for (ProductOptionRequestDto optionDto : dto.getOptionList()) {

                Long optionSeq = productRegisterRepository.getNextSeq("options");

                productRegisterRepository.insertProductOption(
                        optionSeq,
                        productSeq,
                        optionDto
                );
            }
        }

        /*
            현재 테스트에서는 사용하지 않음
            - admin 데이터가 없어서 product_request 등록을 임시 제외함
        */
        /*
        productRegisterRepository.insertProductRequest(
                requestSeq,
                productSeq,
                dto
        );
        */

        return new ProductCreateResponseDto(
                productSeq,
                null,
                "상품 등록 테스트가 완료되었습니다. 현재 product_request 등록은 생략되었습니다."
        );
    }


    /*
        상품 등록 입력값 검증
    */
    private void validateCreateProduct(ProductCreateRequestDto dto) {

        if (dto.getSellerSeq() == null) {
            throw new IllegalArgumentException("판매자 번호는 필수입니다.");
        }

        if (!productRegisterRepository.existsSeller(dto.getSellerSeq())) {
            throw new IllegalArgumentException("존재하지 않거나 활성 상태가 아닌 판매자입니다.");
        }

        /*
            현재 테스트에서는 사용하지 않음
            - admin 데이터가 없어서 관리자 검증을 임시 제외함
        */
        /*
        if (dto.getAdminSeq() == null) {
            throw new IllegalArgumentException("관리자 번호는 필수입니다.");
        }

        if (!productRegisterRepository.existsAdmin(dto.getAdminSeq())) {
            throw new IllegalArgumentException("존재하지 않거나 비활성 상태인 관리자입니다.");
        }
        */

        if (dto.getCategorySeq() == null) {
            throw new IllegalArgumentException("카테고리 번호는 필수입니다.");
        }

        if (dto.getProductName() == null || dto.getProductName().isBlank()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }

        if (dto.getProductName().length() > 60) {
            throw new IllegalArgumentException("상품명은 최대 60자까지 입력할 수 있습니다.");
        }

        if (dto.getPrice() == null || dto.getPrice() < 0) {
            throw new IllegalArgumentException("상품 가격은 0원 이상이어야 합니다.");
        }

        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new IllegalArgumentException("상품 설명은 필수입니다.");
        }

        /*
            이미지 검증은 유지합니다.
            상품 등록 화면에서 대표 이미지는 필요하기 때문입니다.
        */
        validateImages(dto);

        /*
            수정된 부분:
            옵션은 없어도 되므로 optionList 필수 검증은 하지 않습니다.

            단, 옵션 목록이 전달된 경우에는
            재고, 안전재고, 추가금액 값이 정상인지 검증합니다.
        */
        if (dto.getOptionList() != null && !dto.getOptionList().isEmpty()) {
            validateOptions(dto);
        }
    }


    /*
        이미지 검증

        대표 이미지는 정확히 1개만 있어야 합니다.
    */
    private void validateImages(ProductCreateRequestDto dto) {

        if (dto.getImageList() == null || dto.getImageList().isEmpty()) {
            throw new IllegalArgumentException("상품 이미지는 최소 1개 이상 필요합니다.");
        }

        int thumbnailCount = 0;

        for (ProductImageRequestDto imageDto : dto.getImageList()) {

            if (imageDto.getImageUrl() == null || imageDto.getImageUrl().isBlank()) {
                throw new IllegalArgumentException("이미지 URL은 필수입니다.");
            }

            if (imageDto.getThumbnailYn() == null || imageDto.getThumbnailYn().isBlank()) {
                imageDto.setThumbnailYn("N");
            }

            if (!imageDto.getThumbnailYn().equals("Y")
                    && !imageDto.getThumbnailYn().equals("N")) {
                throw new IllegalArgumentException("대표 이미지 여부는 Y 또는 N만 가능합니다.");
            }

            if (imageDto.getThumbnailYn().equals("Y")) {
                thumbnailCount++;
            }

            if (imageDto.getImageOrder() == null) {
                imageDto.setImageOrder(1);
            }
        }

        if (thumbnailCount != 1) {
            throw new IllegalArgumentException("대표 이미지는 반드시 1개여야 합니다.");
        }
    }


    /*
        옵션 검증

        옵션이 있을 때만 실행됩니다.

        옵션 자체는 선택사항이지만,
        옵션을 입력했다면 재고, 안전재고, 추가금액 값은 정상이어야 합니다.
    */
    private void validateOptions(ProductCreateRequestDto dto) {

        for (ProductOptionRequestDto optionDto : dto.getOptionList()) {

            if (optionDto.getStock() == null || optionDto.getStock() < 0) {
                throw new IllegalArgumentException("재고는 0개 이상이어야 합니다.");
            }

            if (optionDto.getSafetyStock() == null || optionDto.getSafetyStock() < 0) {
                throw new IllegalArgumentException("안전재고는 0개 이상이어야 합니다.");
            }

            if (optionDto.getAdditionalPrice() == null) {
                optionDto.setAdditionalPrice(0);
            }

            if (optionDto.getAdditionalPrice() < 0) {
                throw new IllegalArgumentException("추가 금액은 0원 이상이어야 합니다.");
            }
        }
    }
}