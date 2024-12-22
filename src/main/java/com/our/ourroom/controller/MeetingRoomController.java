package com.our.ourroom.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.our.ourroom.dto.ScheduleRequestDTO;
import com.our.ourroom.dto.TimeRangeDTO;
import com.our.ourroom.entity.MeetingRoom;
import com.our.ourroom.exception.CustomException;
import com.our.ourroom.service.MeetingRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;

    public MeetingRoomController(MeetingRoomService meetingRoomService) {
        this.meetingRoomService = meetingRoomService;
    }

    @Operation(summary = "모든 회의실 조회", description = "등록된 모든 회의실을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 회의실 목록 반환"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<List<MeetingRoom>> getAllMeetingRooms() {
        List<MeetingRoom> meetingRooms = meetingRoomService.getAllMeetingRooms();
        return ResponseEntity.ok(meetingRooms);
    }

    @Operation(summary = "특정 회의실 조회", description = "ID로 특정 회의실 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 회의실 반환"),
            @ApiResponse(responseCode = "404", description = "회의실을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MeetingRoom> getRoomById(
            @Parameter(description = "조회할 회의실 ID", required = true)
            @PathVariable Long id) {
        MeetingRoom room = meetingRoomService.getRoomById(id);
        if (room == null) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(room);
    }


    @PostMapping("/{id}/availability")
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @PathVariable Long id,
            HttpServletRequest request // HttpServletRequest로 원시 데이터를 읽음
    ) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            throw new CustomException("Invalid request", "요청 본문이 비어있습니다.");
        }

        String rawJsonBody = stringBuilder.toString();
        if (rawJsonBody.isEmpty()) {
            throw new CustomException("Invalid request", "요청 본문이 비어있습니다.");
        }

        // JSON 역직렬화 테스트
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        try {
            TimeRangeDTO timeRangeDTO = objectMapper.readValue(rawJsonBody, TimeRangeDTO.class);
            startTime = timeRangeDTO.getStartTime();
            endTime = timeRangeDTO.getEndTime();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (endTime.isBefore(startTime)) {
            throw new CustomException("Invalid request data", "종료 시간은 시작 시간보다 빠를 수 없습니다.");
        }

        MeetingRoom room = meetingRoomService.getRoomById(id);
        if (room == null) {
            throw new CustomException("Resource not found", "회의실을 찾을 수 없습니다.");
        }

        int chcekCnt = meetingRoomService.checkAvailability(id, startTime, endTime);
        if (chcekCnt > 0 ){
            throw new CustomException("Schedule conflict", "선택한 회의실은 해당 시간에 이미 예약되었습니다.");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("details", "회의실 예약이 가능 합니다.");

        return ResponseEntity.ok(response);
    }

}
