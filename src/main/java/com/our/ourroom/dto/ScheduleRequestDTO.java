package com.our.ourroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "일정 요청 데이터를 표현하는 DTO")
@Getter
@Setter
@ToString
public class ScheduleRequestDTO {

    @NotBlank(message = "회의명이 빈 값 입니다.")
    @Schema(description = "일정 이름", example = "팀 회의", required = true)
    private String name;

    @NotBlank(message = "일정 시작 시간이 빈 값 입니다.")
    @Schema(description = "일정 시작 시간", example = "2024-12-22T10:00:00", required = true)
    private LocalDateTime startTime;

    @NotBlank(message = "일정 종료 시간이 빈 값 입니다.")
    @Schema(description = "일정 종료 시간", example = "2024-12-22T11:00:00", required = true)
    private LocalDateTime endTime;

    @NotNull(message = "회의실 ID 목록이 빈 값 입니다.")
    @Schema(description = "회의실 ID", example = "1", required = true)
    private Long meetingRoomId;

    @NotNull(message = "참가자 ID 목록이 빈 값 입니다.")
    @Schema(description = "참가자 ID 목록", example = "[1, 2, 3]", required = true)
    private List<Long> participantIds;


}
