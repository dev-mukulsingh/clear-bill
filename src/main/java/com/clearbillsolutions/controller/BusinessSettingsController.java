package com.clearbillsolutions.controller;

import com.clearbillsolutions.dto.BusinessSettingsDTO;
import com.clearbillsolutions.service.BusinessSettingsService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class BusinessSettingsController {

    private final BusinessSettingsService settingsService;

    public BusinessSettingsController(BusinessSettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public ResponseEntity<BusinessSettingsDTO> getSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    @PutMapping
    public ResponseEntity<BusinessSettingsDTO> updateSettings(@Valid @RequestBody BusinessSettingsDTO dto) {
        return ResponseEntity.ok(settingsService.updateSettings(dto));
    }

    @PostMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadLogo(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please select a file to upload."));
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only image files (PNG, JPEG, WebP, SVG) are allowed."));
        }

        String base64 = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(file.getBytes());
        BusinessSettingsDTO current = settingsService.getSettings();
        current.setLogoUrl(base64);
        settingsService.updateSettings(current);

        return ResponseEntity.ok(Map.of("logoUrl", base64));
    }
}
