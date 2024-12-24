package com.our.ourroom.utils;

import com.our.ourroom.dto.ScheduleRequestDTO;
import com.our.ourroom.entity.MeetingRoom;
import com.our.ourroom.entity.Schedule;
import com.our.ourroom.entity.Users;
import com.our.ourroom.exception.CustomException;
import com.our.ourroom.repository.MeetingRoomRepository;
import com.our.ourroom.repository.ScheduleRepository;
import com.our.ourroom.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ScheduleValidationUtils
 * 일정 생성 및 수정 시 필요한 다양한 검증 로직을 제공하는 유틸리티 클래스입니다.
 */
@Component
public class ScheduleValidationUtils {

    private final ScheduleRepository scheduleRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final UserRepository userRepository;


    public ScheduleValidationUtils(ScheduleRepository scheduleRepository, MeetingRoomRepository meetingRoomRepository, UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.meetingRoomRepository = meetingRoomRepository;
        this.userRepository = userRepository;
    }

    /**
     * 회의실 검증
     * @param meetingRoomId 회의실 ID
     * @return 유효한 MeetingRoom 객체
     */
    public MeetingRoom validateMeetingRoom(Long meetingRoomId) {
        return meetingRoomRepository.findById(meetingRoomId)
                .orElseThrow(() -> new CustomException("Resource not found", "요청한 일정 또는 회의실을 찾을 수 없습니다."));
    }

    /**
     * 참가자 검증
     * @param participantIds 참가자 ID 리스트
     * @param roomCapacity 회의실 최대 수용 인원
     * @return 유효한 사용자 리스트
     */
    public List<Users> validateParticipants(List<Long> participantIds, int roomCapacity) {
        if (participantIds.isEmpty()) {
            System.out.println("participantIds is empty: " + participantIds); // 디버깅
            throw new CustomException("Invalid users", "일정을 생성하려면 최소 1명 이상의 참가자가 필요합니다.");
        }
        List<Users> participants = userRepository.findAllById(participantIds);
        System.out.println("participants found: " + participants); // 디버깅

        // 요청된 사용자 ID 리스트와 실제 존재하는 사용자 ID 리스트 비교
        List<Long> foundIds = participants.stream().map(Users::getId).toList();
        List<Long> invalidIds = participantIds.stream().filter(id -> !foundIds.contains(id)).toList();

        if (!invalidIds.isEmpty()) {
            throw new CustomException("Invalid users", "다음 사용자 ID는 유효하지 않습니다: " + invalidIds);
        }

        if (participants.size() > roomCapacity) {
            throw new CustomException("Exceeding room capacity", "회의실의 최대 수용 가능 인원을 초과했습니다.");
        }

        return participants;
    }

    /**
     * 시간 충돌 검증
     * @param dto 일정 요청 DTO
     */
    public void validateTimeConflict(ScheduleRequestDTO dto) {
        // [기존 로직]
        // 입력된 시간에 대한 유효성을 체크
        if(dto.getEndTime().isBefore(dto.getStartTime())){
            throw new CustomException("Invalid request data", "종료 시간은 시작 시간보다 빠를 수 없습니다.");
        }
        // 시간 충돌 검증
        List<Schedule> conflictingSchedules = scheduleRepository.findConflictingSchedules(
                dto.getMeetingRoomId(), dto.getStartTime(), dto.getEndTime());

        if (!conflictingSchedules.isEmpty()) {
            throw new CustomException("Schedule conflict", "선택한 회의실은 해당 시간에 이미 예약되었습니다.");
        }

    }

    /**
     * 시간 충돌 검증 (자신 제외)
     * @param id 일정 ID
     * @param dto 일정 요청 DTO
     */
    public void validateTimeConflictExcludingSelf(Long id, ScheduleRequestDTO dto) {
        // [보완된 로직]
        // - 현재 검증 중인 스케줄 ID(scheduleId)를 제외한 시간 충돌 검증.
        if(dto.getEndTime().isBefore(dto.getStartTime())){
            throw new CustomException("Invalid request data", "종료 시간은 시작 시간보다 빠를 수 없습니다.");
        }
        // 시간 충돌 검증
        List<Schedule> conflictingSchedules = scheduleRepository.findConflictingSchedulesExcludingSelf(
                id, dto.getMeetingRoomId(), dto.getStartTime(), dto.getEndTime());

        if (!conflictingSchedules.isEmpty()) {
            throw new CustomException("Schedule conflict", "선택한 회의실은 해당 시간에 이미 예약되었습니다.");
        }
    }

    /**
     * 사용자 충돌 검증
     * @param dto 일정 요청 DTO
     */
    public void validateUserConflict(ScheduleRequestDTO dto) {

        List<Schedule> conflictingUsers = scheduleRepository.findConflictingUsers(
                dto.getParticipantIds(), dto.getStartTime(), dto.getEndTime());

        if (!conflictingUsers.isEmpty()) {
            throw new CustomException("User conflict", "선택한 시간 동안 일부 사용자가 이미 다른 회의에 참석 중입니다.");
        }
    }

    /**
     * 사용자 충돌 검증 (자신 제외)
     * @param id 일정 ID
     * @param dto 일정 요청 DTO
     */
    public void validateUserConflictExcludingSelf(Long id, ScheduleRequestDTO dto) {

        List<Schedule> conflictingUsers = scheduleRepository.findConflictingUsersExcludingSelf(
                id, dto.getParticipantIds(), dto.getStartTime(), dto.getEndTime());

        if (!conflictingUsers.isEmpty()) {
            throw new CustomException("User conflict", "선택한 시간 동안 일부 사용자가 이미 다른 회의에 참석 중입니다.");
        }
    }
}
