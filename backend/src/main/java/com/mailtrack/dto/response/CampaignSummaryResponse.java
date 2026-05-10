package com.mailtrack.dto.response;

import java.util.List;

// Campaign list item (dashboard)
public record CampaignSummaryResponse(
        String campaignId, String name, String createdAt,
        boolean emailOpenEnabled, boolean timeTrackerEnabled,
        int redirectLinksCount, int itemCount
) {}
