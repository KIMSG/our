package com.our.ourroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.our.ourroom.dto.ScheduleRequestDTO;
import com.our.ourroom.entity.Schedule;
import com.our.ourroom.exception.CustomException;
import com.our.ourroom.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@WebMvcTest(ScheduleController.class)
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

    @InjectMocks
    private ScheduleController scheduleController;

    private Schedule testSchedule;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testSchedule = new Schedule();
        testSchedule.setId(1L);
        testSchedule.setName("Team Meeting");
        objectMapper = new ObjectMapper();
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


    @Test
    public void testDeleteScheduleById_Success() throws Exception {
        when(scheduleService.deleteScheduleById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/schedules/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteScheduleById_NotFound() throws Exception {
        when(scheduleService.deleteScheduleById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/schedules/1"))
                .andExpect(status().isBadRequest()) // CustomException 반환
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.details").value("삭제할 일정을 찾을 수 없습니다."));
    }

    @Test
    public void testUpdateSchedule_Success() throws Exception {
        Schedule updatedSchedule = new Schedule();
        updatedSchedule.setId(1L);
        updatedSchedule.setName("Updated Meeting");

        when(scheduleService.updateSchedule(anyLong(), any(ScheduleRequestDTO.class))).thenReturn(updatedSchedule);

        String updateJson = "{" +
                "\"name\":\"Updated Meeting\"," +
                "\"startTime\":\"2024-12-23T10:00:00\"," +
                "\"endTime\":\"2024-12-23T11:00:00\"," +
                "\"meetingRoomId\":1," +
                "\"participantIds\":[1,2]}" +
                "}";

        mockMvc.perform(put("/api/schedules/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Meeting"));
    }


    @Test
    public void testAddParticipants_Success_MultipleIds() throws Exception {
        // Arrange
        Long scheduleId = 1L;
        Map<String, Object> request = Map.of("id", List.of(10L, 11L));

        doNothing().when(scheduleService).addParticipantsToSchedule(scheduleId, List.of(10L, 11L));

        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/api/schedules/{id}/participants", scheduleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids[0]").value(10L))
                .andExpect(jsonPath("$.ids[1]").value(11L))
                .andExpect(jsonPath("$.details").value("일정에 참여자가 등록 되었습니다."));

        // Verify
        verify(scheduleService, times(1)).addParticipantsToSchedule(scheduleId, List.of(10L, 11L));
    }
    @Test
    public void testAddParticipants_Failure_NoId() throws Exception {
        // Arrange
        Long scheduleId = 1L;
        Map<String, Object> request = new HashMap<>();

        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/api/schedules/{id}/participants", scheduleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddParticipants_Failure_InvalidType() throws Exception {
        // Arrange
        Long scheduleId = 1L;
        Map<String, Object> request = Map.of("id", "invalid");

        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/api/schedules/{id}/participants", scheduleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void testAddParticipants_Failure_IllegalArgumentException() throws Exception {
        // Arrange
        Long scheduleId = 1L;
        Map<String, Object> invalidRequest = Map.of("id", List.of("invalid")); // 유효하지 않은 데이터

        // Mocking: extractUserIds에서 IllegalArgumentException 발생 설정
        doThrow(new IllegalArgumentException()).when(scheduleService).addParticipantsToSchedule(eq(scheduleId), any());

        String requestJson = objectMapper.writeValueAsString(invalidRequest);

        // Act & Assert
        mockMvc.perform(post("/api/schedules/{id}/participants", scheduleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request"))
                .andExpect(jsonPath("$.details").value("요청 본문이 비어있습니다."));
    }

    @Test
    public void testExtractUserIds_SingleNumber() {
        // Arrange
        Object idObject = 10L; // 단일 Number 입력

        // Act
        List<Long> result = scheduleController.extractUserIds(idObject);

        // Assert
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0));
    }

    @Test
    public void testExtractUserIds_SingleInteger() {
        // Arrange
        Object idObject = 10; // Integer 타입 입력

        // Act
        List<Long> result = scheduleController.extractUserIds(idObject);

        // Assert
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0));
    }

    @Test
    public void testRemoveParticipant() throws Exception {
        mockMvc.perform(delete("/schedules/1/participants/1"))
                .andExpect(status().isNoContent());
    }
}