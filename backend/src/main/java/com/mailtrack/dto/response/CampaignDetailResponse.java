package com.mailtrack.dto.response;

import java.util.List;

public record CampaignDetailResponse(
        String campaignId, String name, String createdAt,
        boolean emailOpenEnabled, boolean timeTrackerEnabled,
        List<RedirectLinkConfigResponse> redirectLinkConfigs,
        List<ItemSummaryResponse> items
) {}
