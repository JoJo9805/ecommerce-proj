package com.webthuongmai.repository;
import com.webthuongmai.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiver_UserIDOrderByCreatedAtDesc(Long receiverId);

    long countByReceiver_UserIDAndIsReadFalse(Long receiverId);
}