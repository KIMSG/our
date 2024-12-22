package com.our.ourroom.service;

import com.our.ourroom.entity.MeetingRoom;
import com.our.ourroom.entity.Schedule;
import com.our.ourroom.repository.MeetingRoomRepository;
import com.our.ourroom.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;
    private final ScheduleRepository scheduleRepository;

    public MeetingRoomService(MeetingRoomRepository meetingRoomRepository, ScheduleRepository scheduleRepository) {
        this.meetingRoomRepository = meetingRoomRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public List<MeetingRoom> getAllMeetingRooms() {
        return meetingRoomRepository.findAll();
    }

    public MeetingRoom getRoomById(Long id) {
        return meetingRoomRepository.findById(id).orElse(null);
    }

    public boolean isRoomAvailable(Long id, LocalDateTime startTime, LocalDateTime endTime) {
        List<Schedule> conflictingSchedules = scheduleRepository.findConflictingSchedules(id, startTime, endTime);
        return conflictingSchedules.isEmpty();
    }
}
