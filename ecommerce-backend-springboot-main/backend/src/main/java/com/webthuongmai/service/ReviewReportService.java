package com.webthuongmai.service;
import com.webthuongmai.entity.ReviewReport;
import com.webthuongmai.repository.ReviewReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewReportService {
    @Autowired
    private ReviewReportRepository reviewReportRepository;

    public List<ReviewReport> getAllReports() {
        return reviewReportRepository.findAll();
    }

    public ReviewReport createReport(ReviewReport report) {
        return reviewReportRepository.save(report);
    }
}