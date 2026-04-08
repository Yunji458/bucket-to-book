package com.yunji.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Path UPLOAD_DIR = Paths.get("uploads").normalize();
    private static final Path DUMMY_DIR = Paths.get("..", "dummy-data", "images").normalize();
    private static final String DEFAULT_IMAGE = "default.jpg";

    public String saveImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("이미지 파일이 비어 있습니다.");
        }

        Files.createDirectories(UPLOAD_DIR);

        String originalName = StringUtils.cleanPath(image.getOriginalFilename());
        String extension = "";

        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalName.substring(dotIndex);
        }

        String savedFileName = UUID.randomUUID() + extension;
        Path uploadTarget = UPLOAD_DIR.resolve(savedFileName);

        Files.copy(image.getInputStream(), uploadTarget, StandardCopyOption.REPLACE_EXISTING);

        return savedFileName;
    }

    // 기존 다른 코드와의 호환용
    public String saveUploadedImage(MultipartFile image) throws IOException {
        return saveImage(image);
    }

    public Path resolveImagePath(String imageFileName) {
        if (imageFileName == null || imageFileName.isBlank()) {
            return resolveDefaultImagePath();
        }

        Path uploadPath = UPLOAD_DIR.resolve(imageFileName).normalize();
        if (Files.exists(uploadPath)) {
            return uploadPath;
        }

        Path dummyPath = DUMMY_DIR.resolve(imageFileName).normalize();
        if (Files.exists(dummyPath)) {
            return dummyPath;
        }

        return resolveDefaultImagePath();
    }

    private Path resolveDefaultImagePath() {
        Path uploadDefault = UPLOAD_DIR.resolve(DEFAULT_IMAGE).normalize();
        if (Files.exists(uploadDefault)) {
            return uploadDefault;
        }

        Path dummyDefault = DUMMY_DIR.resolve(DEFAULT_IMAGE).normalize();
        if (Files.exists(dummyDefault)) {
            return dummyDefault;
        }

        throw new RuntimeException(
                "기본 이미지를 찾을 수 없습니다. uploads/default.jpg 또는 dummy-data/images/default.jpg 파일이 필요합니다."
        );
    }
}