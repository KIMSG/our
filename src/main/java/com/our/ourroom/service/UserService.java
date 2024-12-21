package com.our.ourroom.service;

import com.our.ourroom.entity.Users;
import com.our.ourroom.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * 모든 사용자 조회
     * - 데이터베이스에 저장된 모든 사용자 목록을 반환합니다.
     *
     * @return List<Users> 사용자 목록
     */
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 특정 사용자 조회
     * - 주어진 사용자 ID에 해당하는 사용자 정보를 반환합니다.
     * - 사용자 정보가 존재하지 않을 경우 null을 반환합니다.
     *
     * @param id 사용자 ID
     * @return Users 사용자 정보 (존재하지 않으면 null)
     */
    public Users getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}