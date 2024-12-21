package com.our.ourroom.service;

import com.our.ourroom.dto.ScheduleRequestDTO;
import com.our.ourroom.entity.MeetingRoom;
import com.our.ourroom.entity.Schedule;
import com.our.ourroom.entity.Users;
import com.our.ourroom.exception.CustomException;
import com.our.ourroom.repository.MeetingRoomRepository;
import com.our.ourroom.repository.ScheduleRepository;
import com.our.ourroom.repository.UserRepository;
import com.our.ourroom.utils.ScheduleValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ScheduleValidationUtils scheduleValidationUtils;

    @InjectMocks
    private ScheduleService scheduleService;

    private ScheduleRequestDTO requestDTO;
    private Schedule testSchedule;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        scheduleService = new ScheduleService(scheduleRepository, scheduleValidationUtils);

        // 기본 요청 데이터 설정
        requestDTO = new ScheduleRequestDTO();
        requestDTO.setName("Team Meeting");
        requestDTO.setStartTime(LocalDateTime.of(2024, 12, 22, 10, 0));
        requestDTO.setEndTime(LocalDateTime.of(2024, 12, 22, 11, 0));
        requestDTO.setMeetingRoomId(1L);
        requestDTO.setParticipantIds(List.of(1L, 2L));

        testSchedule = new Schedule();
        testSchedule.setId(1L);
        testSchedule.setName("Team Meeting");
    }

    @Test
    public void testCreateSchedule_Success() {
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setName("Test Schedule");
        dto.setMeetingRoomId(1L);
        dto.setStartTime(LocalDateTime.of(2024, 12, 22, 10, 0));
        dto.setEndTime(LocalDateTime.of(2024, 12, 22, 11, 0));
        dto.setParticipantIds(List.of(1L, 2L));

        MeetingRoom mockMeetingRoom = new MeetingRoom();
        mockMeetingRoom.setId(1L);
        mockMeetingRoom.setCapacity(5);

        Users user1 = new Users();
        user1.setId(1L);
        Users user2 = new Users();
        user2.setId(2L);

        when(scheduleValidationUtils.validateMeetingRoom(1L)).thenReturn(mockMeetingRoom);
        when(scheduleValidationUtils.validateParticipants(List.of(1L, 2L), 5)).thenReturn(List.of(user1, user2));
        doNothing().when(scheduleValidationUtils).validateTimeConflict(dto);
        doNothing().when(scheduleValidationUtils).validateUserConflict(dto);

        Schedule mockSchedule = new Schedule();
        mockSchedule.setId(1L);
        mockSchedule.setName("Test Schedule");
        mockSchedule.setStartTime(dto.getStartTime());
        mockSchedule.setEndTime(dto.getEndTime());
        mockSchedule.setMeetingRoom(mockMeetingRoom);
        mockSchedule.setParticipants(List.of(user1, user2));

        when(scheduleRepository.save(any(Schedule.class))).thenReturn(mockSchedule);

        Schedule result = scheduleService.createSchedule(dto);

        assertNotNull(result);
        assertEquals("Test Schedule", result.getName());
        assertEquals(1L, result.getMeetingRoom().getId());
        assertEquals(2, result.getParticipants().size());
    }
