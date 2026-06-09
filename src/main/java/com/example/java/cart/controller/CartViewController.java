package com.example.java.cart.controller;

import com.example.java.cart.dto.CartDto;
import com.example.java.cart.service.CartService;
import com.example.java.member.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartViewController {

    private final CartService cartService;

    @GetMapping(value = "")
    public String cart(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        Long memberSeq = userDetails.getMemberSeq();

        List<CartDto> cartList = cartService.list(memberSeq);

        model.addAttribute("cartList", cartList);

        return "cart/cart";
    }


}
