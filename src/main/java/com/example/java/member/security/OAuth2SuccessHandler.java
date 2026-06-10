package com.example.java.member.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.java.member.service.SessionManagementService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SessionManagementService sessionManagementService;

    public OAuth2SuccessHandler(SessionManagementService sessionManagementService) {
        this.sessionManagementService = sessionManagementService;
        setDefaultTargetUrl("/");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        sessionManagementService.register(request, request.getSession(), userDetails.getUsername());
        log.info("소셜 로그인 성공: {}", userDetails.getUsername());

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