//
//    @Test
//    public void testCreateSchedule_TimeConflict() {
//        ScheduleRequestDTO dto = new ScheduleRequestDTO();
//        dto.setName("Test Schedule");
//        dto.setMeetingRoomId(1L);
//        dto.setStartTime(LocalDateTime.of(2024, 12, 22, 10, 0));
//        dto.setEndTime(LocalDateTime.of(2024, 12, 22, 11, 0));
//        dto.setParticipantIds(List.of(1L, 2L));
//
//        MeetingRoom mockMeetingRoom = new MeetingRoom();
//        mockMeetingRoom.setId(1L);
//        mockMeetingRoom.setCapacity(5);
//
//        Users user1 = new Users();
//        user1.setId(1L);
//        Users user2 = new Users();
//        user2.setId(2L);
//
//        when(scheduleValidationUtils.validateMeetingRoom(1L)).thenReturn(mockMeetingRoom);
//        when(scheduleValidationUtils.validateParticipants(List.of(1L, 2L), 5)).thenReturn(List.of(user1, user2));
//        doThrow(new CustomException("Schedule conflict", "선택한 회의실은 해당 시간에 이미 예약되었습니다.")).when(scheduleValidationUtils).validateTimeConflict(dto);
//
//        CustomException exception = assertThrows(CustomException.class, () -> scheduleService.createSchedule(dto));
//
//        assertEquals("Schedule conflict", exception.getError());
//        assertEquals("선택한 회의실은 해당 시간에 이미 예약되었습니다.", exception.getMessage());
//    }
//

    @Test
    public void testGetAllSchedules() {
        List<Schedule> schedules = List.of(testSchedule);
        when(scheduleRepository.findAll()).thenReturn(schedules);

        List<Schedule> result = scheduleService.getAllSchedules();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Team Meeting", result.get(0).getName());
    }

    @Test
    public void testGetScheduleById_Success() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));

        Schedule result = scheduleService.getScheduleById(1L);

        assertNotNull(result);
        assertEquals("Team Meeting", result.getName());
    }

    @Test
    public void testGetScheduleById_NotFound() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        Schedule result = scheduleService.getScheduleById(1L);

        assertNull(result);
    }

    @Test
    public void testDeleteScheduleById_Success() {
        when(scheduleRepository.existsById(1L)).thenReturn(true);

        boolean result = scheduleService.deleteScheduleById(1L);

        assertTrue(result);
        verify(scheduleRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteScheduleById_NotFound() {
        when(scheduleRepository.existsById(1L)).thenReturn(false);

        boolean result = scheduleService.deleteScheduleById(1L);

        assertFalse(result);
        verify(scheduleRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testUpdateSchedule_Success() {
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setName("Updated Schedule");
        dto.setMeetingRoomId(1L);
        dto.setStartTime(LocalDateTime.of(2024, 12, 22, 14, 0));
        dto.setEndTime(LocalDateTime.of(2024, 12, 22, 15, 0));
        dto.setParticipantIds(List.of(1L, 2L));

        Schedule existingSchedule = new Schedule();
        existingSchedule.setId(1L);
        existingSchedule.setName("Existing Schedule");

        MeetingRoom mockMeetingRoom = new MeetingRoom();
        mockMeetingRoom.setId(1L);
        mockMeetingRoom.setCapacity(5);

        Users user1 = new Users();
        user1.setId(1L);
        Users user2 = new Users();
        user2.setId(2L);

        when(scheduleRepository.findById(1L)).thenReturn(java.util.Optional.of(existingSchedule));
        when(scheduleValidationUtils.validateMeetingRoom(1L)).thenReturn(mockMeetingRoom);
        when(scheduleValidationUtils.validateParticipants(List.of(1L, 2L), 5)).thenReturn(List.of(user1, user2));
        doNothing().when(scheduleValidationUtils).validateTimeConflict(dto);
        doNothing().when(scheduleValidationUtils).validateUserConflict(dto);

        when(scheduleRepository.save(any(Schedule.class))).thenReturn(existingSchedule);

        Schedule result = scheduleService.updateSchedule(1L, dto);

        assertNotNull(result);
        assertEquals("Updated Schedule", result.getName());
        assertEquals(1L, result.getMeetingRoom().getId());
        assertEquals(2, result.getParticipants().size());
    }

    @Test
    public void testUpdateSchedule_NotFound() {
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setName("Nonexistent Schedule");
        dto.setMeetingRoomId(1L);
        dto.setStartTime(LocalDateTime.of(2024, 12, 22, 14, 0));
        dto.setEndTime(LocalDateTime.of(2024, 12, 22, 15, 0));
        dto.setParticipantIds(List.of(1L));

        when(scheduleRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> scheduleService.updateSchedule(1L, dto));

        assertEquals("Resource not found", exception.getError());
        assertEquals("수정할 일정을 찾을 수 없습니다.", exception.getMessage());
    }


}
