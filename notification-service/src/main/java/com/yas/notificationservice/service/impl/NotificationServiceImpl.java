package com.yas.notificationservice.service.impl;

import com.yas.notificationservice.dto.BookingDTO;
import com.yas.notificationservice.dto.NotificationDTO;
import com.yas.notificationservice.mapper.NotificationMapper;
import com.yas.notificationservice.model.Notification;
import com.yas.notificationservice.repository.NotificationRepository;
import com.yas.notificationservice.service.NotificationService;
import com.yas.notificationservice.service.client.BookingFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final BookingFeignClient bookingFeignClient;

    @Override
    public NotificationDTO createNotification(Notification notification) throws Exception {
        Notification savedNotification = notificationRepository.save(notification);
        BookingDTO bookingDTO= bookingFeignClient.getBookingById(savedNotification.getBookingId()).getBody();
        if(bookingDTO==null){
            throw new Exception("booking not found");
        }
        return NotificationMapper.toNotificationDTO(savedNotification,bookingDTO);
    }

    @Override
    public List<Notification> getAllNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public List<Notification> getAllNotificationsBySalonId(Long salonId) {
        return notificationRepository.findBySalonId(salonId);
    }

    @Override
    public Notification markNotificationAsRead(Long notificationId) throws Exception {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if(notification==null){
            throw new Exception("Notification not found");
        }
        if(!notification.isNotificationRead()){
            notification.setNotificationRead(true);
        }
        return notificationRepository.save(notification);
    }
}
