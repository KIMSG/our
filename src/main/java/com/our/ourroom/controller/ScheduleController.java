package com.our.ourroom.controller;

import com.our.ourroom.dto.ScheduleRequestDTO;
import com.our.ourroom.entity.Users;
import com.our.ourroom.exception.CustomException;
import com.our.ourroom.service.ScheduleService;
import com.our.ourroom.entity.Schedule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "일정 API", description = "일정 관리와 관련된 API 제공")
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Operation(summary = "새로운 일정 생성", description = "새로운 일정을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "일정 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@Valid @RequestBody ScheduleRequestDTO dto) {

        Schedule createdSchedule = scheduleService.createSchedule(dto);
        return ResponseEntity.status(201).body(createdSchedule);
    }

    @Operation(summary = "모든 일정 조회", description = "등록된 모든 일정을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 일정 목록 반환")
    @GetMapping
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "특정 일정 조회", description = "ID로 특정 일정 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 일정 반환"),
            @ApiResponse(responseCode = "404", description = "일정을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(
            @Parameter(description = "조회할 일정 ID", required = true)
            @PathVariable Long id) {
        Schedule schedule = scheduleService.getScheduleById(id);
        if (schedule == null) {
            throw new CustomException("Resource not found", "요청한 일정을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok(schedule);
    }

    @Operation(summary = "특정 일정 삭제", description = "ID로 특정 일정을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 일정 삭제"),
            @ApiResponse(responseCode = "404", description = "삭제할 일정을 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleById(
            @Parameter(description = "삭제할 일정 ID", required = true)
            @PathVariable Long id) {
        boolean deleted = scheduleService.deleteScheduleById(id);
        if (!deleted) {
            throw new CustomException("Resource not found", "삭제할 일정을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "일정 수정", description = "특정 일정의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "수정할 일정을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(
            @Parameter(description = "수정할 일정 ID", required = true) @PathVariable Long id,
            @RequestBody ScheduleRequestDTO dto) {
        Schedule updatedSchedule = scheduleService.updateSchedule(id, dto);
        return ResponseEntity.ok(updatedSchedule);
    }


    @PostMapping("/{id}/participants")
    public ResponseEntity<String> addParticipant(
            @PathVariable("id") Long scheduleId,
            @RequestBody Users users) {
        try {
            scheduleService.addParticipantToSchedule(scheduleId, users.getId());
            return ResponseEntity.ok("참여자가 성공적으로 추가되었습니다.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

}