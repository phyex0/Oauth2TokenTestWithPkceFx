package com.phyex.oauth2tokentestwithpkcefx.pages;

import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.pkce.CodeChallenge;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.phyex.oauth2tokentestwithpkcefx.HelloApplication;
import com.phyex.oauth2tokentestwithpkcefx.utils.AuthConfig;
import com.phyex.oauth2tokentestwithpkcefx.utils.AuthorizationCodePKCEUtil;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.net.CookieHandler;
import java.net.CookieManager;

public class PageOne extends BorderPane {

    public PageOne(HelloApplication app) {
        // 1. Force a fresh cookie store for this instance
        CookieHandler.setDefault(new CookieManager());

        CodeVerifier codeVerifier = AuthorizationCodePKCEUtil.getCodeVerifierWithNimbus();
        CodeChallenge codeChallenge = AuthorizationCodePKCEUtil.getCodeChallengeWithNimbus(codeVerifier);

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        webEngine.locationProperty().addListener((obs, oldLocation, newLocation) -> {
            if (newLocation.startsWith(AuthConfig.callbackUrl)) {
                String authCode = extractParam(newLocation, "code");
                System.out.println("Captured Auth Code: " + authCode);

                TokenResponse token = AuthorizationCodePKCEUtil.getTokenWithNimbus(authCode, codeVerifier);

                if (token != null && token.indicatesSuccess()) {
                    // Use the SDK's built-in string conversion to avoid Jackson module issues
                    String finalTokenResponse = token.toSuccessResponse().toHTTPResponse().getContent();

                    javafx.application.Platform.runLater(() -> {
                        app.showPageTwo(finalTokenResponse);
                    });
                } else {
                    System.out.println("Token response is null or indicates failure");
                }
            }
        });

        // 2. Clear history and load a blank state first
        webEngine.getHistory().getEntries().clear();
        webEngine.loadContent("<html></html>");

        // 3. Construct the Auth URL (Ensure getAuth appends &prompt=login)
        String authUrl = AuthorizationCodePKCEUtil.getAuth(codeChallenge);

        // Safety check: if your Util doesn't have it, append it manually here
        if (!authUrl.contains("prompt=login")) {
            authUrl += "&prompt=login";
        }

        webEngine.load(authUrl);
        setCenter(webView);
    }

    private String extractParam(String url, String param) {
        try {
            // Updated to handle potential nulls or missing query strings
            if (url == null || !url.contains("?")) return null;
            String query = url.split("\\?")[1];
            return java.util.Arrays.stream(query.split("&"))
                    .filter(s -> s.startsWith(param + "="))
                    .map(s -> s.split("=")[1])
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}