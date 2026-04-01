package com.yas.notificationservice.controller;

import com.yas.notificationservice.dto.BookingDTO;
import com.yas.notificationservice.dto.NotificationDTO;
import com.yas.notificationservice.mapper.NotificationMapper;
import com.yas.notificationservice.model.Notification;
import com.yas.notificationservice.service.NotificationService;
import com.yas.notificationservice.service.client.BookingFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final BookingFeignClient bookingFeignClient;

    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody Notification notification) throws Exception {
        NotificationDTO notificationDTO = notificationService.createNotification(notification);
        return ResponseEntity.ok(notificationDTO);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserId(@PathVariable Long userId) throws Exception {
        List<Notification> notifications=notificationService.getAllNotificationsByUserId(userId);


        List<NotificationDTO> notificationDTOS = notifications.stream()
                .map(notification -> {
                    BookingDTO booking = null;
                    try {
                        booking = bookingFeignClient.getBookingById(notification.getBookingId()).getBody();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return NotificationMapper.toNotificationDTO(notification, booking);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(notificationDTOS);

    }
}
