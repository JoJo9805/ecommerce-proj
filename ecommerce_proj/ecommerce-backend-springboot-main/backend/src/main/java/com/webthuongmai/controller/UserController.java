package com.webthuongmai.controller;
import com.webthuongmai.dto.UserProfileDTO;
import com.webthuongmai.entity.User;
import com.webthuongmai.repository.UserRepository;
import com.webthuongmai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserInfo(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<String> updateUserProfile(
            @PathVariable Long id, 
            @RequestBody UserProfileDTO request) {
        
        return ResponseEntity.ok(userService.updateUserProfile(id, request));
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.createUser(user);
    }
}