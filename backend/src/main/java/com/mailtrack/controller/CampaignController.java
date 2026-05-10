package com.mailtrack.controller;

import com.mailtrack.config.AuthUtil;
import com.mailtrack.dto.request.CreateCampaignRequest;
import com.mailtrack.dto.response.CampaignDetailResponse;
import com.mailtrack.dto.response.CampaignSummaryResponse;
import com.mailtrack.service.CampaignService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    private final CampaignService service;
    public CampaignController(CampaignService service) { this.service = service; }

    /** POST /api/campaigns */
    @PostMapping
    public ResponseEntity<CampaignDetailResponse> create(
            @Valid @RequestBody CreateCampaignRequest req,OAuth2AuthenticationToken auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                service.createCampaign(req,AuthUtil.getEmail(auth))
        );
    }

    /** GET /api/campaigns */
    @GetMapping
    public ResponseEntity<List<CampaignSummaryResponse>> list(OAuth2AuthenticationToken auth) {
        String ownerEmail = AuthUtil.getEmail(auth);
        return ResponseEntity.ok(service.listCampaigns(ownerEmail));
    }

    /** GET /api/campaigns/{campaignId} */
    @GetMapping("/{campaignId}")
    public ResponseEntity<?> get(@PathVariable String campaignId,OAuth2AuthenticationToken auth) {
        try { return ResponseEntity.ok(service.getCampaign(campaignId)); }
        catch (NoSuchElementException e) { return ResponseEntity.status(404).body(Map.of("error", e.getMessage())); }
    }

    /** DELETE /api/campaigns/{campaignId} */
    @DeleteMapping("/{campaignId}")
    public ResponseEntity<?> delete(@PathVariable String campaignId,OAuth2AuthenticationToken auth) {
        try { service.deleteCampaign(campaignId); return ResponseEntity.noContent().build(); }
        catch (NoSuchElementException e) { return ResponseEntity.status(404).body(Map.of("error", e.getMessage())); }
    }
}
