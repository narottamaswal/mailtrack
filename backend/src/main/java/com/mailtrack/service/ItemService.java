package com.mailtrack.service;

import com.mailtrack.config.ApplicationConstants;
import com.mailtrack.dto.request.VerifyPasswordRequest;
import com.mailtrack.dto.response.*;
import com.mailtrack.model.*;
import com.mailtrack.repository.ItemRepository;
import com.mailtrack.repository.RedirectLinkRepository;
import com.mailtrack.repository.TrackerSessionRepository;
import com.mailtrack.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository          itemRepo;
    private final RedirectLinkRepository  linkRepo;
    private final TrackerSessionRepository sessionRepo;
    private final CampaignService         campaignService;
    private final BCryptPasswordEncoder   bcrypt = new BCryptPasswordEncoder();

    @Value("${app.base-url}")
    private String baseUrl;

    public CreateItemResponse createItem(String campaignId) {
        Campaign campaign = campaignService.requireCampaign(campaignId);
        Item item = Item.builder()
                .itemId(IdGenerator.itemId())
                .campaign(campaign)
                .createdAt(Instant.now())
                .build();

        campaign.getRedirectLinkConfigs().forEach(cfg -> {
            RedirectLink link = RedirectLink.builder()
                    .hash(IdGenerator.linkHash())
                    .item(item)
                    .label(cfg.getLabel())
                    .originalUrl(cfg.getOriginalUrl())
                    .passwordHash(cfg.getPasswordHash())
                    .viewOnce(cfg.isViewOnce())
                    .viewOnceConsumed(false)
                    .noForwarding(cfg.isNoForwarding())
                    .build();
            item.getRedirectLinks().add(link);
        });

        itemRepo.save(item);

        String iid  = item.getItemId();
        String cid  = campaignId;
        String pixelUrl   = campaign.isEmailOpenEnabled()    ? baseUrl + "/pixel/"   + cid + "/" + iid : null;
        String trackerUrl = campaign.isTimeTrackerEnabled()  ? baseUrl + "/tracker/" + cid + "/" + iid : null;
        String snippet    = pixelUrl != null
                ? "<img src=\"" + pixelUrl + "\" width=\"1\" height=\"1\" alt=\"\" style=\"display:none;border:0;\">"
                : null;

        List<RedirectLinkSimpleResponse> links = item.getRedirectLinks().stream()
                .map(l -> new RedirectLinkSimpleResponse(
                        l.getHash(), l.getLabel(),
                        baseUrl + "/r/" + cid + "/" + iid + "/" + l.getHash(),
                        l.isPasswordProtected(), l.isViewOnce(), l.isNoForwarding()))
                .toList();

        return new CreateItemResponse(iid, cid, item.getCreatedAt().toString(),
                pixelUrl, snippet, trackerUrl, links);
    }

    // ── Get Item Detail ────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ItemDetailResponse getItemDetail(String campaignId, String itemId) {
        Item item = requireItem(campaignId, itemId);
        Campaign c = item.getCampaign();
        String cid = c.getCampaignId();
        String iid = item.getItemId();

        String pixelUrl   = c.isEmailOpenEnabled()   ? baseUrl + "/pixel/"   + cid + "/" + iid +"/img.png": null;
        String trackerUrl = c.isTimeTrackerEnabled()  ? baseUrl + "/tracker/" + cid + "/" + iid +"/img.png": null;
        String snippet    = pixelUrl != null
                ? "<img src=\"" + pixelUrl + "\" width=\"1\" height=\"1\" alt=\"\" style=\"display:none;border:0;\">"
                : null;

        List<OpenEventResponse> openEvents = item.getOpenEvents().stream()
                .map(e -> new OpenEventResponse(e.getTimestamp().toString(), e.getIp(), e.getUserAgent()))
                .toList();

        List<TrackerSessionResponse> sessions = item.getTrackerSessions().stream()
                .map(s -> new TrackerSessionResponse(s.getSessionId(), s.getStartedAt().toString(),
                        s.getDurationSeconds(), s.getIp()))
                .toList();

        double avg = item.getTrackerSessions().stream()
                .filter(s -> s.getDurationSeconds() != null)
                .mapToLong(TrackerSession::getDurationSeconds)
                .average().orElse(0.0);

        List<RedirectLinkResponse> links = item.getRedirectLinks().stream()
                .map(l -> {
                    List<ClickEventResponse> clicks = l.getClickEvents().stream()
                            .map(ce -> new ClickEventResponse(ce.getTimestamp().toString(), ce.getIp(), ce.getUserAgent()))
                            .toList();
                    return new RedirectLinkResponse(
                            l.getHash(), l.getLabel(),
                            baseUrl + "/r/" + cid + "/" + iid + "/" + l.getHash(),
                            l.getOriginalUrl(), l.isPasswordProtected(),
                            l.isViewOnce(), l.isExpired(), l.isNoForwarding(),
                            l.getClickCount(), clicks);
                }).toList();

        return new ItemDetailResponse(
                iid, cid, item.getCreatedAt().toString(),
                c.isEmailOpenEnabled(), pixelUrl, snippet,
                item.getOpenEvents().size(), openEvents,
                c.isTimeTrackerEnabled(), trackerUrl,
                item.getTrackerSessions().size(), avg, sessions,
                links);
    }

    // ── Delete Item ────────────────────────────────────────────────

    public void deleteItem(String campaignId, String itemId) {
        Item item = requireItem(campaignId, itemId);
        itemRepo.delete(item);
    }

    public void recordEmailOpen(String campaignId, String itemId, String ip, String ua) {
        itemRepo.findById(itemId).ifPresent(item -> {

            if (item.getCampaign().getCampaignId().equals(campaignId)
                    && item.getCampaign().isEmailOpenEnabled()) {
                item.getOpenEvents().add(OpenEvent.builder()
                        .item(item).timestamp(Instant.now()).ip(ip).userAgent(ua).build());
                itemRepo.save(item);
            }
        });
    }

    public TrackerSession createTrackerSession(String campaignId, String itemId, String ip) {
        Item item = requireItem(campaignId, itemId);
        if (!item.getCampaign().isTimeTrackerEnabled())
            throw new IllegalStateException("Time tracking not enabled");

        TrackerSession session = TrackerSession.builder()
                .sessionId(IdGenerator.sessionId())
                .item(item)
                .startedAt(Instant.now())
                .ip(ip)
                .build();
        item.getTrackerSessions().add(session);
        itemRepo.save(item);
        return session;
    }

    public void endTrackerSession(String sessionId, long durationSeconds) {
        sessionRepo.findById(sessionId).ifPresent(s -> {
            s.setDurationSeconds(durationSeconds);
            sessionRepo.save(s);
        });
    }

    // ── Redirect Link ──────────────────────────────────────────────

    public enum RedirectOutcome { REDIRECT, NEEDS_PASSWORD, EXPIRED }
    public record RedirectResult(RedirectOutcome outcome, String url) {}

    @Transactional
    public synchronized RedirectResult processRedirect(
            String campaignId, String itemId, String hash, String ip, String ua) {

        RedirectLink link = requireLink(itemId, hash);
        if (!link.getItem().getCampaign().getCampaignId().equals(campaignId))
            throw new NoSuchElementException("Link not found");

        if (link.isExpired()) return new RedirectResult(RedirectOutcome.EXPIRED, null);
        if (link.isPasswordProtected()) return new RedirectResult(RedirectOutcome.NEEDS_PASSWORD, null);

        logClick(link, ip, ua);
        return new RedirectResult(RedirectOutcome.REDIRECT, link.getOriginalUrl());
    }

    public LinkInfoResponse getLinkInfo(String campaignId, String itemId, String hash) {
        RedirectLink link = requireLink(itemId, hash);
        return new LinkInfoResponse(link.getLabel(), link.isPasswordProtected(),
                link.isExpired(), link.isViewOnce());
    }

    @Transactional
    public VerifyPasswordResponse verifyPassword(
            String campaignId, String itemId, String hash,
            VerifyPasswordRequest req, String ip, String ua) {

        RedirectLink link = requireLink(itemId, hash);
        if (!link.isPasswordProtected())
            return new VerifyPasswordResponse(true, link.getOriginalUrl());

        boolean valid = bcrypt.matches(req.password(), link.getPasswordHash());
        if (valid) logClick(link, ip, ua);
        return new VerifyPasswordResponse(valid, valid ? link.getOriginalUrl() : null);
    }

    // ── Private helpers ────────────────────────────────────────────

    private void logClick(RedirectLink link, String ip, String ua) {
        if (link.isExpired()) return;
        link.getClickEvents().add(ClickEvent.builder()
                .link(link).timestamp(Instant.now()).ip(ip).userAgent(ua).build());
        if (link.isViewOnce()) link.setViewOnceConsumed(true);
        linkRepo.save(link);
    }

    Item requireItem(String campaignId, String itemId) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found: " + itemId));
        if (!item.getCampaign().getCampaignId().equals(campaignId))
            throw new NoSuchElementException("Item not found in campaign");
        return item;
    }

    private RedirectLink requireLink(String itemId, String hash) {
        return linkRepo.findByHashAndItemItemId(hash, itemId)
                .orElseThrow(() -> new NoSuchElementException("Link not found: " + hash));
    }
}
