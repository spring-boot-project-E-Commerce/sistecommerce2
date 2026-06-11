package com.example.java.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.java.admin.dto.GroupBuyCreateDto;
import com.example.java.admin.dto.GroupBuyOptionDto;
import com.example.java.admin.dto.SellerInfoDto;
import com.example.java.admin.repository.GroupBuyQueryDslRepository;
import com.example.java.product.entity.Options;
import com.example.java.product.entity.Product;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupBuyAdminService {

    private final GroupBuyQueryDslRepository repository;

    public GroupBuyCreateDto getCreateForm(List<Long> optionSeqs) {

        List<Options> optionsList =
                repository.getOptions(optionSeqs);

        if (optionsList.isEmpty()) {
            throw new IllegalArgumentException("선택된 옵션이 없습니다.");
        }

        Options firstOption = optionsList.get(0);
        Product product = firstOption.getProduct();

        SellerInfoDto sellerInfo =
                repository.getSellerInfo(product.getSellerSeq());

        Integer supplyPrice =
                product.getPrice()
                * sellerInfo.getSupplyRate() / 100;

        List<GroupBuyOptionDto> optionDtos =
                optionsList.stream()
                        .map(option -> GroupBuyOptionDto.builder()
                                .optionSeq(option.getSeq())
                                .optionName(option.getDisplayName())
                                .additionalPrice(option.getAdditionalPrice())
                                .build())
                        .toList();

        return GroupBuyCreateDto.builder()
                .productName(product.getProductName())
                .sellerName(sellerInfo.getSellerName())
                .sellerPhone(sellerInfo.getSellerPhone())
                .supplyPrice(supplyPrice)
                .options(optionDtos)
                .productSeq(product.getSeq())
                .originalPrice(product.getPrice())
                .build();
    }
}