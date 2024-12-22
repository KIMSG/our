package com.our.ourroom.entity;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "SCHEDULE_PARTICIPANTS")
@Generated
public class ScheduleParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(name = "user_id")
    private Long userId;

}
