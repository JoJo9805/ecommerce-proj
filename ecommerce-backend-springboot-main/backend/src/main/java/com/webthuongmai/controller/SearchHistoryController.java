package com.webthuongmai.controller;
import com.webthuongmai.entity.SearchHistory;
import com.webthuongmai.service.SearchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/search-history")
@CrossOrigin("*")
public class SearchHistoryController {
    @Autowired
    private SearchHistoryService searchHistoryService;

    @GetMapping
    public List<SearchHistory> getAll() {
        return searchHistoryService.getAllSearchHistory();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SearchHistory>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(searchHistoryService.getByUserId(userId));
    }

    @PostMapping
    public SearchHistory create(@RequestBody SearchHistory history) {
        return searchHistoryService.createSearchHistory(history);
    }
}