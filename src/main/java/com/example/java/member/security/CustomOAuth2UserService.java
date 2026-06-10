package com.example.java.member.security;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.java.member.entity.Member;
import com.example.java.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google" | "naver"
        OAuth2UserInfo userInfo = resolveUserInfo(registrationId, attributes);

        String username = userInfo.toUsername(); // "google_109374..." | "naver_abc123..."

        Member member = memberRepository.findByUsername(username)
                .map(existing -> {
                    existing.updateSocialInfo(userInfo.getName(), userInfo.getEmail());
                    return existing;
                })
                .orElseGet(() -> {
                    String nickname = resolveNickname(userInfo.getName());
                    return memberRepository.save(
                            Member.ofSocial(username, nickname, userInfo.getName(),
                                    userInfo.getEmail(), userInfo.getProvider())
                    );
                });

        log.debug("소셜 로그인: {} ({})", username, registrationId);

        return new CustomUserDetails(member, attributes);
    }

    private OAuth2UserInfo resolveUserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "google" -> new GoogleOAuth2UserInfo(attributes);
            case "naver"  -> new NaverOAuth2UserInfo(attributes);
            default -> throw new OAuth2AuthenticationException("지원하지 않는 소셜 provider: " + registrationId);
        };
    }

    /**
     * 닉네임 중복 시 "_2", "_3" suffix 부여
     */
    private String resolveNickname(String baseName) {
        String nickname = baseName;
        int suffix = 2;
        while (memberRepository.existsByNickname(nickname)) {
            nickname = baseName + "_" + suffix++;
        }
        return nickname;
    }
}
