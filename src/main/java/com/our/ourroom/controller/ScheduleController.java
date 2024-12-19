package com.our.ourroom.controller;

import com.our.ourroom.entity.Schedule;
import com.our.ourroom.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ScheduleController
 * 일정(Schedule) 관련 API를 제공하는 컨트롤러입니다.
 * - GET: 모든 일정 조회
 * - POST: 새로운 일정 생성
 */
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }
    /**
     * 모든 일정을 조회하는 API
     * @return 모든 일정 목록(List<Schedule>)
     */
    @Operation(summary = "모든 일정 조회", description = "등록된 모든 일정을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 일정 조회",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Schedule.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public List<Schedule> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    /**
     * 새로운 일정을 생성하는 API
     * @param schedule 클라이언트에서 요청한 일정 데이터 (JSON 형식)
     * @return 생성된 일정 객체
     */
    @Operation(summary = "새로운 일정 생성", description = "새로운 일정을 생성하고 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "일정 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Schedule.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public Schedule createSchedule(@RequestBody Schedule schedule) {
        return scheduleService.createSchedule(schedule);
    }
}
