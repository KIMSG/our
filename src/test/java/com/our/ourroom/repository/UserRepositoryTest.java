package com.our.ourroom.repository;

import com.our.ourroom.entity.Users;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test") // 테스트 환경 프로파일 활성화
@Sql(scripts = "/data.sql") // 데이터 삽입 스크립트
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindAllUsers() {
        List<Users> users = userRepository.findAll();
        assertNotNull(users); // Null이 아님을 검증
        assertFalse(users.isEmpty()); // 최소 1개의 데이터가 있음을 검증
    }
}
