package com.our.ourroom.service;

import com.our.ourroom.dto.ScheduleRequestDTO;
import com.our.ourroom.entity.MeetingRoom;
import com.our.ourroom.entity.Users;
import com.our.ourroom.exception.CustomException;
import com.our.ourroom.repository.MeetingRoomRepository;
import com.our.ourroom.repository.ScheduleRepository;
import com.our.ourroom.entity.Schedule;
import com.our.ourroom.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final UserRepository userRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, MeetingRoomRepository meetingRoomRepository, UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.meetingRoomRepository = meetingRoomRepository;
        this.userRepository = userRepository;
    }

    public Schedule createSchedule(ScheduleRequestDTO dto) {
        // MeetingRoom 유효성 검증
        MeetingRoom meetingRoom = validateMeetingRoom(dto.getMeetingRoomId());
        List<Users> participants = validateParticipants(dto.getParticipantIds(), meetingRoom.getCapacity());


        //todo : 한 회의실은 동일 시간에 여러 일정에 할당될 수 없습니다.
        //todo : 한 참여자는 동일 시간에 여러 일정에 참여할 수 없습니다.
        //todo : 현재 시점보다 과거에 시작하는 일정은 생성할 수 없습니다.

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

        MeetingRoom meetingRoom = validateMeetingRoom(dto.getMeetingRoomId());
        List<Users> participants = validateParticipants(dto.getParticipantIds(), meetingRoom.getCapacity());

        // 일정 정보 수정
        schedule.setName(dto.getName());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setMeetingRoom(meetingRoom);
        schedule.setParticipants(participants);

        return scheduleRepository.save(schedule);
    }


    private MeetingRoom validateMeetingRoom(Long meetingRoomId) {
        return meetingRoomRepository.findById(meetingRoomId)
                .orElseThrow(() -> new CustomException("Resource not found", "요청한 일정 또는 회의실을 찾을 수 없습니다."));
    }

    private List<Users> validateParticipants(List<Long> participantIds, int roomCapacity) {
        List<Users> participants = userRepository.findAllById(participantIds);

        // 요청된 사용자 ID 리스트와 실제 존재하는 사용자 ID 리스트 비교
        List<Long> foundIds = participants.stream().map(Users::getId).toList();
        List<Long> invalidIds = participantIds.stream().filter(id -> !foundIds.contains(id)).toList();

        if (!invalidIds.isEmpty()) {
            throw new CustomException("Invalid users", "다음 사용자 ID는 유효하지 않습니다: " + invalidIds);
        }

        if (participants.isEmpty()) {
            throw new CustomException("Exceeding room capacity", "일정을 생성하려면 최소 1명의 참가자가 필요합니다.");
        }

        if (participants.size() > roomCapacity) {
            throw new CustomException("Exceeding room capacity", "회의실의 최대 수용 가능 인원을 초과했습니다.");
        }

        return participants;
    }

}
