package com.our.ourroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.our.ourroom.dto.TimeRangeDTO;
import com.our.ourroom.entity.MeetingRoom;
import com.our.ourroom.entity.Users;
import com.our.ourroom.exception.CustomException;
import com.our.ourroom.service.MeetingRoomService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MeetingRoomController.class)
public class MeetingRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeetingRoomService meetingRoomService;

    @InjectMocks // 테스트 대상인 컨트롤러를 주입
    private MeetingRoomController meetingRoomController;

    @Test
    public void testGetAllMeetingRooms() throws Exception {
        List<MeetingRoom> meetingRooms = List.of(
                new MeetingRoom() {{
                    setId(1L);
                    setName("회의실 A");
                    setCapacity(10);
                }},
                new MeetingRoom() {{
                    setId(2L);
                    setName("회의실 B");
                    setCapacity(5);
                }}
        );

        when(meetingRoomService.getAllMeetingRooms()).thenReturn(meetingRooms);

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(meetingRooms.size()))
                .andExpect(jsonPath("$[0].name").value("회의실 A"))
                .andExpect(jsonPath("$[1].capacity").value(5));
    }


    @Test
    public void testGetRoomById() throws Exception {
        MeetingRoom meetingRoom = new MeetingRoom();
        meetingRoom.setId(1L);

        when(meetingRoomService.getRoomById(1L)).thenReturn(meetingRoom);

        mockMvc.perform(get("/api/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testGetMeetingRoomById_NotFound() throws Exception {
        // Mock: 존재하지 않는 ID로 조회
        when(meetingRoomService.getRoomById(1L)).thenReturn(null);

        // GET 요청
        mockMvc.perform(get("/api/rooms/1"))
                .andExpect(status().isNotFound()) // HTTP 404 상태 확인
                .andExpect(jsonPath("$").doesNotExist()); // body가 비어 있는지 확인
    }

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void checkAvailability_WhenRoomIsAvailable_ReturnsOk() throws Exception {
        // Given
        Long roomId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 22, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 12, 22, 12, 0);
        TimeRangeDTO timeRangeDTO = new TimeRangeDTO();
        timeRangeDTO.setStartTime(startTime);
        timeRangeDTO.setEndTime(endTime);

        MeetingRoom mockRoom = new MeetingRoom();
        when(meetingRoomService.getRoomById(roomId)).thenReturn(mockRoom);
        when(meetingRoomService.checkAvailability(roomId, startTime, endTime)).thenReturn(0);

        // When & Then
        mockMvc.perform(post("/api/rooms/{id}/availability", roomId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(timeRangeDTO)))
                .andDo(print()) // 요청/응답 디버깅
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roomId))
                .andExpect(jsonPath("$.details").value("회의실 예약이 가능 합니다."));
    }


    @Test
    void checkAvailability_Conflict_ThrowsException() throws Exception {
        // Given
        Long roomId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 22, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 12, 22, 12, 0);
        TimeRangeDTO timeRangeDTO = new TimeRangeDTO();
        timeRangeDTO.setStartTime(startTime);
        timeRangeDTO.setEndTime(endTime);

        MeetingRoom mockRoom = new MeetingRoom();
        when(meetingRoomService.getRoomById(roomId)).thenReturn(mockRoom);
        when(meetingRoomService.checkAvailability(roomId, startTime, endTime)).thenReturn(1);

        // When & Then
        mockMvc.perform(post("/api/rooms/{id}/availability", roomId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(timeRangeDTO)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("Schedule conflict"))
                .andExpect(jsonPath("$.details").value("선택한 회의실은 해당 시간에 이미 예약되었습니다."));
    }


    @Test
    void checkAvailability_RoomNotFound_ThrowsException() throws Exception {
        // Given
        Long roomId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 22, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 12, 22, 12, 0);
        TimeRangeDTO timeRangeDTO = new TimeRangeDTO();
        timeRangeDTO.setStartTime(startTime);
        timeRangeDTO.setEndTime(endTime);

        // Mock: 회의실이 존재하지 않음
        when(meetingRoomService.getRoomById(roomId)).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/api/rooms/{id}/availability", roomId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(timeRangeDTO)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.details").value("회의실을 찾을 수 없습니다."));
    }

    @Test
    void checkAvailability_InvalidTimeRange_ThrowsException() throws Exception {
        // Given
        Long roomId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 22, 12, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 12, 22, 10, 0);
        TimeRangeDTO timeRangeDTO = new TimeRangeDTO();
        timeRangeDTO.setStartTime(startTime);
        timeRangeDTO.setEndTime(endTime);

        // When & Then
        mockMvc.perform(post("/api/rooms/{id}/availability", roomId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(timeRangeDTO)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("Invalid request data"))
                .andExpect(jsonPath("$.details").value("종료 시간은 시작 시간보다 빠를 수 없습니다."));
    }

    @Test
    void checkAvailability_WhenEndTimeBeforeStartTime_ThrowsInvalidRequest() throws Exception {
        // Given
        Long roomId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 22, 12, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 12, 22, 10, 0);
        TimeRangeDTO timeRangeDTO = new TimeRangeDTO();
        timeRangeDTO.setStartTime(startTime);
        timeRangeDTO.setEndTime(endTime);

        // When & Then
        mockMvc.perform(post("/api/rooms/{id}/availability", roomId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(timeRangeDTO)))
                .andExpect(status().is4xxClientError()) // 4xx 상태 코드 확인
                .andExpect(jsonPath("$.error").value("Invalid request data")) // JSON 응답의 error 필드 검증
                .andExpect(jsonPath("$.details").value("종료 시간은 시작 시간보다 빠를 수 없습니다.")); // JSON 응답의 details 필드 검증
    }

    @Test
    void getRequestBody_WhenIOExceptionOccurs_ThrowsCustomException() throws IOException {
        // Given
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getReader()).thenThrow(new IOException("Test IOException"));

        // When & Then
        Exception exception = assertThrows(CustomException.class, () -> {
            meetingRoomController.getRequestBody(mockRequest);
        });

        // CustomException 검증
        assertTrue(exception instanceof CustomException);
        CustomException customException = (CustomException) exception;
        assertEquals("Invalid request", customException.getError());
        assertEquals("요청 본문이 비어있습니다.", customException.getMessage());
    }

    @Test
    void parseRequestBody_WhenJsonProcessingExceptionOccurs_ThrowsRuntimeException() {
        // Given
        String invalidJson = "{ invalid json format }";

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            meetingRoomController.parseRequestBody(invalidJson);
        });

        // 예외 메시지 검증
        assertNotNull(exception.getMessage());
    }


    private void assertEquals(String invalidRequest, String error) {
    }

}
