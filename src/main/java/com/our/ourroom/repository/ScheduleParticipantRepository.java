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

    /**
     * 특정 사용자 ID로 주어진 시간 범위와 겹치는 일정이 있는지 확인합니다.
     * @param userId 사용자 ID
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 겹치는 일정이 존재하면 true, 없으면 false
     */
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

    /**
     * 특정 일정과 사용자 간의 참여 기록이 있는지 확인합니다.
     * @param scheduleId 일정 ID
     * @param userId 사용자 ID
     * @return 존재하면 true, 없으면 false
     */
    boolean existsByScheduleIdAndUserId(Long scheduleId, Long userId);

    /**
     * 특정 일정과 사용자 간의 참여 기록을 삭제합니다.
     * @param scheduleId 일정 ID
     * @param userId 사용자 ID
     */
    @Modifying
    @Query("DELETE FROM ScheduleParticipant sp WHERE sp.scheduleId = :scheduleId AND sp.userId = :userId")
    void deleteByScheduleIdAndUserId(@Param("scheduleId") Long scheduleId, @Param("userId") Long userId);

}
