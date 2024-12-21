package com.our.ourroom.service;

import com.our.ourroom.entity.MeetingRoom;
import com.our.ourroom.repository.MeetingRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;

    public MeetingRoomService(MeetingRoomRepository meetingRoomRepository) {
        this.meetingRoomRepository = meetingRoomRepository;
    }

    public List<MeetingRoom> getAllMeetingRooms() {
        return meetingRoomRepository.findAll();
    }

    public MeetingRoom getRoomById(Long id) {
        return meetingRoomRepository.findById(id).orElse(null);
    }
}
