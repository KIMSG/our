package com.our.ourroom.service;

import com.our.ourroom.entity.Users;
import com.our.ourroom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
}
