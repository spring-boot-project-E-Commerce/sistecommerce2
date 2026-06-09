package com.example.java.product.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.java.product.dto.CloudinaryUploadResult;
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

    private final CloudinaryUploadService cloudinaryUploadService;


    /*
        상품 등록 처리

        한 번의 상품 등록에서 처리하는 작업:

        1. 입력값 검증
        2. Cloudinary 이미지 업로드
        3. product 테이블 INSERT
           - thumbnail_url 컬럼에 대표 이미지 Cloudinary URL 저장
        4. product_image 테이블 INSERT
           - Cloudinary image_url, public_id 저장
        5. options 테이블 INSERT
           - 옵션은 없어도 됨
           - 옵션이 있을 때만 저장
        6. product_request 테이블 INSERT

        @Transactional
        - 중간에 하나라도 실패하면 전체 INSERT가 취소됩니다.
    */
    @Transactional
    public ProductCreateResponseDto createProduct(
            ProductCreateRequestDto dto,
            List<MultipartFile> images,
            List<MultipartFile> detailImages,
            int thumbnailIndex) {

        validateCreateProduct(dto);
        validateImageFiles(images, thumbnailIndex);

        Long productSeq = productRegisterRepository.getNextSeq("product");

        /*
            현재 테스트에서는 사용하지 않음
            - admin 데이터가 없어서 product_request 등록을 임시 제외함
        */
        // Long requestSeq = productRegisterRepository.getNextSeq("product_request");

        /*
            이미지 업로드

            화면에서 전달받은 이미지 파일들을 Cloudinary에 업로드합니다.

            업로드 후 Cloudinary가 반환하는 값:
            - secure_url → DB의 image_url / thumbnail_url에 저장
            - public_id  → DB의 public_id에 저장

            thumbnailIndex에 해당하는 이미지를 대표 이미지로 지정합니다.
        */
        String thumbnailUrl = null;
        List<ProductImageRequestDto> uploadedImageList = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {

            MultipartFile image = images.get(i);

            CloudinaryUploadResult uploadResult =
                    cloudinaryUploadService.uploadProductImage(image);

            ProductImageRequestDto imageDto = new ProductImageRequestDto();
            imageDto.setImageUrl(uploadResult.getImageUrl());
            imageDto.setPublicId(uploadResult.getPublicId());
            imageDto.setThumbnailYn(i == thumbnailIndex ? "Y" : "N");
            imageDto.setImageOrder(i + 1);

            uploadedImageList.add(imageDto);

            if (i == thumbnailIndex) {
                thumbnailUrl = uploadResult.getImageUrl();
            }
        }

        /*
            상품 등록

            product 테이블에 상품 기본 정보와 대표 이미지 URL을 저장합니다.

            thumbnailUrl:
            - Cloudinary에서 가져온 대표 이미지 URL
            - product.thumbnail_url 컬럼에 들어갑니다.
        */
        productRegisterRepository.insertProduct(productSeq, dto, thumbnailUrl);

        /*
            이미지 등록

            Cloudinary에 업로드된 이미지 정보를 product_image 테이블에 저장합니다.
        */
        for (ProductImageRequestDto imageDto : uploadedImageList) {

            Long imageSeq = productRegisterRepository.getNextSeq("product_image");

            productRegisterRepository.insertProductImage(
                    imageSeq,
                    productSeq,
                    imageDto
            );
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
                "상품 등록 테스트가 완료되었습니다. 대표 이미지가 Cloudinary에 저장되었습니다."
        );
    }


    /*
        상품 등록 입력값 검증

        이미지 검증은 validateImageFiles()에서 처리합니다.
        이유:
        - 이제 이미지 URL을 JSON으로 받는 방식이 아니라,
          화면에서 MultipartFile로 이미지 파일을 받기 때문입니다.
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
        이미지 파일 검증

        화면에서 사용자가 선택한 이미지 파일을 검증합니다.

        조건:
        - 이미지는 최소 1개 이상 필요합니다.
        - thumbnailIndex는 이미지 목록 범위 안에 있어야 합니다.
        - 빈 파일은 등록할 수 없습니다.
        - image 타입 파일만 등록할 수 있습니다.
    */
    private void validateImageFiles(List<MultipartFile> images, int thumbnailIndex) {

        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("상품 이미지는 최소 1개 이상 필요합니다.");
        }

        if (thumbnailIndex < 0 || thumbnailIndex >= images.size()) {
            throw new IllegalArgumentException("대표 이미지 순서가 올바르지 않습니다.");
        }

        for (MultipartFile image : images) {

            if (image == null || image.isEmpty()) {
                throw new IllegalArgumentException("비어 있는 이미지는 등록할 수 없습니다.");
            }

            String contentType = image.getContentType();

            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("이미지 파일만 등록할 수 있습니다.");
            }
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