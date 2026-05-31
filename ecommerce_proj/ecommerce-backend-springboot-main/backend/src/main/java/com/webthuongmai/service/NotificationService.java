package com.webthuongmai.service;
import com.webthuongmai.dto.NotificationDTO;
import com.webthuongmai.entity.Notification;
import com.webthuongmai.entity.User;
import com.webthuongmai.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    // Helper tạo nhanh 1 thông báo gửi tới 1 user. Bọc try-catch ở nơi gọi
    // để việc tạo thông báo lỗi không làm hỏng nghiệp vụ chính.
    public Notification create(Long receiverId, String type, String title, String content, String relatedLink) {
        User receiver = new User();
        receiver.setUserID(receiverId);

        Notification n = new Notification();
        n.setReceiver(receiver);
        n.setType(type);
        n.setTitle(title);
        n.setContent(content);
        n.setRelatedLink(relatedLink);
        n.setIsRead(false);
        n.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(n);
    }

    public List<NotificationDTO> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByReceiver_UserIDOrderByCreatedAtDesc(userId);

        List<NotificationDTO> dtoList = new ArrayList<>();

        for (Notification notif : notifications) {
            NotificationDTO dto = new NotificationDTO();
            dto.setNotificationId(notif.getNotificationID());
            dto.setType(notif.getType());
            dto.setTitle(notif.getTitle());
            dto.setContent(notif.getContent());
            dto.setIsRead(notif.getIsRead());
            dto.setCreatedAt(notif.getCreatedAt());

            dtoList.add(dto);
        }

        return dtoList;
    }

    // Đánh dấu MỘT thông báo đã đọc
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now());
            notificationRepository.save(n);
        });
    }

    // Đánh dấu TẤT CẢ thông báo của user là đã đọc
    public void markAllAsRead(Long userId) {
        List<Notification> list = notificationRepository.findByReceiver_UserIDOrderByCreatedAtDesc(userId);
        for (Notification n : list) {
            if (n.getIsRead() == null || !n.getIsRead()) {
                n.setIsRead(true);
                n.setReadAt(LocalDateTime.now());
            }
        }
        notificationRepository.saveAll(list);
    }
}