# OAuth2 Token Test with PKCE (JavaFX)

This is a premium JavaFX application designed to demonstrate a secure **OAuth2 Authorization Code Flow with PKCE** (Proof Key for Code Exchange) using a **Keycloak** identity provider.

## 🚀 Features

* **Secure PKCE Implementation**: Uses the Nimbus OAuth2 SDK to generate code verifiers and challenges.
* **JavaFX WebView Integration**: Handles the full login handshake within a native desktop component.
* **Session Management**: Automatically clears cookies and forces a fresh login using the `prompt=login` parameter.
* **Modular Architecture**: Fully compatible with the Java Module System (Project Jigsaw).

---

## 🛠 Prerequisites

1. **JDK 17+** (Recommended: Gluon or BellSoft Liberica with JavaFX bundled).
2. **Keycloak Instance** running on `localhost:8090`.
3. **Maven** for dependency management.

---

## ⚙️ Keycloak Configuration

To make this app work, configure a Client in your Keycloak Realm with the following settings:

| Setting | Value |
| --- | --- |
| **Client ID** | `client-pkce` |
| **Client Authentication** | `Off` (Public Client) |
| **Authorization Code Flow** | `Enabled` |
| **Valid Redirect URIs** | `http://localhost:8081/callback` |
| **Web Origins** | `*` (or `+` for development) |
| **PKCE Challenge Method** | `S256` |

---

## 📦 Installation & Setup

### 1. Module Info

Ensure your `module-info.java` includes the necessary requirements:

```java
module com.phyex.oauth2tokentestwithpkcefx {
    requires javafx.controls;
    requires javafx.web;
    requires com.nimbusds.oauth2.sdk;
    requires com.fasterxml.jackson.databind;
    // Add other UI libraries as needed
}

```

### 2. Running the App

Run the application using the Maven wrapper:

```bash
mvn javafx:run

```

---

## 📖 How it Works

1. **Page One**: Initializes a `WebView` and generates a `CodeVerifier`. It loads the Keycloak Auth URL with a `code_challenge`.
2. **Interception**: A listener monitors the `WebView` location. When it detects the `redirect_uri`, it extracts the `code` parameter.
3. **Token Exchange**: The app makes a background POST request to Keycloak’s `/token` endpoint, sending the `authCode` and the original `code_verifier`.
4. **Page Two**: Upon success, the raw JSON response (containing the `access_token`) is passed to the second screen and displayed in a `TextArea`.

---

## ⚠️ Troubleshooting

* **Invalid `redirect_uri**`: Ensure the URL in your Java code matches the Keycloak "Valid Redirect URIs" exactly, including port and path.
* **Module Access Errors**: If you encounter errors accessing internal JSON classes, use `.toHTTPResponse().getContent()` to handle the raw response string.
* **Sticky Sessions**: The app resets the `CookieHandler` on startup to ensure a clean login state.

---

### Project Structure

* `PageOne.java`: Authentication and WebView logic.
* `PageTwo.java`: Success dashboard and token display.
* `AuthorizationCodePKCEUtil.java`: Utility for generating PKCE values and calling the token endpoint.

---
