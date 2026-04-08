package com.yunji.backend.dto.response;

import java.util.List;
import java.util.Map;

public class OrganizedBucketResponse {

    private Map<String, List<BucketItemResponse>> achieved;
    private Map<String, List<BucketItemResponse>> inProgress;
    private Map<String, List<BucketItemResponse>> planned;

    public OrganizedBucketResponse(
            Map<String, List<BucketItemResponse>> achieved,
            Map<String, List<BucketItemResponse>> inProgress,
            Map<String, List<BucketItemResponse>> planned
    ) {
        this.achieved = achieved;
        this.inProgress = inProgress;
        this.planned = planned;
    }

    public Map<String, List<BucketItemResponse>> getAchieved() {
        return achieved;
    }

    public Map<String, List<BucketItemResponse>> getInProgress() {
        return inProgress;
    }

    public Map<String, List<BucketItemResponse>> getPlanned() {
        return planned;
    }
}