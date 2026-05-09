package com.phyex.oauth2tokentestwithpkcefx.utils;


import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.pkce.CodeChallenge;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;

import java.net.URI;

public class AuthorizationCodePKCEUtil {

    public static CodeVerifier getCodeVerifier() {
        return new CodeVerifier();
    }

    public static CodeChallenge getCodeChallenge(CodeVerifier verifier) {
        return CodeChallenge.compute(CodeChallengeMethod.S256, verifier);
    }

    public static String getAuth(CodeChallenge codeChallenge) {
        return new StringBuilder()
                .append(AuthConfig.AUTH)
                .append("?client_id=").append(AuthConfig.clientId)
                .append("&response_type=code")
                .append("&scope=").append(AuthConfig.scope)
                .append("&code_challenge=").append(codeChallenge)
                .append("&code_challenge_method=S256")
                .append("&redirect_uri=").append(AuthConfig.callbackUrl)
                .toString();
    }

    public static TokenResponse getToken(String code, CodeVerifier codeVerifier) {
        ClientID clientID = new ClientID(AuthConfig.clientId);
        URI tokenEndPoint = URI.create(AuthConfig.TOKEN);
        URI callbackURI = URI.create(AuthConfig.callbackUrl);

        AuthorizationCode authorizationCode = new AuthorizationCode(code);
        AuthorizationGrant authorizationGrant = new AuthorizationCodeGrant(authorizationCode, callbackURI, codeVerifier);
        TokenRequest tokenRequest = new TokenRequest(tokenEndPoint, clientID, authorizationGrant);
        try {
            TokenResponse tokenResponse = TokenResponse.parse(tokenRequest.toHTTPRequest().send());
            return tokenResponse;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
//        String payload = new StringBuilder()
//                .append("grant_type=authorization_code")
//                .append("&client_id=").append(AuthConfig.clientId)
//                .append("&code=").append(code)
//                .append("&code_verifier=").append(codeVerifier)
//                .append("&redirect_uri=").append(AuthConfig.callbackUrl)
//                .toString();
    }

}
