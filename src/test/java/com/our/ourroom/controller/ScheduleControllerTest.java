package com.our.ourroom.controller;

import com.our.ourroom.dto.ScheduleRequestDTO;
import com.our.ourroom.entity.Schedule;
import com.our.ourroom.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ScheduleController.class)
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateSchedule_Success() throws Exception {
        // Mocking: ScheduleService의 createSchedule 메서드
        Schedule mockSchedule = new Schedule();
        mockSchedule.setId(1L);
        mockSchedule.setName("Team Meeting");

        when(scheduleService.createSchedule(any(ScheduleRequestDTO.class))).thenReturn(mockSchedule);

        // /api/schedules POST 요청 테스트
        String scheduleJson = "{" +
                "\"name\":\"Team Meeting\"," +
                "\"startTime\":\"2024-12-22T10:00:00\"," +
                "\"endTime\":\"2024-12-22T11:00:00\"," +
                "\"meetingRoomId\":1," +
                "\"participantIds\":[1,2,3]}";

        mockMvc.perform(post("/api/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(scheduleJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Team Meeting"));
    }
}