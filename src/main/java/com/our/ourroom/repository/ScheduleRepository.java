package com.our.ourroom.repository;

import com.our.ourroom.entity.Schedule;
import com.our.ourroom.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    /**
     * 특정 회의실에서 주어진 시간 범위와 겹치는 일정을 조회합니다.
     * @param meetingRoomId 회의실 ID
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 겹치는 일정 리스트
     */
    @Query("SELECT s FROM Schedule s " +
            "WHERE s.meetingRoom.id = :meetingRoomId AND " +
            "((:startTime BETWEEN s.startTime AND s.endTime) OR " +
            "(:endTime BETWEEN s.startTime AND s.endTime) OR " +
            "(s.startTime BETWEEN :startTime AND :endTime) OR " +
            "(s.endTime BETWEEN :startTime AND :endTime))")
    List<Schedule> findConflictingSchedules(@Param("meetingRoomId") Long meetingRoomId,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 특정 회의실에서 주어진 시간 범위와 겹치는 일정 중, 자신을 제외한 일정을 조회합니다.
     * @param id 제외할 일정 ID
     * @param meetingRoomId 회의실 ID
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 겹치는 일정 리스트
     */
    @Query("SELECT s FROM Schedule s " +
            "WHERE s.meetingRoom.id = :meetingRoomId AND " +
            "((:startTime BETWEEN s.startTime AND s.endTime) OR " +
            "(:endTime BETWEEN s.startTime AND s.endTime) OR " +
            "(s.startTime BETWEEN :startTime AND :endTime) OR " +
            "(s.endTime BETWEEN :startTime AND :endTime))" +
            "AND s.id NOT IN (:id) ")
    List<Schedule> findConflictingSchedulesExcludingSelf(@Param("id") Long id,
                                                         @Param("meetingRoomId") Long meetingRoomId,
                                                         @Param("startTime") LocalDateTime startTime,
                                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 특정 시간 범위와 겹치는 일정에 참여 중인 사용자를 조회합니다.
     * @param participantIds 참여자 ID 리스트
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 겹치는 일정 리스트
     */
    @Query("SELECT DISTINCT s FROM Schedule s " +
            "JOIN ScheduleParticipant sp on sp.scheduleId = s.id " +
            "WHERE ((:startTime BETWEEN s.startTime AND s.endTime) OR " +
            "(:endTime BETWEEN s.startTime AND s.endTime) OR " +
            "(s.startTime BETWEEN :startTime AND :endTime) OR " +
            "(s.endTime BETWEEN :startTime AND :endTime))" +
            "AND sp.userId IN :participantIds ")
    List<Schedule> findConflictingUsers(@Param("participantIds") List<Long> participantIds,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);

    /**
     * 특정 시간 범위와 겹치는 일정 중 자신을 제외하고 참여 중인 사용자를 조회합니다.
     * @param id 제외할 일정 ID
     * @param participantIds 참여자 ID 리스트
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 겹치는 일정 리스트
     */
    @Query("SELECT DISTINCT s FROM Schedule s " +
            "JOIN ScheduleParticipant sp on sp.scheduleId = s.id " +
            "WHERE ((:startTime BETWEEN s.startTime AND s.endTime) OR " +
            "(:endTime BETWEEN s.startTime AND s.endTime) OR " +
            "(s.startTime BETWEEN :startTime AND :endTime) OR " +
            "(s.endTime BETWEEN :startTime AND :endTime))" +
            "AND sp.scheduleId NOT IN (:id) " +
            "AND sp.userId IN :participantIds ")
    List<Schedule> findConflictingUsersExcludingSelf(@Param("id") Long id,
                                                     @Param("participantIds") List<Long> participantIds,
                                                     @Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime);
}
