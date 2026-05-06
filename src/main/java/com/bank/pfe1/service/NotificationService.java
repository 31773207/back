package com.bank.pfe1.service;

import com.bank.pfe1.entity.Notification;
import com.bank.pfe1.entity.NotificationType;  // ✅ ADD THIS IMPORT
import com.bank.pfe1.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findByReadFalseOrderByCreatedAtDesc();
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public long getUnreadCount() {
        return notificationRepository.countByReadFalse();
    }

    // ✅ FIXED: Use NotificationType (not Notification.NotificationType)
    public Notification createNotification(String title, String message, NotificationType type, Long relatedId, String link) {
        Notification notification = Notification.builder()
                .type(type)
                .title(title)
                .message(message)
                .link(link)
                .relatedId(relatedId)
                .read(false)
                .build();
        return notificationRepository.save(notification);
    }

    // ✅ OVERLOADED METHOD: For backwards compatibility
    public Notification createNotification(String title, String message, NotificationType type, Long relatedId) {
        return createNotification(title, message, type, relatedId, null);
    }

    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    public void deleteAllRead() {
        List<Notification> readNotifications = notificationRepository.findAll().stream()
                .filter(Notification::isRead)
                .toList();
        notificationRepository.deleteAll(readNotifications);
    }
}