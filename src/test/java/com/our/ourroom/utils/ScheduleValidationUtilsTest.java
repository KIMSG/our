package com.our.ourroom.utils;

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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ScheduleValidationUtilsTest {

    private ScheduleValidationUtils scheduleValidationUtils;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private MeetingRoomRepository meetingRoomRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        scheduleValidationUtils = new ScheduleValidationUtils(scheduleRepository, meetingRoomRepository, userRepository);
    }

    @Test
    public void testValidateMeetingRoom_Success() {
        MeetingRoom mockMeetingRoom = new MeetingRoom();
        mockMeetingRoom.setId(1L);

        when(meetingRoomRepository.findById(1L)).thenReturn(Optional.of(mockMeetingRoom));

        MeetingRoom result = scheduleValidationUtils.validateMeetingRoom(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testValidateMeetingRoom_NotFound() {
        when(meetingRoomRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                scheduleValidationUtils.validateMeetingRoom(1L));

        assertEquals("Resource not found", exception.getError());
        assertEquals("요청한 일정 또는 회의실을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    public void testValidateParticipants_Success() {
        Users user1 = new Users();
        user1.setId(1L);
        Users user2 = new Users();
        user2.setId(2L);

        when(userRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(user1, user2));

        List<Users> participants = scheduleValidationUtils.validateParticipants(List.of(1L, 2L), 5);

        assertEquals(2, participants.size());
    }

    @Test
    public void testValidateParticipants_EmptyParticipants() {
        when(userRepository.findAllById(List.of(1L))).thenReturn(List.of());

        CustomException exception = assertThrows(CustomException.class, () ->
                scheduleValidationUtils.validateParticipants(List.of(1L), 5));

        assertEquals("Invalid users", exception.getError());
        assertEquals("다음 사용자 ID는 유효하지 않습니다: [1]", exception.getMessage());
    }

    @Test
    public void testValidateParticipants_ExceedingRoomCapacity() {
        Users user1 = new Users();
        user1.setId(1L);
        Users user2 = new Users();
        user2.setId(2L);
        Users user3 = new Users();
        user3.setId(3L);

        // Room capacity is 2, but 3 participants are provided
        when(userRepository.findAllById(List.of(1L, 2L, 3L))).thenReturn(List.of(user1, user2, user3));

        CustomException exception = assertThrows(CustomException.class, () ->
                scheduleValidationUtils.validateParticipants(List.of(1L, 2L, 3L), 2));

        assertEquals("Exceeding room capacity", exception.getError());
        assertEquals("회의실의 최대 수용 가능 인원을 초과했습니다.", exception.getMessage());
    }

    @Test
    public void testValidateTimeConflict_Success() {
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setMeetingRoomId(1L);
        dto.setStartTime(LocalDateTime.of(2024, 12, 22, 10, 0));
        dto.setEndTime(LocalDateTime.of(2024, 12, 22, 11, 0));

        when(scheduleRepository.findConflictingSchedules(1L, dto.getStartTime(), dto.getEndTime())).thenReturn(List.of());

        assertDoesNotThrow(() -> scheduleValidationUtils.validateTimeConflict(dto));
    }

    @Test
    public void testValidateTimeConflict_ConflictExists() {
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setMeetingRoomId(1L);
        dto.setStartTime(LocalDateTime.of(2024, 12, 22, 10, 0));
        dto.setEndTime(LocalDateTime.of(2024, 12, 22, 11, 0));

        Schedule conflictingSchedule = new Schedule();
        when(scheduleRepository.findConflictingSchedules(1L, dto.getStartTime(), dto.getEndTime()))
                .thenReturn(List.of(conflictingSchedule));

        CustomException exception = assertThrows(CustomException.class, () ->
                scheduleValidationUtils.validateTimeConflict(dto));

        assertEquals("Schedule conflict", exception.getError());
        assertEquals("선택한 회의실은 해당 시간에 이미 예약되었습니다.", exception.getMessage());
    }

    @Test
    public void testValidateTimeConflict_EndTimeBeforeStartTime() {
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setMeetingRoomId(1L);
        dto.setStartTime(LocalDateTime.of(2024, 12, 22, 15, 0));
        dto.setEndTime(LocalDateTime.of(2024, 12, 22, 14, 0)); // 종료 시간이 시작 시간보다 이전

        CustomException exception = assertThrows(CustomException.class, () -> scheduleValidationUtils.validateTimeConflict(dto));

        assertEquals("Invalid request data", exception.getError());
        assertEquals("종료 시간은 시작 시간보다 빠를 수 없습니다.", exception.getMessage());
    }

    @Test
    public void testValidateTimeConflictExcludingSelf_Success() {
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setMeetingRoomId(1L);
        dto.setStartTime(LocalDateTime.of(2024, 12, 22, 10, 0));
        dto.setEndTime(LocalDateTime.of(2024, 12, 22, 11, 0));

        when(scheduleRepository.findConflictingSchedulesExcludingSelf(1L, 1L, dto.getStartTime(), dto.getEndTime())).thenReturn(List.of());

        assertDoesNotThrow(() -> scheduleValidationUtils.validateTimeConflictExcludingSelf(1L, dto));
    }

    @Test
    public void testValidateTimeConflictExcludingSelf_ConflictExists() {
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setMeetingRoomId(1L);
        dto.setStartTime(LocalDateTime.of(2024, 12, 22, 10, 0));
        dto.setEndTime(LocalDateTime.of(2024, 12, 22, 11, 0));

        Schedule conflictingSchedule = new Schedule();
        when(scheduleRepository.findConflictingSchedulesExcludingSelf(1L, 1L, dto.getStartTime(), dto.getEndTime()))
                .thenReturn(List.of(conflictingSchedule));

        CustomException exception = assertThrows(CustomException.class, () ->
                scheduleValidationUtils.validateTimeConflictExcludingSelf(1L,dto));

        assertEquals("Schedule conflict", exception.getError());
        assertEquals("선택한 회의실은 해당 시간에 이미 예약되었습니다.", exception.getMessage());
    }

    @Test
    public void testValidateTimeConflictExcludingSelf_EndTimeBeforeStartTime() {
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setMeetingRoomId(1L);
        dto.setStartTime(LocalDateTime.of(2024, 12, 22, 15, 0));
        dto.setEndTime(LocalDateTime.of(2024, 12, 22, 14, 0)); // 종료 시간이 시작 시간보다 이전

        CustomException exception = assertThrows(CustomException.class, ()
                -> scheduleValidationUtils.validateTimeConflictExcludingSelf(1L, dto));

        assertEquals("Invalid request data", exception.getError());
        assertEquals("종료 시간은 시작 시간보다 빠를 수 없습니다.", exception.getMessage());
    }

    @Test
    public void testValidateUserConflict_Success() {
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setParticipantIds(List.of(1L));
        dto.setStartTime(LocalDateTime.of(2024, 12, 22, 10, 0));
        dto.setEndTime(LocalDateTime.of(2024, 12, 22, 11, 0));

        when(scheduleRepository.findConflictingUsers(List.of(1L), dto.getStartTime(), dto.getEndTime()))
                .thenReturn(List.of());

        assertDoesNotThrow(() -> scheduleValidationUtils.validateUserConflict(dto));
    }

    @Test
    public void testValidateUserConflict_ConflictExists() {
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setParticipantIds(List.of(1L));
        dto.setStartTime(LocalDateTime.of(2024, 12, 22, 10, 0));
        dto.setEndTime(LocalDateTime.of(2024, 12, 22, 11, 0));

        Schedule conflictingSchedule = new Schedule();
        when(scheduleRepository.findConflictingUsers(List.of(1L), dto.getStartTime(), dto.getEndTime()))
                .thenReturn(List.of(conflictingSchedule));

        CustomException exception = assertThrows(CustomException.class, () ->
                scheduleValidationUtils.validateUserConflict(dto));

        assertEquals("User conflict", exception.getError());
        assertEquals("선택한 시간 동안 일부 사용자가 이미 다른 회의에 참석 중입니다.", exception.getMessage());
    }


    @Test
    public void testValidateParticipants_EmptyParticipantIds() {
        // Given
        List<Long> participantIds = List.of(); // 비어 있는 리스트
        int roomCapacity = 5;
        when(userRepository.findAllById(anyList())).thenReturn(List.of());

        // When
        CustomException exception = assertThrows(CustomException.class, () ->
                scheduleValidationUtils.validateParticipants(participantIds, roomCapacity)
        );

        // Then
        assertEquals("Invalid users", exception.getError());
        assertEquals("일정을 생성하려면 최소 1명 이상의 참가자가 필요합니다.", exception.getMessage());
    }


    @Test
    public void testValidateUserConflictExcludingSelf_Success() {
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setParticipantIds(List.of(1L));
        dto.setStartTime(LocalDateTime.of(2024, 12, 22, 10, 0));
        dto.setEndTime(LocalDateTime.of(2024, 12, 22, 11, 0));

        when(scheduleRepository.findConflictingUsersExcludingSelf(1L, List.of(1L), dto.getStartTime(), dto.getEndTime()))
                .thenReturn(List.of());

        assertDoesNotThrow(() -> scheduleValidationUtils.validateUserConflictExcludingSelf(1L, dto));
    }

    @Test
    public void testValidateUserConflictExcludingSelf_ConflictExists() {
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setParticipantIds(List.of(1L));
        dto.setStartTime(LocalDateTime.of(2024, 12, 22, 10, 0));
        dto.setEndTime(LocalDateTime.of(2024, 12, 22, 11, 0));

        Schedule conflictingSchedule = new Schedule();
        when(scheduleRepository.findConflictingUsersExcludingSelf(1L,
                List.of(1L), dto.getStartTime(), dto.getEndTime()))
                .thenReturn(List.of(conflictingSchedule));

        CustomException exception = assertThrows(CustomException.class, () ->
                scheduleValidationUtils.validateUserConflictExcludingSelf(1L, dto));

        assertEquals("User conflict", exception.getError());
        assertEquals("선택한 시간 동안 일부 사용자가 이미 다른 회의에 참석 중입니다.", exception.getMessage());
    }

}