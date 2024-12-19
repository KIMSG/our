package com.our.ourroom.controller;

import com.our.ourroom.entity.Schedule;
import com.our.ourroom.service.ScheduleService;
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

    @GetMapping
    public List<Schedule> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    @PostMapping
    public Schedule createSchedule(@RequestBody Schedule schedule) {
        return scheduleService.createSchedule(schedule);
    }
}
