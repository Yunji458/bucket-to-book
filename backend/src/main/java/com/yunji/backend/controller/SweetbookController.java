package com.yunji.backend.controller;

import com.yunji.backend.service.SweetbookApiService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sweetbook")
public class SweetbookController {

    private final SweetbookApiService sweetbookApiService;

    public SweetbookController(SweetbookApiService sweetbookApiService) {
        this.sweetbookApiService = sweetbookApiService;
    }

    @GetMapping("/credits")
    public Map<String, Object> getCredits() {
        return sweetbookApiService.getCredits();
    }

    @GetMapping("/book-specs")
    public Map<String, Object> getBookSpecs() {
        return sweetbookApiService.getBookSpecs();
    }

    @GetMapping("/templates")
    public Map<String, Object> getTemplates(@RequestParam String bookSpecUid) {
        return sweetbookApiService.getTemplates(bookSpecUid);
    }
}