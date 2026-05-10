package com.mailtrack.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public record CreateCampaignRequest(
        String name,
        @NotNull boolean emailOpenLink,
        @NotNull boolean timeTrackingLink,
        @NotNull @Valid List<RedirectLinkConfigRequest> redirectLinks
) {
    public CreateCampaignRequest {
        if (redirectLinks == null) redirectLinks = new ArrayList<>();
    }
}
