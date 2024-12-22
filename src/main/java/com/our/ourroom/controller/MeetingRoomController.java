package com.our.ourroom.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.our.ourroom.dto.TimeRangeDTO;
import com.our.ourroom.entity.MeetingRoom;
import com.our.ourroom.entity.Schedule;
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
import org.springframework.web.util.ContentCachingRequestWrapper;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            throw new CustomException("Resource not found", "회의실을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok(room);
    }

    @Operation(summary = "회의실 예약 가능 여부 확인", description = "특정 시간에 회의실의 예약 가능 여부를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 가능 여부 확인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "회의실을 찾을 수 없음")
    })
    @PostMapping("/{id}/availability")
    public ResponseEntity<Map<String, Object>> checkRoomAvailability(
            @PathVariable Long id,
            HttpServletRequest request) throws IOException {

        ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;
        // 요청 본문 읽기
        String body = new String(cachingRequest.getContentAsByteArray(), request.getCharacterEncoding());

        // ObjectMapper로 JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        TimeRangeDTO timeRange = objectMapper.readValue(body, TimeRangeDTO.class);

        if (timeRange == null) {
            throw new CustomException("Invalid request", "요청 본문이 비어있습니다.");
        }

        LocalDateTime startTime = LocalDateTime.parse(timeRange.getStartTime());
        LocalDateTime endTime = LocalDateTime.parse(timeRange.getEndTime());

        if (endTime.isBefore(startTime)) {
            throw new CustomException("Invalid request data", "종료 시간은 시작 시간보다 빠를 수 없습니다.");
        }

        MeetingRoom room = meetingRoomService.getRoomById(id);
        if (room == null) {
            throw new CustomException("Resource not found", "회의실을 찾을 수 없습니다.");
        }

        boolean isAvailable = meetingRoomService.isRoomAvailable(id, startTime, endTime);
        Map<String, Object> response = new HashMap<>();
        response.put("roomId", id);
        response.put("message", isAvailable ? "예약 가능." : "예약 불가능.");
        if (!isAvailable){
            throw new CustomException("Schedule conflict", "선택한 회의실은 해당 시간에 이미 예약되었습니다.");
        }
        return ResponseEntity.ok(response);
//        return ResponseEntity.ok().build();

    }
}
