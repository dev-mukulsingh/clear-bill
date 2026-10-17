package com.clearbillsolutions.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class BusinessSettingsDTO {
    private Long id;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String stateCode;
    private String postalCode;
    private String country;
    private String taxId;
    private String gstin;
    private String upiId;
    private String payeeName;
    private String currencySymbol;
    private String logoUrl;
    private String notesDefault;
    private LocalDateTime updatedAt;

    public BusinessSettingsDTO() {
    }

    public BusinessSettingsDTO(Long id, String companyName, String email, String phone, String address, String city, String state, String stateCode, String postalCode, String country, String taxId, String gstin, String upiId, String payeeName, String currencySymbol, String logoUrl, String notesDefault, LocalDateTime updatedAt) {
        this.id = id;
        this.companyName = companyName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.stateCode = stateCode;
        this.postalCode = postalCode;
        this.country = country;
        this.taxId = taxId;
        this.gstin = gstin;
        this.upiId = upiId;
        this.payeeName = payeeName;
        this.currencySymbol = currencySymbol;
        this.logoUrl = logoUrl;
        this.notesDefault = notesDefault;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getStateCode() { return stateCode; }
    public void setStateCode(String stateCode) { this.stateCode = stateCode; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    public String getGstin() { return gstin; }
    public void setGstin(String gstin) { this.gstin = gstin; }
    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }
    public String getPayeeName() { return payeeName; }
    public void setPayeeName(String payeeName) { this.payeeName = payeeName; }
    public String getCurrencySymbol() { return currencySymbol; }
    public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getNotesDefault() { return notesDefault; }
    public void setNotesDefault(String notesDefault) { this.notesDefault = notesDefault; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String companyName;
        private String email;
        private String phone;
        private String address;
        private String city;
        private String state;
        private String stateCode;
        private String postalCode;
        private String country;
        private String taxId;
        private String gstin;
        private String upiId;
        private String payeeName;
        private String currencySymbol;
        private String logoUrl;
        private String notesDefault;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder companyName(String companyName) { this.companyName = companyName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder stateCode(String stateCode) { this.stateCode = stateCode; return this; }
        public Builder postalCode(String postalCode) { this.postalCode = postalCode; return this; }
        public Builder country(String country) { this.country = country; return this; }
        public Builder taxId(String taxId) { this.taxId = taxId; return this; }
        public Builder gstin(String gstin) { this.gstin = gstin; return this; }
        public Builder upiId(String upiId) { this.upiId = upiId; return this; }
        public Builder payeeName(String payeeName) { this.payeeName = payeeName; return this; }
        public Builder currencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; return this; }
        public Builder logoUrl(String logoUrl) { this.logoUrl = logoUrl; return this; }
        public Builder notesDefault(String notesDefault) { this.notesDefault = notesDefault; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public BusinessSettingsDTO build() {
            return new BusinessSettingsDTO(id, companyName, email, phone, address, city, state, stateCode, postalCode, country, taxId, gstin, upiId, payeeName, currencySymbol, logoUrl, notesDefault, updatedAt);
        }
    }
}
