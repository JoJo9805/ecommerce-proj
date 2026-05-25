package com.webthuongmai.service;
import com.webthuongmai.entity.UserActivity;
import com.webthuongmai.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserActivityService {
    @Autowired
    private UserActivityRepository userActivityRepository;

    public List<UserActivity> getAllActivities() {
        return userActivityRepository.findAll();
    }

    public UserActivity createActivity(UserActivity activity) {
        return userActivityRepository.save(activity);
    }
}