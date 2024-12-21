package com.our.ourroom.controller;

import com.our.ourroom.entity.Users;
import com.our.ourroom.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setId(1L);
        testUser.setName("Test User");
    }

    @Test
    public void testCreateUser() throws Exception {
        // Mock: UserService의 createUser 동작 설정
        Mockito.when(userService.createUser(any(Users.class))).thenReturn(testUser);

        // POST 요청 테스트
        String userJson = "{\"name\":\"Test User\"}";

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk()) // HTTP 상태 코드 200 확인
                .andExpect(jsonPath("$.id", is(1))) // 반환된 ID 확인
                .andExpect(jsonPath("$.name", is("Test User"))); // 반환된 이름 확인
    }
}