package com.mailtrack.dto.response;

public record ItemSummaryResponse(
        String itemId, String campaignId, String createdAt,
        int openCount, int sessionCount, double avgDurationSeconds,
        int redirectLinkCount
) {}
