package com.our.ourroom.service;

import com.our.ourroom.entity.Users;
import com.our.ourroom.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 사용자 생성 로직
     * @param user 사용자 데이터
     * @return 저장된 사용자
     */
    public Users createUser(Users user) {
        return userRepository.save(user);
    }
}