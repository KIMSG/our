package com.our.ourroom.service;

import com.our.ourroom.dto.ScheduleRequestDTO;
import com.our.ourroom.entity.MeetingRoom;
import com.our.ourroom.entity.Schedule;
import com.our.ourroom.entity.Users;
import com.our.ourroom.exception.CustomException;
import com.our.ourroom.repository.MeetingRoomRepository;
import com.our.ourroom.repository.ScheduleRepository;
import com.our.ourroom.repository.UserRepository;
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
    private MeetingRoomRepository meetingRoomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    private ScheduleRequestDTO requestDTO;
    private Schedule testSchedule;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

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
        // Mock MeetingRoom
        MeetingRoom meetingRoom = new MeetingRoom();
        meetingRoom.setId(1L);
        meetingRoom.setCapacity(5);

        // Mock Users
        Users user1 = new Users();
        user1.setId(1L);
        Users user2 = new Users();
        user2.setId(2L);

        // Mock 저장된 Schedule
        Schedule savedSchedule = new Schedule();
        savedSchedule.setId(1L);
        savedSchedule.setName("Team Meeting");

        // Mock 동작 설정
        when(meetingRoomRepository.findById(1L)).thenReturn(Optional.of(meetingRoom));
        when(userRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(user1, user2));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(savedSchedule);

        // 테스트 실행
        Schedule result = scheduleService.createSchedule(requestDTO);

        // 결과 검증
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Team Meeting", result.getName());

        // 호출 검증
        verify(meetingRoomRepository).findById(1L);
        verify(userRepository).findAllById(List.of(1L, 2L));
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    public void testCreateSchedule_InvalidMeetingRoom() {
        when(meetingRoomRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            scheduleService.createSchedule(requestDTO);
        });

        assertEquals("Resource not found", exception.getError());
        assertEquals("요청한 일정 또는 회의실을 찾을 수 없습니다.", exception.getMessage());
        verify(meetingRoomRepository).findById(1L);
    }

    @Test
    public void testCreateSchedule_InvalidParticipants() {
        MeetingRoom meetingRoom = new MeetingRoom();
        meetingRoom.setId(1L);
        meetingRoom.setCapacity(5);

        when(meetingRoomRepository.findById(1L)).thenReturn(Optional.of(meetingRoom));
        when(userRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of());

        CustomException exception = assertThrows(CustomException.class, () -> {
            scheduleService.createSchedule(requestDTO);
        });

        assertEquals("Invalid users", exception.getError());
        assertTrue(exception.getMessage().contains("다음 사용자 ID는 유효하지 않습니다"));
        verify(userRepository).findAllById(List.of(1L, 2L));
    }

    @Test
    public void testCreateSchedule_ExceedingRoomCapacity() {
        MeetingRoom meetingRoom = new MeetingRoom();
        meetingRoom.setId(1L);
        meetingRoom.setCapacity(1);

        Users user1 = new Users();
        user1.setId(1L);
        Users user2 = new Users();
        user2.setId(2L);

        when(meetingRoomRepository.findById(1L)).thenReturn(Optional.of(meetingRoom));
        when(userRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(user1, user2));

        CustomException exception = assertThrows(CustomException.class, () -> {
            scheduleService.createSchedule(requestDTO);
        });

        assertEquals("Exceeding room capacity", exception.getError());
        assertEquals("회의실의 최대 수용 가능 인원을 초과했습니다.", exception.getMessage());
        verify(userRepository).findAllById(List.of(1L, 2L));
    }

    @Test
    public void testCreateSchedule_NoParticipants() {
        // Given
        ScheduleRequestDTO requestDTO = new ScheduleRequestDTO();
        requestDTO.setName("Test Schedule");
        requestDTO.setStartTime(LocalDateTime.now().plusHours(1));
        requestDTO.setEndTime(LocalDateTime.now().plusHours(2));
        requestDTO.setMeetingRoomId(1L);
        requestDTO.setParticipantIds(List.of()); // 비어 있는 참가자 ID

        when(meetingRoomRepository.findById(1L)).thenReturn(Optional.of(new MeetingRoom()));
        when(userRepository.findAllById(List.of())).thenReturn(List.of()); // Mock: 빈 리스트 반환

        // When
        CustomException exception = assertThrows(CustomException.class, () -> {
            scheduleService.createSchedule(requestDTO);
        });

        // Then
        assertEquals("Exceeding room capacity", exception.getError());
        assertEquals("일정을 생성하려면 최소 1명의 참가자가 필요합니다.", exception.getMessage());
    }

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

}
