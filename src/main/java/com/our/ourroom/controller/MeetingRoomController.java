package com.our.ourroom.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.our.ourroom.dto.TimeRangeDTO;
import com.our.ourroom.entity.MeetingRoom;
import com.our.ourroom.exception.CustomException;
import com.our.ourroom.service.MeetingRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 회의실 관리 API 컨트롤러
 * 회의실 조회, 예약 가능 여부 확인 등의 기능을 제공합니다.
 */
@Tag(name = "회의실 API", description = "회의실 관리와 관련된 API 제공")
@RestController
@RequestMapping("/api/rooms")
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;

    /**
     * MeetingRoomController 생성자
     * @param meetingRoomService 회의실 관리 서비스 객체
     */
    public MeetingRoomController(MeetingRoomService meetingRoomService) {
        this.meetingRoomService = meetingRoomService;
    }

    /**
     * 모든 회의실 조회
     * @return 등록된 모든 회의실 리스트
     */
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

    /**
     * 특정 회의실 조회
     * @param id 조회할 회의실 ID
     * @return 조회된 회의실 객체
     */
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

    /**
     * 회의실 예약 가능 여부 확인
     * @param id 회의실 ID
     * @param request HTTP 요청 객체 (JSON 본문 포함)
     * @return 예약 가능 여부 결과
     */
    @Operation(
            summary = "회의실 예약 가능 여부 확인",
            description = "특정 회의실의 지정된 시간 범위 내 예약 가능 여부를 확인합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회의실 예약 가능",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 시간 범위가 유효하지 않음)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomException.class))),
            @ApiResponse(responseCode = "404", description = "회의실을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomException.class))),
            @ApiResponse(responseCode = "409", description = "해당 시간에 이미 예약된 회의실",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomException.class)))
    })
    @PostMapping("/{id}/availability")
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @PathVariable Long id,
            HttpServletRequest request // HttpServletRequest로 원시 데이터를 읽음
    ) {
        // 요청 본문 읽기
        String rawJsonBody = getRequestBody(request);

        // JSON 역직렬화 테스트
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        TimeRangeDTO timeRangeDTO = parseRequestBody(rawJsonBody);
        LocalDateTime startTime = timeRangeDTO.getStartTime();
        LocalDateTime endTime = timeRangeDTO.getEndTime();

        // 시간 유효성 검사
        if (endTime.isBefore(startTime)) {
            throw new CustomException("Invalid request data", "종료 시간은 시작 시간보다 빠를 수 없습니다.");
        }

        // 회의실 ID 확인
        MeetingRoom room = meetingRoomService.getRoomById(id);
        if (room == null) {
            throw new CustomException("Resource not found", "회의실을 찾을 수 없습니다.");
        }

        // 예약 가능 여부 확인
        int chcekCnt = meetingRoomService.checkAvailability(id, startTime, endTime);
        if (chcekCnt > 0 ){
            throw new CustomException("Schedule conflict", "선택한 회의실은 해당 시간에 이미 예약되었습니다.");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("details", "회의실 예약이 가능 합니다.");

        return ResponseEntity.ok(response);
    }

    /**
     * HTTP 요청 본문을 문자열로 읽어오는 메서드
     * @param request HTTP 요청 객체
     * @return JSON 문자열
     */
    protected String getRequestBody(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            throw new CustomException("Invalid request", "요청 본문이 비어있습니다.");
        }
        return stringBuilder.toString();
    }

    /**
     * JSON 문자열을 TimeRangeDTO 객체로 변환하는 메서드
     * @param rawJsonBody JSON 문자열
     * @return TimeRangeDTO 객체
     */
    protected TimeRangeDTO parseRequestBody(String rawJsonBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            return objectMapper.readValue(rawJsonBody, TimeRangeDTO.class);
        } catch (JsonProcessingException e) {
            throw new CustomException("Invalid JSON", "JSON 파싱 중 오류가 발생했습니다.");
        }
    }


}
