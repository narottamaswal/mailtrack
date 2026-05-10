package com.mailtrack.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "campaigns")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Campaign {

    @Id
    @Column(name = "campaign_id", length = 16)
    private String campaignId;

    private String  name;
    private Instant createdAt;

    private boolean emailOpenEnabled;
    private boolean timeTrackerEnabled;

    @Column(nullable = false)
    private String ownerEmail;

    private String ownerName;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<RedirectLinkConfig> redirectLinkConfigs = new ArrayList<>();

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Item> items = new ArrayList<>();
}
