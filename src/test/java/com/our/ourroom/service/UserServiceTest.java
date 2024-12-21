package com.our.ourroom.service;

import com.our.ourroom.entity.Users;
import com.our.ourroom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser() {
        // Given: Mock 데이터 준비
        Users user = new Users();
        user.setName("Test User");

        // When: userRepository.save()가 호출될 때 동작 설정
        when(userRepository.save(user)).thenReturn(user);

        // Then: 서비스 메서드 호출 및 결과 검증
        Users createdUser = userService.createUser(user);
        assertEquals("Test User", createdUser.getName()); // 이름 검증

        // Verify: userRepository.save()가 한 번 호출되었는지 검증
        verify(userRepository).save(user);
    }

    @Test
    public void testGetAllUsers() {
        // 가짜 데이터 설정
        Users user1 = new Users();
        user1.setId(1L);
        user1.setName("User 1");

        Users user2 = new Users();
        user2.setId(2L);
        user2.setName("User 2");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // 테스트 수행
        List<Users> users = userService.getAllUsers();

        // 결과 검증
        assertEquals(2, users.size());
        assertEquals("User 1", users.get(0).getName());
        assertEquals("User 2", users.get(1).getName());
    }

    @Test
    public void testGetUserById() {
        // Mock 데이터 설정
        Users user = new Users();
        user.setId(1L);
        user.setName("Test User");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // 테스트 수행
        Users foundUser = userService.getUserById(1L);

        // 결과 검증
        assertEquals("Test User", foundUser.getName());
        assertEquals(1L, foundUser.getId());
    }

}
