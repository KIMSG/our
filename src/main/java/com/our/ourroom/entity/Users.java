package com.our.ourroom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Users 엔티티
 * - 사용자 정보를 저장하는 엔티티로, 사용자 ID와 이름을 포함합니다.
 */
@Entity
@Getter
@Setter
@ToString
public class Users {

    /**
     * 사용자 고유 ID
     * - 자동 생성되는 기본 키 값
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자 이름
     * - 사용자의 이름을 저장
     */
    @Column(nullable = false)
    private String name;

}

