package com.mailtrack.dto.response;

public record TrackerSessionResponse(
        String sessionId, String startedAt, Long durationSeconds, String ip
) {}
