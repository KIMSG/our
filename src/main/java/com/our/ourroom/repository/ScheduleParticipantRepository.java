package com.our.ourroom.repository;

import com.our.ourroom.entity.ScheduleParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ScheduleParticipantRepository extends JpaRepository<ScheduleParticipant, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(sp) > 0 THEN true ELSE false END
        FROM ScheduleParticipant sp
        JOIN Schedule s ON sp.scheduleId = s.id
        WHERE sp.userId = :userId
          AND (s.startTime < :endTime AND s.endTime > :startTime)
    """)
    boolean existsByUserIdAndOverlappingSchedule(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    boolean existsByScheduleIdAndUserId(Long scheduleId, Long userId);

    @Modifying
    @Query("DELETE FROM ScheduleParticipant sp WHERE sp.scheduleId = :scheduleId AND sp.userId = :userId")
    void deleteByScheduleIdAndUserId(@Param("scheduleId") Long scheduleId, @Param("userId") Long userId);

}
