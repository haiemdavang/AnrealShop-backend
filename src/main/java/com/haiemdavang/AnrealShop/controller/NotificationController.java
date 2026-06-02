package com.haiemdavang.AnrealShop.controller;

import com.haiemdavang.AnrealShop.dto.notification.NotificationResponse;
import com.haiemdavang.AnrealShop.service.notice.NotificationRestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRestService notificationRestService;

    @GetMapping("/user")
    public ResponseEntity<Page<NotificationResponse>> getUserNotifications(Pageable pageable) {
        return ResponseEntity.ok(notificationRestService.getUserNotifications(pageable));
    }

    @GetMapping("/user/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        return ResponseEntity.ok(notificationRestService.getUnreadCount());
    }

    @PatchMapping("/user/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable String id) {
        return ResponseEntity.ok(notificationRestService.markAsRead(id));
    }

    @PatchMapping("/user/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        notificationRestService.markAllAsRead();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shop")
    public ResponseEntity<Page<NotificationResponse>> getShopNotifications(Pageable pageable) {
        return ResponseEntity.ok(notificationRestService.getShopNotifications(pageable));
    }

    @GetMapping("/shop/unread-count")
    public ResponseEntity<Long> getUnreadShopCount() {
        return ResponseEntity.ok(notificationRestService.getUnreadShopCount());
    }

    @PatchMapping("/shop/{id}/read")
    public ResponseEntity<NotificationResponse> markShopAsRead(@PathVariable String id) {
        return ResponseEntity.ok(notificationRestService.markShopAsRead(id));
    }

    @PatchMapping("/shop/read-all")
    public ResponseEntity<Void> markAllShopAsRead() {
        notificationRestService.markAllShopAsRead();
        return ResponseEntity.noContent().build();
    }
}
