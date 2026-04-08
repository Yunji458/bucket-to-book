package com.yunji.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sweetbook.api")
public class SweetbookProperties {

    private String baseUrl;
    private String key;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getKey() {
        return key;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setKey(String key) {
        this.key = key;
    }
}