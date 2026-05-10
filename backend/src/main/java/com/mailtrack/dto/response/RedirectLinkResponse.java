package com.mailtrack.dto.response;

import java.util.List;

public record RedirectLinkResponse(
        String hash, String label, String shortUrl, String originalUrl,
        boolean isPasswordProtected, boolean viewOnce, boolean isExpired, boolean noForwarding,
        int clickCount, List<ClickEventResponse> clickEvents
) {}
