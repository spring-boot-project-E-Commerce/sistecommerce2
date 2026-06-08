package com.example.java.cart.controller;

import com.example.java.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CartViewController {

    private final CartService cartService;

}
