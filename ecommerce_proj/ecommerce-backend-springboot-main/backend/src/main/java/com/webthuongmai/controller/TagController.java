package com.webthuongmai.controller;
import com.webthuongmai.entity.Tag;
import com.webthuongmai.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin("*")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping
    public List<Tag> getAll() {
        return tagService.getAllTags();
    }

    @PostMapping
    public Tag create(@RequestBody Tag tag) {
        return tagService.createTag(tag);
    }
}