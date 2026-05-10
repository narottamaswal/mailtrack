package com.mailtrack.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name = "click_events")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ClickEvent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "link_hash", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private RedirectLink link;

    private Instant timestamp;
    private String  ip;

    @Column(length = 512)
    private String userAgent;
}
