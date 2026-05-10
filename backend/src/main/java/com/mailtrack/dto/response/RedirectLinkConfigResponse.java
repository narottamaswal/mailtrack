package com.mailtrack.dto.response;

public record RedirectLinkConfigResponse(
        Long id, String label, String originalUrl,
        boolean isPasswordProtected, boolean viewOnce, boolean noForwarding
) {}
