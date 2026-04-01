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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notification/salon-woner")
@RequiredArgsConstructor
public class SalonNotificationController {

    private final NotificationService notificationService;
    private final BookingFeignClient bookingFeignClient;

    @GetMapping("/salon/{salonId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserId(@PathVariable Long salonId) throws Exception {
        List<Notification> notifications=notificationService.getAllNotificationsBySalonId(salonId);


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
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Notification>  readNotification(@PathVariable Long notificationId) throws Exception {
        Notification notification=notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok(notification);
    }
}
