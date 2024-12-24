package com.our.ourroom.repository;

import com.our.ourroom.entity.MeetingRoom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test") // 테스트 환경 프로파일 활성화
@Sql(scripts = "/data.sql") // 데이터 삽입 스크립트
public class MeetingRoomRepositoryTest {

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    @Test
    void testSaveValidMeetingRoom() {
        MeetingRoom room = new MeetingRoom();
        room.setName("Main Conference Room");
        room.setCapacity(10);

        MeetingRoom savedRoom = meetingRoomRepository.save(room);

        assertNotNull(savedRoom.getId());
        assertEquals("Main Conference Room", savedRoom.getName());
        assertEquals(10, savedRoom.getCapacity());
    }

}
