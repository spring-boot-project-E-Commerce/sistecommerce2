package com.example.java.mypage.controller;

import com.example.java.member.dto.DeliveryAddressDto;
import com.example.java.member.entity.DeliveryAddress;
import com.example.java.member.security.CustomUserDetails;
import com.example.java.member.service.MemberAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/mypage/address")
@RestController
@RequiredArgsConstructor
public class MyPageApiController {

    private final MemberAddressService memberAddressService;

    @PostMapping
    public ResponseEntity<?> addMyAddress(@RequestBody DeliveryAddressDto deliveryAddressDto, @AuthenticationPrincipal     CustomUserDetails customUserDetails ) {

        deliveryAddressDto.setMemberSeq(customUserDetails.getMemberSeq());
        memberAddressService.addMyAddress(deliveryAddressDto);

        return ResponseEntity.ok().build();
    }


}
