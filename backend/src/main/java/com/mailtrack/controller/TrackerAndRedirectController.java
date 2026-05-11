package com.mailtrack.controller;

import com.mailtrack.config.ApplicationConstants;
import com.mailtrack.model.TrackerSession;
import com.mailtrack.service.ItemService;
import com.mailtrack.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

@Slf4j
@RestController
public class TrackerAndRedirectController {

    private final ItemService service;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public TrackerAndRedirectController(ItemService service) { this.service = service; }

    /** GET /r/{campaignId}/{itemId}/{hash} */
    @GetMapping("/r/{campaignId}/{itemId}/{hash}")
    public ResponseEntity<?> redirect(
            @PathVariable String campaignId,
            @PathVariable String itemId,
            @PathVariable String hash,
            HttpServletRequest req) {

        String ip = IpUtil.getClientIp(req);
        String ua = req.getHeader("User-Agent");
        try {
            var result = service.processRedirect(campaignId, itemId, hash, ip, ua != null ? ua : "");
            return switch (result.outcome()) {
                case REDIRECT -> ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(result.url()))
                        .header(HttpHeaders.CACHE_CONTROL, "no-store").build();
                case NEEDS_PASSWORD -> ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(frontendUrl + "/verify/" + campaignId + "/" + itemId + "/" + hash))
                        .build();
                case EXPIRED -> ResponseEntity.status(HttpStatus.GONE)
                        .contentType(MediaType.TEXT_HTML)
                        .body("<html><body style='font-family:sans-serif;text-align:center;padding:60px'>" +
                              "<h2>Link Expired</h2><p>This single-use link has already been used.</p></body></html>");
            };
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<html><body style='font-family:sans-serif;text-align:center;padding:60px'>" +
                          "<h2>Link Not Found</h2></body></html>");
        }
    }

    @GetMapping("/pixel/{campaignId}/{itemId}/img.png")
    public ResponseEntity<byte[]> pixel(
            @PathVariable String campaignId, @PathVariable String itemId,
            HttpServletRequest req, HttpServletResponse res) throws Exception {
        String userAgent = req.getHeader("User-Agent");
        String ip = IpUtil.getClientIp(req);

        service.recordEmailOpen(campaignId, itemId, ip, userAgent != null ?userAgent : "");
        byte[] image = generateMImage();
        log.info("Image: {}", Arrays.toString(image));
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }

    private byte[] generateMImage() {
        try {
            int width = 14, height = 14;
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            // Transparent background
            g.setColor(new Color(0, 0, 0, 0));
            g.fillRect(0, 0, width, height);

            // Smooth rendering
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString("M", 1, 11);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            return baos.toByteArray();

        } catch (Exception e) {
            return Base64.getDecoder().decode(
                    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="
            );
        }
    }

    @GetMapping("/tracker/{campaignId}/{itemId}/img.png")
    public void stream(
            @PathVariable String campaignId,
            @PathVariable String itemId,
            HttpServletRequest req,
            HttpServletResponse res) {
        String userAgent = req.getHeader("User-Agent");
        String ip = IpUtil.getClientIp(req);
        TrackerSession session;
        try {
            session = service.createTrackerSession(campaignId, itemId, ip);
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Instant start = session.getStartedAt();
        res.setContentType("image/gif");
        res.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, private");
        res.setHeader(HttpHeaders.PRAGMA, "no-cache");
        res.setHeader("Transfer-Encoding", "chunked");

        try {
            OutputStream out = res.getOutputStream();
            out.write(ApplicationConstants.GIF_OPEN);
            out.flush();

            // Keep alive: write a comment block every 5 seconds
            // Virtual threads (Java 21) make this sleep safe at scale
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(5_000);
                out.write(ApplicationConstants.KEEP_ALIVE);
                out.flush();
            }
        } catch (IOException e) {
            // Normal path — client closed the connection (email closed / tab closed)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            long durationSeconds = Duration.between(start, Instant.now()).getSeconds();
            service.endTrackerSession(session.getSessionId(), durationSeconds);
        }
    }
}
