package com.our.ourroom.entity;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 일정 참여자 엔티티
 * SCHEDULE_PARTICIPANTS 테이블과 매핑되며, 일정과 참여자 간의 관계를 정의합니다.
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "SCHEDULE_PARTICIPANTS")
@Generated
public class ScheduleParticipant {

    /**
     * 고유 ID (Primary Key)
     * AUTO_INCREMENT 방식으로 값이 자동 생성됩니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 일정 ID
     * SCHEDULE 테이블의 고유 ID와 매핑됩니다.
     */
    @Column(name = "schedule_id")
    private Long scheduleId;

    /**
     * 사용자 ID
     * USER 테이블의 고유 ID와 매핑됩니다.
     */
    @Column(name = "user_id")
    private Long userId;

}
