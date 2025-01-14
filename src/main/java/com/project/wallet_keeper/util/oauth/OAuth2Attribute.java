package com.project.wallet_keeper.util.oauth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@ToString
public class OAuth2Attribute {
    private Map<String, Object> attributes;
    private String attributeKey;
    private String email;
    private String name;
    private String provider;

    static OAuth2Attribute of(String provider, String attributeKey,
                              Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return ofGoogle(provider, attributeKey, attributes);
            default:
                throw new RuntimeException();
        }
    }

    private static OAuth2Attribute ofGoogle(String provider, String attributeKey,
                                            Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                .email((String) attributes.get("email"))
                .name((String) attributes.get("name"))
                .provider(provider)
                .attributes(attributes)
                .attributeKey(attributeKey)
                .build();
    }

    // OAuth2User 객체에 넣기 위해 Map으로 반환
    Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", attributeKey);
        map.put("key", attributeKey);
        map.put("email", email);
        map.put("name", name);
        map.put("provider", provider);

        return map;
    }
}
