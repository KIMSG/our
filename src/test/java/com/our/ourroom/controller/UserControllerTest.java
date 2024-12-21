package com.our.ourroom.controller;

import com.our.ourroom.entity.Users;
import com.our.ourroom.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        when(userService.createUser(any(Users.class))).thenReturn(testUser);

        // POST 요청 테스트
        String userJson = "{\"name\":\"Test User\"}";

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk()) // HTTP 상태 코드 200 확인
                .andExpect(jsonPath("$.id", is(1))) // 반환된 ID 확인
                .andExpect(jsonPath("$.name", is("Test User"))); // 반환된 이름 확인
    }

    @Test
    public void testGetAllUsers() throws Exception {
        Users user1 = new Users();
        user1.setId(1L);
        user1.setName("User 1");

        Users user2 = new Users();
        user2.setId(2L);
        user2.setName("User 2");

        List<Users> users = List.of(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(users.size()))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("User 2"));
    }

    @Test
    public void testGetUserById() throws Exception {
        Users user = new Users();
        user.setId(1L);
        user.setName("Test User");

        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    public void testGetUserById_NotFound() throws Exception {
        // Mock: 존재하지 않는 ID로 조회
        when(userService.getUserById(1L)).thenReturn(null);

        // GET 요청
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound()) // HTTP 404 상태 확인
                .andExpect(jsonPath("$").doesNotExist()); // body가 비어 있는지 확인
    }

}