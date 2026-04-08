package com.yunji.backend.controller;

import com.yunji.backend.entity.BucketItem;
import com.yunji.backend.repository.BucketItemRepository;
import com.yunji.backend.service.FileStorageService;
import com.yunji.backend.service.SweetbookApiService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final SweetbookApiService sweetbookApiService;
    private final BucketItemRepository bucketItemRepository;
    private final FileStorageService fileStorageService;

    public BookController(
            SweetbookApiService sweetbookApiService,
            BucketItemRepository bucketItemRepository,
            FileStorageService fileStorageService
    ) {
        this.sweetbookApiService = sweetbookApiService;
        this.bucketItemRepository = bucketItemRepository;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/create")
    public Map<String, Object> createBook(@RequestBody Map<String, Object> request) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", request.get("title"));
            payload.put("bookSpecUid", request.get("bookSpecUid"));

            Map<String, Object> apiResponse = sweetbookApiService.createBook(payload);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("apiResponse", apiResponse);
            return response;
        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return error;
        }
    }

    @PostMapping(value = "/{bookUid}/cover", consumes = "multipart/form-data")
    public Map<String, Object> addCover(
            @PathVariable String bookUid,
            @RequestParam String templateUid,
            @RequestParam String dateRange,
            @RequestParam String spineTitle,
            @RequestPart MultipartFile frontImage,
            @RequestPart MultipartFile backImage
    ) {
        try {
            String savedFrontFileName = fileStorageService.saveUploadedImage(frontImage);
            String savedBackFileName = fileStorageService.saveUploadedImage(backImage);

            Map<String, Object> apiResponse = sweetbookApiService.addBookCover(
                    bookUid,
                    templateUid,
                    savedFrontFileName,
                    savedBackFileName,
                    dateRange,
                    spineTitle
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("frontImageFile", savedFrontFileName);
            response.put("backImageFile", savedBackFileName);
            response.put("apiResponse", apiResponse);
            return response;

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return error;
        }
    }

    @PostMapping("/{bookUid}/contents-auto")
    public Map<String, Object> addContentsAutomatically(
            @PathVariable String bookUid,
            @RequestBody Map<String, Object> request
    ) {
        try {
            String templateUid = (String) request.get("templateUid");

            List<BucketItem> selectedItems = getSelectedItemsFromRequest(request);

            Map<String, List<BucketItem>> achieved = selectedItems.stream()
                    .filter(item -> "완료".equals(item.getStatus()))
                    .collect(Collectors.groupingBy(item ->
                            item.getCategory() == null || item.getCategory().isBlank() ? "기타" : item.getCategory()
                    ));

            Map<String, List<BucketItem>> planned = selectedItems.stream()
                    .filter(item -> !"완료".equals(item.getStatus()))
                    .collect(Collectors.groupingBy(item ->
                            item.getCategory() == null || item.getCategory().isBlank() ? "기타" : item.getCategory()
                    ));

            List<Map<String, Object>> results = new ArrayList<>();

            addGroupContents(bookUid, templateUid, achieved, "achieved", results);
            addGroupContents(bookUid, templateUid, planned, "planned", results);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("bookUid", bookUid);
            response.put("selectedCount", selectedItems.size());
            response.put("addedCount", results.size());
            response.put("results", results);

            return response;
        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return error;
        }
    }

    @PostMapping("/{bookUid}/finalize")
    public Map<String, Object> finalizeBook(@PathVariable String bookUid) {
        try {
            Map<String, Object> apiResponse = sweetbookApiService.finalizeBook(bookUid);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("apiResponse", apiResponse);
            return response;

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return error;
        }
    }

    private List<BucketItem> getSelectedItemsFromRequest(Map<String, Object> request) {
        Object selectedItemsObject = request.get("selectedItems");

        if (!(selectedItemsObject instanceof List<?> selectedItemsList) || selectedItemsList.isEmpty()) {
            throw new RuntimeException("선택된 버킷리스트가 없습니다.");
        }

        List<Long> selectedIds = selectedItemsList.stream()
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(itemMap -> itemMap.get("id"))
                .filter(Objects::nonNull)
                .map(idValue -> Long.valueOf(String.valueOf(idValue)))
                .toList();

        if (selectedIds.isEmpty()) {
            throw new RuntimeException("선택된 버킷리스트 ID가 없습니다.");
        }

        return bucketItemRepository.findAllById(selectedIds);
    }

    private void addGroupContents(
            String bookUid,
            String templateUid,
            Map<String, List<BucketItem>> groupedItems,
            String groupName,
            List<Map<String, Object>> results
    ) {
        for (Map.Entry<String, List<BucketItem>> entry : groupedItems.entrySet()) {
            String category = entry.getKey();
            List<BucketItem> items = entry.getValue();

            for (BucketItem item : items) {
                String dateValue;

                if (item.getCompletedDate() != null) {
                    dateValue = item.getCompletedDate().toString();
                } else if (item.getTargetDate() != null) {
                    dateValue = item.getTargetDate().toString();
                } else {
                    dateValue = "2026-04-03";
                }

                Map<String, Object> apiResponse =
                        sweetbookApiService.addBookContent(
                                bookUid,
                                templateUid,
                                item.getImageUrl(),
                                dateValue
                        );

                Map<String, Object> resultItem = new HashMap<>();
                resultItem.put("group", groupName);
                resultItem.put("category", category);
                resultItem.put("bucketItemId", item.getId());
                resultItem.put("title", item.getTitle());
                resultItem.put("imageFile", item.getImageUrl());
                resultItem.put("date", dateValue);
                resultItem.put("apiResponse", apiResponse);

                results.add(resultItem);
            }
        }
    }
}