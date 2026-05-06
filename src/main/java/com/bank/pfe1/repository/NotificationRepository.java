package com.bank.pfe1.repository;

import com.bank.pfe1.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReadFalseOrderByCreatedAtDesc();

    //List<Notification> findByTypeOrderByCreatedAtDesc(Notification.NotificationType type);

    long countByReadFalse();
}

