package com.mailtrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RedirectLinkConfigRequest(
        String label,
        @NotBlank @Pattern(regexp = "https?://.+", message = "Must start with http:// or https://")
        String originalUrl,
        String  password,
        boolean viewOnce,
        boolean noForwarding
) {}
