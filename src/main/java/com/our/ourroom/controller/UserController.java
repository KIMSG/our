package com.our.ourroom.controller;

import com.our.ourroom.entity.Users;
import com.our.ourroom.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 사용자 생성 API
     * @param user 사용자 데이터 (JSON)
     * @return 생성된 사용자 데이터
     */
    @PostMapping
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        Users createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }
}
