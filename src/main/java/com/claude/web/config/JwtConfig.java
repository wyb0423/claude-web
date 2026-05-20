package com.claude.web.config;

import lombok.Getter;

@Getter
public class JwtConfig {

    public JwtConfig(String base64Secret) {
        this.base64Secret = base64Secret;
    }
    private final String base64Secret;
//    private String base64Secret = "MDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjY=";

    // jwt
    private final String clientId = "098f6bcd4621d373cade4e832627b4f6";
    private final String name = "feishu_application";
    private final int expiresSecond = 43200;

    // soa
    private final int shortExpiresSecond = 3600;
    private final String soaBase64Secret = "MIIEpQIBAAKCAQEAwgVUUOZFLW+mcGQ6K+e3er4sIKq6W7PPKeodYjNcXCUzB7XO";
}
