package com.our.ourroom.controller;

import com.our.ourroom.entity.Schedule;
import com.our.ourroom.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

    private Schedule testSchedule;

    @BeforeEach
    void setUp() {
        testSchedule = new Schedule();
        testSchedule.setId(1L);
        testSchedule.setName("Test Schedule");
    }

    @Test
    public void testCreateSchedule() throws Exception {
//        when(scheduleService.createSchedule(any(Schedule.class))).thenReturn(testSchedule);
//
//        String scheduleJson = "{\"name\":\"Test Schedule\"}";
//
//        mockMvc.perform(post("/api/schedules")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(scheduleJson))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.name").value("Test Schedule"));
    }
}