package com.mailtrack.util;

import jakarta.servlet.http.HttpServletRequest;

public final class IpUtil {
    private IpUtil() {}

    public static String getClientIp(HttpServletRequest req) {
        for (String h : new String[]{"X-Forwarded-For","X-Real-IP","Proxy-Client-IP"}) {
            String ip = req.getHeader(h);
            if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip))
                return ip.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }
}
