package com.mailtrack.controller;

import com.mailtrack.dto.request.VerifyPasswordRequest;
import com.mailtrack.dto.response.*;
import com.mailtrack.service.ItemService;
import com.mailtrack.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/campaigns/{campaignId}/items")
public class ItemController {

    private final ItemService service;

    /** POST /api/campaigns/{cid}/items — one-click create item */
    @PostMapping
    public ResponseEntity<?> create(@PathVariable String campaignId, OAuth2AuthenticationToken auth) {
        try { return ResponseEntity.status(HttpStatus.CREATED).body(service.createItem(campaignId)); }
        catch (NoSuchElementException e) { return ResponseEntity.status(404).body(Map.of("error", e.getMessage())); }
    }

    /** GET /api/campaigns/{cid}/items/{iid} */
    @GetMapping("/{itemId}")
    public ResponseEntity<?> get(@PathVariable String campaignId, @PathVariable String itemId,OAuth2AuthenticationToken auth) {
        try {
            return ResponseEntity.ok(service.getItemDetail(campaignId, itemId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    /** DELETE /api/campaigns/{cid}/items/{iid} */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> delete(@PathVariable String campaignId, @PathVariable String itemId,OAuth2AuthenticationToken auth) {
        try { service.deleteItem(campaignId, itemId); return ResponseEntity.noContent().build(); }
        catch (NoSuchElementException e) { return ResponseEntity.status(404).body(Map.of("error", e.getMessage())); }
    }

    /** GET /api/campaigns/{cid}/items/{iid}/r/{hash}/info */
    @GetMapping("/{itemId}/r/{hash}/info")
    public ResponseEntity<?> linkInfo(
            @PathVariable String campaignId, @PathVariable String itemId, @PathVariable String hash) {
        try { return ResponseEntity.ok(service.getLinkInfo(campaignId, itemId, hash)); }
        catch (NoSuchElementException e) { return ResponseEntity.status(404).body(Map.of("error", e.getMessage())); }
    }

    /** POST /api/campaigns/{cid}/items/{iid}/r/{hash}/verify */
    @PostMapping("/{itemId}/r/{hash}/verify")
    public ResponseEntity<?> verify(
            @PathVariable String campaignId, @PathVariable String itemId, @PathVariable String hash,
            @Valid @RequestBody VerifyPasswordRequest req, HttpServletRequest httpRequest) {
        try {
            String ip = IpUtil.getClientIp(httpRequest);
            String ua = httpRequest.getHeader("User-Agent");
            return ResponseEntity.ok(service.verifyPassword(campaignId, itemId, hash, req, ip, ua != null ? ua : ""));
        } catch (NoSuchElementException e) { return ResponseEntity.status(404).body(Map.of("error", e.getMessage())); }
    }
}
