package com.example.java.member.service;

import com.example.java.member.dto.DeliveryAddressDto;
import com.example.java.member.entity.DeliveryAddress;
import com.example.java.member.entity.Member;
import com.example.java.member.repository.MemberAddressRepository;
import com.example.java.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberAddressService {

    private final MemberAddressRepository memberAddressRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addMyAddress(DeliveryAddressDto deliveryAddressDto) {
        Member member = memberRepository.getReferenceById(deliveryAddressDto.getMemberSeq());

        DeliveryAddress deliveryAddress = DeliveryAddress.builder()
                .member(member)
                .addressAlias(deliveryAddressDto.getAddressAlias())
                .recipientName(deliveryAddressDto.getRecipientName())
                .recipientPhone(deliveryAddressDto.getRecipientPhone())
                .zipcode(deliveryAddressDto.getZipcode())
                .address(deliveryAddressDto.getAddress())
                .addressDetail(deliveryAddressDto.getAddressDetail())
                .note(deliveryAddressDto.getNote())
                .entryCode(deliveryAddressDto.getEntryCode())
                .defaultYn(deliveryAddressDto.getDefaultYn())
                .status("Y")
                .build();

        if ("Y".equals(deliveryAddressDto.getDefaultYn())) {
            memberAddressRepository.findByMember_SeqOrderByDefaultYnDesc(deliveryAddressDto.getMemberSeq()).stream()
                    .filter(a -> "Y".equals(a.getDefaultYn()))
                    .forEach(DeliveryAddress::clearDefault);
        }

        memberAddressRepository.save(deliveryAddress);
    }

    public List<DeliveryAddress> myAddress(Long memberSeq) {
        return memberAddressRepository.findByMember_SeqOrderByDefaultYnDesc(memberSeq);
    }
}
