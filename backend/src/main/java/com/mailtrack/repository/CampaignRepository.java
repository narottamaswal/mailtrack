package com.mailtrack.repository;

import com.mailtrack.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, String> {
    List<Campaign> findAllByOwnerEmailOrderByCreatedAtDesc(String email);
}
