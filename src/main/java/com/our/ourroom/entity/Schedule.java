package com.our.ourroom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Schedule 엔티티
 * - 일정 정보를 저장하는 엔티티로, 이름, 시작/종료 시간, 회의실, 사용자 정보 등을 포함합니다.
 */
@Entity
@Getter
@Setter
@ToString
public class Schedule {
    /**
     * 일정 고유 ID
     * - 자동 생성되는 기본 키 값
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 일정 이름
     * - 일정의 이름 또는 제목
     */
    private String name;

    /**
     * 일정 시작 시간
     * - LocalDateTime으로 일정 시작 시간을 저장
     */
    private LocalDateTime startTime;

    /**
     * 일정 종료 시간
     * - LocalDateTime으로 일정 종료 시간을 저장
     */
    private LocalDateTime endTime;

    /**
     * 관련된 회의실
     * - ManyToOne 관계: 하나의 회의실에서 여러 일정이 생성될 수 있음
     */
    @ManyToOne
    private MeetingRoom meetingRoom;

    /**
     * 관련된 사용자
     * - ManyToOne 관계: 하나의 사용자가 여러 일정을 가질 수 있음
     */
    @OneToMany
    private List<Users> participants;

}