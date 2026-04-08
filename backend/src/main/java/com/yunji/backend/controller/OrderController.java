package com.yunji.backend.controller;

import com.yunji.backend.service.SweetbookApiService;
import org.springframework.web.bind.annotation.*;

        import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final SweetbookApiService sweetbookApiService;

    public OrderController(SweetbookApiService sweetbookApiService) {
        this.sweetbookApiService = sweetbookApiService;
    }

    @PostMapping("/estimate")
    public Map<String, Object> estimateOrder(@RequestBody Map<String, Object> request) {
        try {
            String bookUid = (String) request.get("bookUid");

            Map<String, Object> item = new HashMap<>();
            item.put("bookUid", bookUid);
            item.put("quantity", 1);

            Map<String, Object> payload = new HashMap<>();
            payload.put("items", List.of(item));

            return sweetbookApiService.estimateOrder(payload);

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return error;
        }
    }

    @PostMapping("/create")
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> request) {
        try {
            String bookUid = (String) request.get("bookUid");

            Map<String, Object> item = new HashMap<>();
            item.put("bookUid", bookUid);
            item.put("quantity", 1);

            Map<String, Object> shipping = new HashMap<>();
            shipping.put("recipientName", "김윤지");
            shipping.put("recipientPhone", "010-1234-5678");
            shipping.put("postalCode", "06236");
            shipping.put("address1", "서울특별시 광진구 뚝섬로 435");
            shipping.put("address2", "4층");

            Map<String, Object> payload = new HashMap<>();
            payload.put("items", List.of(item));
            payload.put("shipping", shipping);

            return sweetbookApiService.createOrder(payload);

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return error;
        }
    }
}