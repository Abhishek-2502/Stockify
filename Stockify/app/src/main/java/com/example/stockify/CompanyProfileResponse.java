package com.example.stockify;

public class CompanyProfileResponse {
    private String name;    // Company name
    private String logo;    // Company logo URL
    private String weburl;  // Company website URL
    private String exchange; // Exchange name

    // Getters
    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public String getWebUrl() {
        return weburl;
    }

    public String getExchange() {
        return exchange;
    }
}
