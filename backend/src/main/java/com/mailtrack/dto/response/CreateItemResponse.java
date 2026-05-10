package com.mailtrack.dto.response;

import java.util.List;

public record CreateItemResponse(
        String itemId, String campaignId, String createdAt,
        String emailOpenUrl, String emailPixelSnippet,
        String timeTrackerUrl,
        List<RedirectLinkSimpleResponse> redirectLinks
) {}
