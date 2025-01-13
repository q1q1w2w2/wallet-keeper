package com.project.wallet_keeper.security.oauth;

import com.project.wallet_keeper.entity.User;
import com.project.wallet_keeper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static com.project.wallet_keeper.entity.Role.*;

// OAuth 인증을 통해 사용자 정보를 가져온다.
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();

        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2Attribute oAuth2Attribute =
                OAuth2Attribute.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Map<String, Object> userAttribute = oAuth2Attribute.convertToMap();

        String email = (String) userAttribute.get("email");
        Optional<User> findUser = userRepository.findByEmail(email);

        if (findUser.isEmpty()) {
            userAttribute.put("exist", false);
            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(ROLE_USER.toString())),
                    userAttribute, "email"
            );
        }

        userAttribute.put("exist", true);
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(findUser.get().getRole().toString())),
                userAttribute, "email"
        );
    }
}
