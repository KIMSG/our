package com.our.ourroom.service;

import com.our.ourroom.dto.ScheduleRequestDTO;
import com.our.ourroom.entity.MeetingRoom;
import com.our.ourroom.entity.ScheduleParticipant;
import com.our.ourroom.entity.Users;
import com.our.ourroom.exception.CustomException;
import com.our.ourroom.repository.ScheduleParticipantRepository;
import com.our.ourroom.repository.ScheduleRepository;
import com.our.ourroom.entity.Schedule;
import com.our.ourroom.utils.ScheduleValidationUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleValidationUtils scheduleValidationUtils;
    private final ScheduleParticipantRepository scheduleParticipantRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, ScheduleValidationUtils scheduleValidationUtils, ScheduleParticipantRepository scheduleParticipantRepository) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleValidationUtils = scheduleValidationUtils;
        this.scheduleParticipantRepository = scheduleParticipantRepository;
    }

    public Schedule createSchedule(ScheduleRequestDTO dto) {
        // MeetingRoom 유효성 검증
        MeetingRoom meetingRoom = scheduleValidationUtils.validateMeetingRoom(dto.getMeetingRoomId());
        List<Users> participants = scheduleValidationUtils.validateParticipants(dto.getParticipantIds(), meetingRoom.getCapacity());

        scheduleValidationUtils.validateTimeConflict(dto);
        scheduleValidationUtils.validateUserConflict(dto);

        // Schedule 생성 및 저장
        Schedule schedule = new Schedule();
        schedule.setName(dto.getName());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setMeetingRoom(meetingRoom);
        schedule.setParticipants(participants);

        return scheduleRepository.save(schedule);
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }

    public boolean deleteScheduleById(Long id) {
        if (!scheduleRepository.existsById(id)) {
            return false;
        }
        scheduleRepository.deleteById(id);
        return true;
    }

    public Schedule updateSchedule(Long id, ScheduleRequestDTO dto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Resource not found", "수정할 일정을 찾을 수 없습니다."));

        MeetingRoom meetingRoom = scheduleValidationUtils.validateMeetingRoom(dto.getMeetingRoomId());
        List<Users> participants = scheduleValidationUtils.validateParticipants(dto.getParticipantIds(), meetingRoom.getCapacity());

        scheduleValidationUtils.validateTimeConflict(dto);
        scheduleValidationUtils.validateUserConflict(dto);

        // 일정 정보 수정
        schedule.setName(dto.getName());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setMeetingRoom(meetingRoom);
        schedule.setParticipants(participants);

        return scheduleRepository.save(schedule);
    }


/*  단건에 대한 사용자 id만 스케쥴에 저장할 수 있습니다.
    public void addParticipantToSchedule(Long scheduleId, Long id) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException("Resource not found", "수정할 일정을 찾을 수 없습니다."));

        boolean isOverlapping = scheduleParticipantRepository.existsByUserIdAndOverlappingSchedule(
                id, schedule.getStartTime(), schedule.getEndTime()
        );

        if (isOverlapping) {
            throw new CustomException("users conflict", "동일 시간대에 이미 다른 회의에 참여 중입니다.");
        }

        ScheduleParticipant participant = new ScheduleParticipant();
        participant.setScheduleId(scheduleId);
        participant.setUserId(id);
        scheduleParticipantRepository.save(participant);
    }*/

    public void addParticipantsToSchedule(Long scheduleId, List<Long> userIds) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException("Resource not found", "수정할 일정을 찾을 수 없습니다."));

        for (Long userId : userIds) {
            if (scheduleParticipantRepository.existsByUserIdAndOverlappingSchedule(
                    userId, schedule.getStartTime(), schedule.getEndTime())) {
                throw new CustomException("users conflict", "동일 시간대에 이미 다른 회의에 참여 중입니다.");
            }

            ScheduleParticipant participant = new ScheduleParticipant();
            participant.setScheduleId(schedule.getId());
            participant.setUserId(userId);
            scheduleParticipantRepository.save(participant);
        }
    }

    @Transactional
    public void removeParticipant(Long scheduleId, Long userId) {
        boolean exists = scheduleParticipantRepository.existsByScheduleIdAndUserId(scheduleId, userId);
        if (!exists) {
            throw new CustomException("Participant not found in the schedule", "스케줄 ID에서 사용자 ID를 찾을 수 없습니다.");
        }

        scheduleParticipantRepository.deleteByScheduleIdAndUserId(scheduleId, userId);
    }
}
