package com.webthuongmai.repository;
import com.webthuongmai.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByUser_UserIDOrderByCreatedAtDesc(Long userId);
}