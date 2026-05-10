package com.mailtrack.dto.response;

import java.util.List;

public record ItemDetailResponse(
        String itemId, String campaignId, String createdAt,
        boolean emailOpenEnabled, String emailOpenUrl, String emailPixelSnippet,
        int openCount, List<OpenEventResponse> openEvents,
        boolean timeTrackerEnabled, String timeTrackerUrl,
        int sessionCount, double avgDurationSeconds, List<TrackerSessionResponse> sessions,
        List<RedirectLinkResponse> redirectLinks
) {}
