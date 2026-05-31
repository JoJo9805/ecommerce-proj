package com.webthuongmai.controller;
import com.webthuongmai.entity.ReviewReport;
import com.webthuongmai.service.ReviewReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/review-reports")
@CrossOrigin("*")
public class ReviewReportController {
    @Autowired
    private ReviewReportService reviewReportService;

    @GetMapping
    public List<ReviewReport> getAll() {
        return reviewReportService.getAllReports();
    }

    @PostMapping
    public ReviewReport create(@RequestBody ReviewReport report) {
        return reviewReportService.createReport(report);
    }
}