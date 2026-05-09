package com.phyex.oauth2tokentestwithpkcefx.utils;


public class AuthConfig {
    static final String clientId = "client-pkce";
    static final String scope = "openid";
    public static final String callbackUrl = "http://localhost:8081/callback";
    static final String AUTH = "http://localhost:8090/realms/spring-oauth-test-realm/protocol/openid-connect/auth";
    static final String TOKEN = "http://localhost:8090/realms/spring-oauth-test-realm/protocol/openid-connect/token";
}
