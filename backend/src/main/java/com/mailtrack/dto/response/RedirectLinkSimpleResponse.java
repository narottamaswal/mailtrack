package com.mailtrack.dto.response;
public record RedirectLinkSimpleResponse(String hash, String label, String shortUrl, boolean isPasswordProtected, boolean viewOnce, boolean noForwarding) {}
