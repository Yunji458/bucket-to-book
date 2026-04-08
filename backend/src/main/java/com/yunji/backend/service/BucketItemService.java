package com.yunji.backend.service;

import com.yunji.backend.dto.response.BucketItemResponse;
import com.yunji.backend.dto.response.OrganizedBucketResponse;
import com.yunji.backend.entity.BucketItem;
import com.yunji.backend.repository.BucketItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class BucketItemService {

    private final BucketItemRepository bucketItemRepository;
    private final FileStorageService fileStorageService;

    public BucketItemService(
            BucketItemRepository bucketItemRepository,
            FileStorageService fileStorageService
    ) {
        this.bucketItemRepository = bucketItemRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public List<BucketItemResponse> getBuckets() {
        return bucketItemRepository.findAll().stream()
                .map(BucketItemResponse::new)
                .toList();
    }

    public BucketItemResponse createBucket(
            String title,
            String description,
            String category,
            String status,
            LocalDate targetDate,
            LocalDate completedDate,
            MultipartFile image
    ) throws IOException {
        BucketItem bucketItem = new BucketItem();
        bucketItem.setTitle(title);
        bucketItem.setDescription(description);
        bucketItem.setCategory(category);
        bucketItem.setStatus(status);
        bucketItem.setTargetDate(targetDate);
        bucketItem.setCompletedDate(completedDate);

        if (image != null && !image.isEmpty()) {
            String savedFileName = fileStorageService.saveUploadedImage(image);
            bucketItem.setImageUrl(savedFileName);
        }

        BucketItem saved = bucketItemRepository.save(bucketItem);
        return new BucketItemResponse(saved);
    }

    public BucketItemResponse updateBucket(
            Long id,
            String title,
            String description,
            String category,
            String status,
            LocalDate targetDate,
            LocalDate completedDate,
            MultipartFile image
    ) throws IOException {
        BucketItem bucketItem = bucketItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("버킷리스트를 찾을 수 없습니다. id=" + id));

        bucketItem.setTitle(title);
        bucketItem.setDescription(description);
        bucketItem.setCategory(category);
        bucketItem.setStatus(status);
        bucketItem.setTargetDate(targetDate);
        bucketItem.setCompletedDate(completedDate);

        if (image != null && !image.isEmpty()) {
            String savedFileName = fileStorageService.saveUploadedImage(image);
            bucketItem.setImageUrl(savedFileName);
        }

        BucketItem saved = bucketItemRepository.save(bucketItem);
        return new BucketItemResponse(saved);
    }

    public void deleteBucket(Long id) {
        if (!bucketItemRepository.existsById(id)) {
            throw new RuntimeException("버킷리스트를 찾을 수 없습니다. id=" + id);
        }
        bucketItemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public OrganizedBucketResponse getOrganizedBuckets() {
        List<BucketItem> items = bucketItemRepository.findAll();

        // 완료: 완료일 최근순(내림차순)
        Comparator<BucketItem> achievedOrder = Comparator
                .comparing(BucketItem::getCompletedDate,
                        Comparator.nullsLast(Comparator.reverseOrder()));

        // 진행중/예정: 목표일 오름차순
        Comparator<BucketItem> plannedOrder = Comparator
                .comparing(BucketItem::getTargetDate,
                        Comparator.nullsLast(Comparator.naturalOrder()));

        Map<String, List<BucketItemResponse>> achieved = items.stream()
                .filter(item -> "완료".equals(item.getStatus()))
                .sorted(achievedOrder)
                .collect(Collectors.groupingBy(
                        item -> normalizeCategory(item.getCategory()),
                        Collectors.mapping(BucketItemResponse::new, Collectors.toList())
                ));

        Map<String, List<BucketItemResponse>> inProgress = items.stream()
                .filter(item -> "진행중".equals(item.getStatus()))
                .sorted(plannedOrder)
                .collect(Collectors.groupingBy(
                        item -> normalizeCategory(item.getCategory()),
                        Collectors.mapping(BucketItemResponse::new, Collectors.toList())
                ));

        Map<String, List<BucketItemResponse>> planned = items.stream()
                .filter(item -> !"완료".equals(item.getStatus()) && !"진행중".equals(item.getStatus()))
                .sorted(plannedOrder)
                .collect(Collectors.groupingBy(
                        item -> normalizeCategory(item.getCategory()),
                        Collectors.mapping(BucketItemResponse::new, Collectors.toList())
                ));

        return new OrganizedBucketResponse(achieved, inProgress, planned);
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "기타";
        }
        return category;
    }
}