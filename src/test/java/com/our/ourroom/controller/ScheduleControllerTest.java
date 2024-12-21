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
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ScheduleController.class)
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

    private Schedule testSchedule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testSchedule = new Schedule();
        testSchedule.setId(1L);
        testSchedule.setName("Team Meeting");
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

    @Test
    public void testGetAllSchedules() throws Exception {
        List<Schedule> schedules = List.of(testSchedule);
        when(scheduleService.getAllSchedules()).thenReturn(schedules);

        mockMvc.perform(get("/api/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(schedules.size()))
                .andExpect(jsonPath("$[0].name").value("Team Meeting"));
    }

    @Test
    public void testGetScheduleById_Success() throws Exception {
        when(scheduleService.getScheduleById(1L)).thenReturn(testSchedule);

        mockMvc.perform(get("/api/schedules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Team Meeting"));
    }

    @Test
    public void testGetScheduleById_NotFound() throws Exception {
        when(scheduleService.getScheduleById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/schedules/100"))
                .andExpect(status().isBadRequest()) // 404 상태 코드 확인
                .andExpect(jsonPath("$.error").value("Resource not found")) // error 메시지 확인
                .andExpect(jsonPath("$.details").value("요청한 일정을 찾을 수 없습니다.")); // details 확인
    }

}