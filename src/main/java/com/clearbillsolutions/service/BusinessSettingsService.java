package com.clearbillsolutions.service;

import com.clearbillsolutions.dto.BusinessSettingsDTO;
import com.clearbillsolutions.entity.BusinessSettings;
import com.clearbillsolutions.exception.BadRequestException;
import com.clearbillsolutions.repository.BusinessSettingsRepository;
import com.clearbillsolutions.util.GstUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BusinessSettingsService {

    private final BusinessSettingsRepository settingsRepository;

    public BusinessSettingsService(BusinessSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Transactional(readOnly = true)
    public BusinessSettingsDTO getSettings() {
        BusinessSettings settings = getOrCreateSettings();
        return toDTO(settings);
    }

    @Transactional
    public BusinessSettingsDTO updateSettings(BusinessSettingsDTO dto) {
        if (dto.getGstin() != null && !dto.getGstin().trim().isEmpty()) {
            String cleanGstin = dto.getGstin().trim().toUpperCase();
            if (!GstUtil.isValidGstin(cleanGstin)) {
                throw new BadRequestException("Invalid GSTIN format. Business GSTIN must be a 15-character valid code (e.g., 27AABCU9603R1ZM).");
            }
            dto.setGstin(cleanGstin);
        }

        BusinessSettings settings = getOrCreateSettings();

        settings.setCompanyName(dto.getCompanyName().trim());
        settings.setEmail(dto.getEmail());
        settings.setPhone(dto.getPhone());
        settings.setAddress(dto.getAddress());
        settings.setCity(dto.getCity());
        settings.setState(dto.getState());
        settings.setStateCode(dto.getStateCode());
        settings.setPostalCode(dto.getPostalCode());
        settings.setCountry(dto.getCountry());
        settings.setTaxId(dto.getTaxId());
        settings.setGstin(dto.getGstin());
        settings.setUpiId(dto.getUpiId());
        settings.setPayeeName(dto.getPayeeName());

        if (dto.getCurrencySymbol() != null && !dto.getCurrencySymbol().trim().isEmpty()) {
            settings.setCurrencySymbol(dto.getCurrencySymbol().trim());
        }
        if (dto.getLogoUrl() != null) {
            settings.setLogoUrl(dto.getLogoUrl());
        }
        settings.setNotesDefault(dto.getNotesDefault());

        BusinessSettings saved = settingsRepository.save(settings);
        return toDTO(saved);
    }

    public BusinessSettings getOrCreateSettings() {
        return settingsRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    BusinessSettings defaultSettings = BusinessSettings.builder()
                            .companyName("ClearBill Solutions")
                            .email("billing@clearbillsolutions.com")
                            .phone("+91 98765 43210")
                            .address("42 Cedar Grove Avenue, MG Road")
                            .city("Bengaluru")
                            .state("Karnataka")
                            .stateCode("29")
                            .postalCode("560001")
                            .country("India")
                            .taxId("TAX-IN-98234")
                            .gstin("29AABCH1234F1Z8")
                            .upiId("clearbill@upi")
                            .payeeName("ClearBill Solutions")
                            .currencySymbol("₹")
                            .logoUrl("")
                            .notesDefault("Thank you for your business! Payment is appreciated within 15 days via UPI or Net Banking.")
                            .build();
                    return settingsRepository.save(defaultSettings);
                });
    }

    public BusinessSettingsDTO toDTO(BusinessSettings settings) {
        return BusinessSettingsDTO.builder()
                .id(settings.getId())
                .companyName(settings.getCompanyName())
                .email(settings.getEmail())
                .phone(settings.getPhone())
                .address(settings.getAddress())
                .city(settings.getCity())
                .state(settings.getState())
                .stateCode(settings.getStateCode())
                .postalCode(settings.getPostalCode())
                .country(settings.getCountry())
                .taxId(settings.getTaxId())
                .gstin(settings.getGstin())
                .upiId(settings.getUpiId())
                .payeeName(settings.getPayeeName())
                .currencySymbol(settings.getCurrencySymbol())
                .logoUrl(settings.getLogoUrl())
                .notesDefault(settings.getNotesDefault())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }
}
