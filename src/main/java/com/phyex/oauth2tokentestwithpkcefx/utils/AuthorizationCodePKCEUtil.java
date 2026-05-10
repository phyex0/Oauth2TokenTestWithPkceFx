package com.phyex.oauth2tokentestwithpkcefx.utils;


import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeChallenge;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Prompt;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthorizationCodePKCEUtil {

    public static CodeVerifier getCodeVerifierWithNimbus() {
        return new CodeVerifier();
    }

    public static CodeChallenge getCodeChallengeWithNimbus(CodeVerifier verifier) {
        return CodeChallenge.compute(CodeChallengeMethod.S256, verifier);
    }

    public static String getAuthWithNimbus(CodeChallenge codeChallenge) {
        try {
            // 1. Initialize Nimbus-specific types
            URI authEndpoint = new URI(AuthConfig.AUTH);
            URI callbackUri = new URI(AuthConfig.callbackUrl);
            ClientID clientID = new ClientID(AuthConfig.clientId);
            Scope scope = Scope.parse(AuthConfig.scope);

            // State is a random string to verify the response later (recommended)
            State state = new State();

            // 2. Construct the OIDC Authentication Request
            AuthenticationRequest request = new AuthenticationRequest.Builder(
                    new ResponseType(ResponseType.Value.CODE),
                    scope,
                    clientID,
                    callbackUri)
                    .endpointURI(authEndpoint)
                    .codeChallenge(codeChallenge, CodeChallengeMethod.S256)
                    .state(state)
                    .prompt(new Prompt(Prompt.Type.LOGIN)) // Forces login screen
                    .build();

            // 3. Generate the final URI for the WebView
            return request.toURI().toString();

        } catch (Exception e) {
            // Log the error and rethrow
            System.err.println("Error building Nimbus Auth URL: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static TokenResponse getTokenWithNimbus(String code, CodeVerifier codeVerifier) {
        try {
            // 1. Prepare the credentials and endpoints
            ClientID clientID = new ClientID(AuthConfig.clientId);
            URI tokenEndPoint = URI.create(AuthConfig.TOKEN);
            URI callbackURI = URI.create(AuthConfig.callbackUrl);

            // 2. Create the Grant (This is where the PKCE "Proof" happens)
            AuthorizationCode authorizationCode = new AuthorizationCode(code);
            AuthorizationGrant codeGrant = new AuthorizationCodeGrant(
                    authorizationCode,
                    callbackURI,
                    codeVerifier
            );

            // 3. Assemble the request
            TokenRequest tokenRequest = new TokenRequest(tokenEndPoint, clientID, codeGrant);

            // 4. Send and Parse
            // Note: .send() is a blocking call. Ensure this is inside your
            // listener logic before switching scenes.
            TokenResponse tokenResponse = TokenResponse.parse(tokenRequest.toHTTPRequest().send());

            if (!tokenResponse.indicatesSuccess()) {
                System.err.println("Token Request Failed: " +
                        tokenResponse.toErrorResponse().getErrorObject().getDescription());
            }

            return tokenResponse;

        } catch (Exception e) {
            System.err.println("Error during Token Exchange: " + e.getMessage());
            return null;
        }
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

    public static String getToken(String authCode, String codeVerifier) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            // 1. Prepare the parameters in a Map
            Map<String, String> params = Map.of(
                    "grant_type", "authorization_code",
                    "client_id", AuthConfig.clientId,
                    "code", authCode,
                    "redirect_uri", AuthConfig.callbackUrl,
                    "code_verifier", codeVerifier // The raw, un-hashed verifier string
            );

            // 2. Convert the Map to a URL-encoded string (form-data)
            String formBody = params.entrySet().stream()
                    .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" +
                            URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));

            // 3. Build the POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AuthConfig.TOKEN))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formBody))
                    .build();

            // 4. Send the request synchronously (or use .sendAsync for non-blocking)
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body(); // Returns the raw JSON string
            } else {
                System.err.println("Error: " + response.statusCode() + " - " + response.body());
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
