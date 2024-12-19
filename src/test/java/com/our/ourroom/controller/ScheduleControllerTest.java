package com.our.ourroom.controller;

import com.our.ourroom.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private ScheduleService scheduleService;

    @Test
    public void testGetAllSchedules() throws Exception {
        // ScheduleService의 getAllSchedules() 메서드를 Mocking
        Mockito.doReturn(Collections.emptyList()).when(scheduleService).getAllSchedules();

        // /api/schedules GET 요청 테스트
        mockMvc.perform(get("/api/schedules"))
                .andExpect(status().isOk()) // HTTP 상태 코드 200 확인
                .andExpect(jsonPath("$", hasSize(0))); // 반환된 JSON 배열이 비어 있는지 확인
    }
}
