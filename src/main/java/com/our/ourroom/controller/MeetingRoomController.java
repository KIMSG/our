package com.our.ourroom.controller;

import com.our.ourroom.entity.MeetingRoom;
import com.our.ourroom.service.MeetingRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
