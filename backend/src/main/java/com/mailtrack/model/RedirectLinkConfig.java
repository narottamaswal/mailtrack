package com.mailtrack.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "redirect_link_configs")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RedirectLinkConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Campaign campaign;

    private String  label;
    private String  originalUrl;

    /** BCrypt hash of password — null means no protection. */
    private String  passwordHash;

    private boolean viewOnce;
    private boolean noForwarding;

    public boolean isPasswordProtected() {
        return passwordHash != null && !passwordHash.isBlank();
    }
}
