package com.yunji.backend.dto.request;

import java.time.LocalDate;

public class BucketItemCreateRequest {

    private String title;
    private String description;
    private String category;
    private String status;
    private String imageUrl;
    private LocalDate targetDate;
    private LocalDate completedDate;

    public BucketItemCreateRequest() {
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }
}