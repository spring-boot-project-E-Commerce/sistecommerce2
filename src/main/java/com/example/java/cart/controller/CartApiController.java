package com.example.java.cart.controller;

import com.example.java.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CartApiController {

    private final CartService cartService;

}
