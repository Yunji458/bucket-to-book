package com.yunji.backend.controller;

import com.yunji.backend.dto.response.BucketItemResponse;
import com.yunji.backend.dto.response.OrganizedBucketResponse;
import com.yunji.backend.entity.BucketItem;
import com.yunji.backend.repository.BucketItemRepository;
import com.yunji.backend.service.FileStorageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/buckets")
public class BucketItemController {

    private final BucketItemRepository bucketItemRepository;
    private final FileStorageService fileStorageService;

    public BucketItemController(
            BucketItemRepository bucketItemRepository,
            FileStorageService fileStorageService
    ) {
        this.bucketItemRepository = bucketItemRepository;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public BucketItemResponse createBucketItem(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) String category,
            @RequestParam String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate completedDate,
            @RequestPart(required = false) MultipartFile image
    ) {
        validateRequiredFields(title, description, status);

        BucketItem bucketItem = new BucketItem();
        bucketItem.setTitle(title);
        bucketItem.setDescription(description);
        bucketItem.setCategory(category);
        bucketItem.setStatus(status);
        bucketItem.setTargetDate(targetDate);
        bucketItem.setCompletedDate(completedDate);

        if (image != null && !image.isEmpty()) {
            try {
                String savedFileName = fileStorageService.saveImage(image);
                bucketItem.setImageUrl(savedFileName);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장 실패", e);
            }
        }

        BucketItem saved = bucketItemRepository.save(bucketItem);
        return new BucketItemResponse(saved);
    }

    @GetMapping
    public List<BucketItemResponse> getBucketItems() {
        return bucketItemRepository.findAll()
                .stream()
                .map(BucketItemResponse::new)
                .toList();
    }

    @GetMapping("/organized")
    public OrganizedBucketResponse getOrganizedBucketItems() {
        List<BucketItemResponse> allItems = bucketItemRepository.findAll()
                .stream()
                .map(BucketItemResponse::new)
                .toList();

        // 완료: 완료일 최근순(내림차순)
        Comparator<BucketItemResponse> achievedOrder = Comparator
                .comparing(BucketItemResponse::getCompletedDate,
                        Comparator.nullsLast(Comparator.reverseOrder()));

        // 진행중/예정: 목표일 오름차순
        Comparator<BucketItemResponse> plannedOrder = Comparator
                .comparing(BucketItemResponse::getTargetDate,
                        Comparator.nullsLast(Comparator.naturalOrder()));

        Map<String, List<BucketItemResponse>> achieved = allItems.stream()
                .filter(item -> "완료".equals(item.getStatus()))
                .sorted(achievedOrder)
                .collect(Collectors.groupingBy(
                        item -> normalizeCategory(item.getCategory()),
                        Collectors.toList()
                ));

        Map<String, List<BucketItemResponse>> inProgress = allItems.stream()
                .filter(item -> "진행중".equals(item.getStatus()))
                .sorted(plannedOrder)
                .collect(Collectors.groupingBy(
                        item -> normalizeCategory(item.getCategory()),
                        Collectors.toList()
                ));

        Map<String, List<BucketItemResponse>> planned = allItems.stream()
                .filter(item -> !"완료".equals(item.getStatus()) && !"진행중".equals(item.getStatus()))
                .sorted(plannedOrder)
                .collect(Collectors.groupingBy(
                        item -> normalizeCategory(item.getCategory()),
                        Collectors.toList()
                ));

        return new OrganizedBucketResponse(achieved, inProgress, planned);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public BucketItemResponse updateBucketItem(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) String category,
            @RequestParam String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate completedDate,
            @RequestPart(required = false) MultipartFile image
    ) {
        validateRequiredFields(title, description, status);

        BucketItem item = bucketItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 버킷리스트가 없습니다."));

        item.setTitle(title);
        item.setDescription(description);
        item.setCategory(category);
        item.setStatus(status);
        item.setTargetDate(targetDate);
        item.setCompletedDate(completedDate);

        if (image != null && !image.isEmpty()) {
            try {
                String savedFileName = fileStorageService.saveImage(image);
                item.setImageUrl(savedFileName);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장 실패", e);
            }
        }

        BucketItem saved = bucketItemRepository.save(item);
        return new BucketItemResponse(saved);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBucketItem(@PathVariable Long id) {
        if (!bucketItemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 버킷리스트가 없습니다.");
        }
        bucketItemRepository.deleteById(id);
    }

    private void validateRequiredFields(String title, String description, String status) {
        if (title == null || title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목은 필수입니다.");
        }
        if (description == null || description.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용은 필수입니다.");
        }
        if (status == null || status.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상태는 필수입니다.");
        }
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "기타";
        }
        return category;
    }
}