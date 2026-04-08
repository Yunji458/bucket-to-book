package com.yunji.backend.service;

import com.yunji.backend.config.SweetbookProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

@Service
public class SweetbookApiService {

    private final RestClient restClient;
    private final SweetbookProperties properties;
    private final FileStorageService fileStorageService;

    public SweetbookApiService(
            RestClient restClient,
            SweetbookProperties properties,
            FileStorageService fileStorageService
    ) {
        this.restClient = restClient;
        this.properties = properties;
        this.fileStorageService = fileStorageService;
    }

    public Map<String, Object> getCredits() {
        return restClient.get()
                .uri(properties.getBaseUrl() + "/credits")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getKey())
                .retrieve()
                .body(Map.class);
    }

    public Map<String, Object> getBookSpecs() {
        return restClient.get()
                .uri(properties.getBaseUrl() + "/book-specs")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getKey())
                .retrieve()
                .body(Map.class);
    }

    public Map<String, Object> getTemplates(String bookSpecUid) {
        return restClient.get()
                .uri(properties.getBaseUrl() + "/templates?bookSpecUid=" + bookSpecUid)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getKey())
                .retrieve()
                .body(Map.class);
    }

    public Map<String, Object> createBook(Map<String, Object> payload) {
        return restClient.post()
                .uri(properties.getBaseUrl() + "/books")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getKey())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(payload)
                .retrieve()
                .body(Map.class);
    }

    public Map<String, Object> addBookCover(
            String bookUid,
            String templateUid,
            String frontImageFileName,
            String backImageFileName,
            String dateRange,
            String spineTitle
    ) {
        Path frontPath = fileStorageService.resolveImagePath(frontImageFileName);
        Path backPath = fileStorageService.resolveImagePath(backImageFileName);

        File frontFile = frontPath.toFile();
        File backFile = backPath.toFile();

        if (!frontFile.exists()) {
            throw new RuntimeException("표지 앞면 이미지를 찾을 수 없습니다: " + frontPath.toAbsolutePath());
        }

        if (!backFile.exists()) {
            throw new RuntimeException("표지 뒷면 이미지를 찾을 수 없습니다: " + backPath.toAbsolutePath());
        }

        FileSystemResource frontResource = new FileSystemResource(frontFile);
        FileSystemResource backResource = new FileSystemResource(backFile);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("frontPhoto", frontResource);
        body.add("backPhoto", backResource);
        body.add("templateUid", templateUid);
        body.add("dateRange", dateRange);
        body.add("spineTitle", spineTitle);

        return restClient.post()
                .uri(properties.getBaseUrl() + "/books/" + bookUid + "/cover")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getKey())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(Map.class);
    }

    public Map<String, Object> addBookContent(String bookUid, String templateUid, String imageFileName, String date) {
        Path imagePath = fileStorageService.resolveImagePath(imageFileName);

        System.out.println("Trying image path: " + imagePath.toAbsolutePath());

        File imageFile = imagePath.toFile();
        if (!imageFile.exists()) {
            throw new RuntimeException("이미지 파일을 찾을 수 없습니다: " + imagePath.toAbsolutePath());
        }

        FileSystemResource fileResource = new FileSystemResource(imageFile);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("collagePhotos", fileResource);
        body.add("templateUid", templateUid);
        body.add("date", date);

        return restClient.post()
                .uri(properties.getBaseUrl() + "/books/" + bookUid + "/contents?breakBefore=page")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getKey())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(Map.class);
    }

    public Map<String, Object> finalizeBook(String bookUid) {
        return restClient.post()
                .uri(properties.getBaseUrl() + "/books/" + bookUid + "/finalization")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getKey())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{}")
                .retrieve()
                .body(Map.class);
    }

    public Map<String, Object> estimateOrder(Map<String, Object> payload) {
        return restClient.post()
                .uri(properties.getBaseUrl() + "/orders/estimate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getKey())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(payload)
                .retrieve()
                .body(Map.class);
    }

    public Map<String, Object> createOrder(Map<String, Object> payload) {
        return restClient.post()
                .uri(properties.getBaseUrl() + "/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getKey())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(payload)
                .retrieve()
                .body(Map.class);
    }
}