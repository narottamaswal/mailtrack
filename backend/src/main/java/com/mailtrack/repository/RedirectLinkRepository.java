package com.mailtrack.repository;

import com.mailtrack.model.RedirectLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RedirectLinkRepository extends JpaRepository<RedirectLink, String> {
    Optional<RedirectLink> findByHashAndItemItemId(String hash, String itemId);
}
