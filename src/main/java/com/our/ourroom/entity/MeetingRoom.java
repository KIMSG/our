package com.our.ourroom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * MeetingRoom 엔티티
 * - 회의실 정보를 저장하는 엔티티로, 회의실 ID, 이름, 수용 인원을 포함합니다.
 */
@Entity
@Getter
@Setter
@ToString
public class MeetingRoom {

    /**
     * 회의실 고유 ID
     * - 자동 생성되는 기본 키 값
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 회의실 이름
     * - 회의실의 이름을 저장
     */
    private String name;

    /**
     * 회의실 수용 인원
     * - 해당 회의실이 수용할 수 있는 최대 인원
     */
    private int capacity;

}
