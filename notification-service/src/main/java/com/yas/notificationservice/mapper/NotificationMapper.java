package com.yas.notificationservice.mapper;

import com.yas.notificationservice.dto.BookingDTO;
import com.yas.notificationservice.dto.NotificationDTO;
import com.yas.notificationservice.model.Notification;

public class NotificationMapper {
    public static NotificationDTO toNotificationDTO(Notification notification, BookingDTO bookingDTO) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notification.getId());
        notificationDTO.setBookingId(bookingDTO.getId());
        notificationDTO.setUserId(notification.getUserId());
        notificationDTO.setRead(notification.isRead());
        notificationDTO.setDescription(notification.getDescription());
        notificationDTO.setType(notification.getType());
        notificationDTO.setCreatedAt(notification.getCreatedAt());
        notificationDTO.setBooking(bookingDTO);
        notificationDTO.setSalonId(notification.getSalonId());
        return notificationDTO;

    }
}
