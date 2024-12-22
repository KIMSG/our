package com.our.ourroom.service;

import com.our.ourroom.entity.MeetingRoom;
import com.our.ourroom.entity.Schedule;
import com.our.ourroom.repository.MeetingRoomRepository;
import com.our.ourroom.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class MeetingRoomServiceTest {

    @Mock
    private MeetingRoomRepository meetingRoomRepository;

    @InjectMocks
    private MeetingRoomService meetingRoomService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllMeetingRoom() {
        MeetingRoom meetingRoom = new MeetingRoom();
        meetingRoom.setId(1L);
        meetingRoom.setName("Room A");
        meetingRoom.setCapacity(5);

        MeetingRoom meetingRoom1 = new MeetingRoom();
        meetingRoom1.setId(2L);
        meetingRoom1.setName("Room B");
        meetingRoom1.setCapacity(15);

        when(meetingRoomRepository.findAll()).thenReturn(List.of(meetingRoom, meetingRoom1));
        List<MeetingRoom> meetingRooms = meetingRoomService.getAllMeetingRooms();

        assertEquals(2, meetingRooms.size());
        assertEquals("Room A", meetingRooms.get(0).getName());
        assertEquals("Room B", meetingRooms.get(1).getName());

    }

    @Test
    public void testGetRoomById() {
        // Mocking: MeetingRoomService의 getRoomById 메서드
        MeetingRoom meetingRoom = new MeetingRoom();
        meetingRoom.setId(1L);
        meetingRoom.setName("Room A");
        meetingRoom.setCapacity(10);

        when(meetingRoomRepository.findById(1L)).thenReturn(Optional.of(meetingRoom));

        MeetingRoom meetingRoom1 = meetingRoomService.getRoomById(1L);

        assertEquals("Room A", meetingRoom1.getName());
        assertEquals(10, meetingRoom1.getCapacity());
        assertEquals(1L, meetingRoom1.getId());
    }
    @Test
    void checkAvailability_WhenNoConflictingSchedules_ReturnsZero() {
        // Given
        Long roomId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 22, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 12, 22, 11, 0);

        when(scheduleRepository.findConflictingSchedules(roomId, startTime, endTime))
                .thenReturn(Collections.emptyList());

        // When
        int result = meetingRoomService.checkAvailability(roomId, startTime, endTime);

        // Then
        assertEquals(0, result);
        verify(scheduleRepository, times(1)).findConflictingSchedules(roomId, startTime, endTime);
    }

    @Test
    void checkAvailability_WhenConflictingSchedulesExist_ReturnsConflictCount() {
        // Given
        Long roomId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 22, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 12, 22, 11, 0);
        List<Schedule> conflictingSchedules = List.of(
                new Schedule(), new Schedule() // Mocked conflicting schedules
        );

        when(scheduleRepository.findConflictingSchedules(roomId, startTime, endTime))
                .thenReturn(conflictingSchedules);

        // When
        int result = meetingRoomService.checkAvailability(roomId, startTime, endTime);

        // Then
        assertEquals(2, result);
        verify(scheduleRepository, times(1)).findConflictingSchedules(roomId, startTime, endTime);
    }
}
