package com.mailtrack.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "witems")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Item {

    @Id
    @Column(name = "item_id", length = 16)
    private String itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Campaign campaign;

    private Instant createdAt;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<OpenEvent> openEvents = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<TrackerSession> trackerSessions = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<RedirectLink> redirectLinks = new ArrayList<>();
}
