package com.mailtrack.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name = "tracker_sessions")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TrackerSession {

    @Id
    @Column(length = 40)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Item item;

    private Instant startedAt;
    private Long    durationSeconds; // null until session ends
    private String  ip;
}
