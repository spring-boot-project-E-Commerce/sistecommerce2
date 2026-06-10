package com.example.java.member.security;

import java.util.Map;

/**
 * Naver 응답 파싱.
 * Naver는 실제 사용자 정보가 최상위 "response" 키 안에 감싸져 있음.
 * application.yml: user-name-attribute: response
 * attributes = { "response": { "id": "...", "name": "...", "email": "..." } }
 */
public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes; // "response" 내부 맵

    @SuppressWarnings("unchecked")
    public NaverOAuth2UserInfo(Map<String, Object> rawAttributes) {
        this.attributes = (Map<String, Object>) rawAttributes.get("response");
    }

    @Override
    public String getProvider() { return "NAVER"; }

    @Override
    public String getProviderId() { return (String) attributes.get("id"); }

    @Override
    public String getName() { return (String) attributes.get("name"); }

    @Override
    public String getEmail() { return (String) attributes.get("email"); }
}
