package com.webthuongmai.controller;
import com.webthuongmai.entity.UserActivity;
import com.webthuongmai.service.UserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user-activities")
@CrossOrigin("*")
public class UserActivityController {
    @Autowired
    private UserActivityService userActivityService;

    @GetMapping
    public List<UserActivity> getAll() {
        return userActivityService.getAllActivities();
    }

    @PostMapping
    public UserActivity create(@RequestBody UserActivity activity) {
        return userActivityService.createActivity(activity);
    }
}