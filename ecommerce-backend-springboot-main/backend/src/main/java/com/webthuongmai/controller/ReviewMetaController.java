package com.webthuongmai.controller;
import com.webthuongmai.entity.ReviewMeta;
import com.webthuongmai.service.ReviewMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/review-metas")
@CrossOrigin("*")
public class ReviewMetaController {
    @Autowired
    private ReviewMetaService reviewMetaService;

    @GetMapping
    public List<ReviewMeta> getAll() {
        return reviewMetaService.getAllMetas();
    }

    @PostMapping
    public ReviewMeta create(@RequestBody ReviewMeta meta) {
        return reviewMetaService.createMeta(meta);
    }
}