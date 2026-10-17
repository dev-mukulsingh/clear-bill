package com.clearbillsolutions.repository;

import com.clearbillsolutions.entity.BusinessSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessSettingsRepository extends JpaRepository<BusinessSettings, Long> {
    Optional<BusinessSettings> findFirstByOrderByIdAsc();
}
