package com.example.java.product.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.java.product.dto.ProductCreateRequestDto;
import com.example.java.product.dto.ProductCreateResponseDto;
import com.example.java.product.service.ProductRegisterService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductRegisterApiController {

    private final ProductRegisterService productRegisterService;


    /*
        상품 등록 API

        테스트 주소:
        POST http://localhost:8080/api/product/register

        HTML 화면 없이 JSON으로 요청을 보내서 테스트할 수 있습니다.
    */
    @PostMapping("/api/product/register")
    public ResponseEntity<ProductCreateResponseDto> registerProduct(
            @RequestBody ProductCreateRequestDto dto) {

        ProductCreateResponseDto response = productRegisterService.createProduct(dto);

        return ResponseEntity.ok(response);
    }


    /*
        입력값 오류 처리

        Service에서 IllegalArgumentException이 발생하면
        400 Bad Request로 응답합니다.
    */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {

        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }


    /*
        예상하지 못한 서버 오류 처리
    */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {

        return ResponseEntity
                .internalServerError()
                .body("서버 오류가 발생했습니다: " + e.getMessage());
    }
}