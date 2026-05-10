package com.mailtrack.config;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public class AuthUtil {
    public static final String X_DEVICE_FINGERPRINT = "X-Device-Fingerprint";
    public static final String X_POST_PASSWORD = "X-Post-Password";
    public static String getEmail(OAuth2AuthenticationToken auth) {
        if(auth==null) return null;
        return auth.getPrincipal().getAttribute("email");
    }

    public static String getName(OAuth2AuthenticationToken auth) {
        if(auth==null) return null;
        return auth.getPrincipal().getAttribute("name");
    }
}
