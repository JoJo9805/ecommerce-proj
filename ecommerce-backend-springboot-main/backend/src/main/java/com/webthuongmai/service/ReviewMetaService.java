package com.webthuongmai.service;
import com.webthuongmai.entity.ReviewMeta;
import com.webthuongmai.repository.ReviewMetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewMetaService {
    @Autowired
    private ReviewMetaRepository reviewMetaRepository;

    public List<ReviewMeta> getAllMetas() {
        return reviewMetaRepository.findAll();
    }

    public ReviewMeta createMeta(ReviewMeta meta) {
        return reviewMetaRepository.save(meta);
    }
}