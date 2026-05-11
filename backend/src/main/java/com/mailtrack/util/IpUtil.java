package com.mailtrack.util;

import jakarta.servlet.http.HttpServletRequest;

public final class IpUtil {
    private IpUtil() {
    }

    public static String getClientIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        return (forwarded != null) ? forwarded.split(",")[0].trim() : req.getRemoteAddr();
    }
}
