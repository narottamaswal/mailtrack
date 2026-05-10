package com.mailtrack.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name = "open_events")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OpenEvent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Item item;

    private Instant timestamp;
    private String  ip;

    @Column(length = 512)
    private String userAgent;
}
