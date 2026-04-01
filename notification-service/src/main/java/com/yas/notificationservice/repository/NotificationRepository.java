package com.yas.notificationservice.repository;

import com.yas.notificationservice.model.Notification;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {


    List<Notification> findByUserId(Long userId);
    List<Notification> findBySalonId(Long salonId);
}
