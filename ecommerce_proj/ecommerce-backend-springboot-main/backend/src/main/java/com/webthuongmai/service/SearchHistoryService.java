package com.webthuongmai.service;
import com.webthuongmai.entity.SearchHistory;
import com.webthuongmai.repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SearchHistoryService {
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    public List<SearchHistory> getAllSearchHistory() {
        return searchHistoryRepository.findAll();
    }

    public List<SearchHistory> getByUserId(Long userId) {
        return searchHistoryRepository.findByUser_UserIDOrderByCreatedAtDesc(userId);
    }

    public SearchHistory createSearchHistory(SearchHistory history) {
        return searchHistoryRepository.save(history);
    }
}