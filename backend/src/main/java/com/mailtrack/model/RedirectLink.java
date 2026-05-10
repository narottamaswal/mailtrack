package com.mailtrack.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "redirect_links")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RedirectLink {

    @Id
    @Column(length = 12)
    private String hash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Item item;

    private String  label;
    private String  originalUrl;
    private String  passwordHash;
    private boolean viewOnce;
    private boolean viewOnceConsumed;
    private boolean noForwarding;

    @OneToMany(mappedBy = "link", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<ClickEvent> clickEvents = new ArrayList<>();

    public boolean isPasswordProtected() { return passwordHash != null && !passwordHash.isBlank(); }
    public boolean isExpired()           { return viewOnce && viewOnceConsumed; }
    public int     getClickCount()       { return clickEvents.size(); }
}
