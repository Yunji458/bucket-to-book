package com.yunji.backend.dto.response;

import com.yunji.backend.entity.BucketItem;

import java.time.LocalDate;

public class BucketItemResponse {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String status;
    private String imageUrl;
    private LocalDate targetDate;
    private LocalDate completedDate;

    public BucketItemResponse(BucketItem bucketItem) {
        this.id = bucketItem.getId();
        this.title = bucketItem.getTitle();
        this.description = bucketItem.getDescription();
        this.category = bucketItem.getCategory();
        this.status = bucketItem.getStatus();
        this.imageUrl = bucketItem.getImageUrl();
        this.targetDate = bucketItem.getTargetDate();
        this.completedDate = bucketItem.getCompletedDate();
    }

    public Long getId() {
        return id;
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
}