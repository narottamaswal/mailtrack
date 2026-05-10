package com.mailtrack.service;

import com.mailtrack.dto.request.CreateCampaignRequest;
import com.mailtrack.dto.response.*;
import com.mailtrack.model.*;
import com.mailtrack.repository.CampaignRepository;
import com.mailtrack.repository.ItemRepository;
import com.mailtrack.util.IdGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CampaignService {

    private final CampaignRepository campaignRepo;
    private final ItemRepository     itemRepo;
    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    public CampaignService(CampaignRepository campaignRepo, ItemRepository itemRepo) {
        this.campaignRepo = campaignRepo;
        this.itemRepo     = itemRepo;
    }

    public CampaignDetailResponse createCampaign(CreateCampaignRequest req,String email) {
        Campaign campaign = Campaign.builder()
                .ownerEmail(email)
                .campaignId(IdGenerator.campaignId())
                .name(req.name() != null && !req.name().isBlank() ? req.name() : null)
                .createdAt(Instant.now())
                .emailOpenEnabled(req.emailOpenLink())
                .timeTrackerEnabled(req.timeTrackingLink())
                .build();

        req.redirectLinks().forEach(r -> {
            String hash = (r.password() != null && !r.password().isBlank())
                    ? bcrypt.encode(r.password()) : null;
            RedirectLinkConfig cfg = RedirectLinkConfig.builder()
                    .campaign(campaign)
                    .label(r.label() != null && !r.label().isBlank() ? r.label() : "Link")
                    .originalUrl(r.originalUrl())
                    .passwordHash(hash)
                    .viewOnce(r.viewOnce())
                    .noForwarding(r.noForwarding())
                    .build();
            campaign.getRedirectLinkConfigs().add(cfg);
        });

        campaignRepo.save(campaign);
        return toCampaignDetail(campaign, List.of());
    }

    // ── List Campaigns ────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CampaignSummaryResponse> listCampaigns(String email) {
        return campaignRepo.findAllByOwnerEmailOrderByCreatedAtDesc(email).stream()
                .map(c -> new CampaignSummaryResponse(
                        c.getCampaignId(), c.getName(), c.getCreatedAt().toString(),
                        c.isEmailOpenEnabled(), c.isTimeTrackerEnabled(),
                        c.getRedirectLinkConfigs().size(),
                        c.getItems().size()
                )).toList();
    }

    // ── Get Campaign Detail ────────────────────────────────────────

    @Transactional(readOnly = true)
    public CampaignDetailResponse getCampaign(String campaignId) {
        Campaign campaign = requireCampaign(campaignId);
        List<ItemSummaryResponse> items = itemRepo
                .findByCampaignCampaignIdOrderByCreatedAtDesc(campaignId)
                .stream().map(this::toItemSummary).toList();
        return toCampaignDetail(campaign, items);
    }

    // ── Delete Campaign ────────────────────────────────────────────

    public void deleteCampaign(String campaignId) {
        if (!campaignRepo.existsById(campaignId))
            throw new NoSuchElementException("Campaign not found: " + campaignId);
        campaignRepo.deleteById(campaignId);
    }

    // ── Helpers ────────────────────────────────────────────────────

    private CampaignDetailResponse toCampaignDetail(Campaign c, List<ItemSummaryResponse> items) {
        List<RedirectLinkConfigResponse> configs = c.getRedirectLinkConfigs().stream()
                .map(r -> new RedirectLinkConfigResponse(
                        r.getId(), r.getLabel(), r.getOriginalUrl(),
                        r.isPasswordProtected(), r.isViewOnce(), r.isNoForwarding()))
                .toList();
        return new CampaignDetailResponse(
                c.getCampaignId(), c.getName(), c.getCreatedAt().toString(),
                c.isEmailOpenEnabled(), c.isTimeTrackerEnabled(),
                configs, items
        );
    }

    ItemSummaryResponse toItemSummary(Item item) {
        double avg = item.getTrackerSessions().stream()
                .filter(s -> s.getDurationSeconds() != null)
                .mapToLong(TrackerSession::getDurationSeconds)
                .average().orElse(0.0);
        return new ItemSummaryResponse(
                item.getItemId(), item.getCampaign().getCampaignId(),
                item.getCreatedAt().toString(),
                item.getOpenEvents().size(),
                item.getTrackerSessions().size(), avg,
                item.getRedirectLinks().size()
        );
    }

    Campaign requireCampaign(String campaignId) {
        return campaignRepo.findById(campaignId)
                .orElseThrow(() -> new NoSuchElementException("Campaign not found: " + campaignId));
    }
}
