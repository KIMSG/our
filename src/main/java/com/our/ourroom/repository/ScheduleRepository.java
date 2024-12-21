package com.our.ourroom.repository;

import com.our.ourroom.entity.Schedule;
import com.our.ourroom.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s " +
            "WHERE s.meetingRoom.id = :meetingRoomId AND " +
            "((:startTime BETWEEN s.startTime AND s.endTime) OR " +
            "(:endTime BETWEEN s.startTime AND s.endTime) OR " +
            "(s.startTime BETWEEN :startTime AND :endTime) OR " +
            "(s.endTime BETWEEN :startTime AND :endTime))")
    List<Schedule> findConflictingSchedules(@Param("meetingRoomId") Long meetingRoomId,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

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
}
