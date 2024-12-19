package com.our.ourroom.controller;

import com.our.ourroom.entity.Schedule;
import com.our.ourroom.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ScheduleService scheduleService; // Mockito의 @Mock 사용


    @Test
    public void testGetAllSchedules() throws Exception {
        mockMvc.perform(get("/api/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testCreateSchedule() throws Exception {
        // Mocking: ScheduleService의 createSchedule() 메서드 동작 설정
        Schedule mockSchedule = new Schedule();
        mockSchedule.setId(1L);
        mockSchedule.setName("Test Schedule");

        Mockito.when(scheduleService.createSchedule(Mockito.any(Schedule.class))).thenReturn(mockSchedule);

        // /api/schedules POST 요청 테스트
        String scheduleJson = "{\"name\":\"Test Schedule\"}";

        mockMvc.perform(post("/api/schedules")
                        .contentType(APPLICATION_JSON)
                        .content(scheduleJson))
                .andExpect(status().isOk()) // HTTP 상태 코드 200 확인
                .andExpect(jsonPath("$.id").value(1)) // 반환된 ID 확인
                .andExpect(jsonPath("$.name").value("Test Schedule")); // 반환된 이름 확인
    }
}
