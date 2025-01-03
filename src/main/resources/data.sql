-- 테이블 삭제
DROP TABLE IF EXISTS SCHEDULE_PARTICIPANTS;
DROP TABLE IF EXISTS SCHEDULE;
DROP TABLE IF EXISTS MEETING_ROOM;
DROP TABLE IF EXISTS USERS;

-- USERS 테이블
CREATE TABLE IF NOT EXISTS USERS (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(255) NOT NULL
);

-- MEETING_ROOM 테이블
CREATE TABLE IF NOT EXISTS MEETING_ROOM (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    capacity INT NOT NULL
);

-- SCHEDULE 테이블
CREATE TABLE IF NOT EXISTS SCHEDULE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    meeting_room_id BIGINT NOT NULL,
    FOREIGN KEY (meeting_room_id) REFERENCES MEETING_ROOM (id)
);

-- SCHEDULE_PARTICIPANTS 테이블 (다대다 관계)
CREATE TABLE IF NOT EXISTS SCHEDULE_PARTICIPANTS (
    schedule_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (schedule_id, user_id),
    FOREIGN KEY (schedule_id) REFERENCES SCHEDULE (id),
    FOREIGN KEY (user_id) REFERENCES USERS (id)
);





-- 회의실 데이터
INSERT INTO MEETING_ROOM (name, capacity) VALUES ('회의실 A', 3);
INSERT INTO MEETING_ROOM (name, capacity) VALUES ('회의실 B', 5);
INSERT INTO MEETING_ROOM (name, capacity) VALUES ('회의실 C', 10);

-- 사용자 데이터
INSERT INTO USERS (name) VALUES ('포스트말론');
INSERT INTO USERS (name) VALUES ('도자캣');
INSERT INTO USERS (name) VALUES ('켄드릭 라마');
INSERT INTO USERS (name) VALUES ('트래비스 스캇');
INSERT INTO USERS (name) VALUES ('제이 콜');
INSERT INTO USERS (name) VALUES ('릴 나스 엑스');
INSERT INTO USERS (name) VALUES ('차일디시 감비노');
INSERT INTO USERS (name) VALUES ('맥 밀러');
INSERT INTO USERS (name) VALUES ('에이셉 라키');
INSERT INTO USERS (name) VALUES ( '타일러 더 크리에이터');
INSERT INTO USERS (name) VALUES ( '빌리 아일리시');
INSERT INTO USERS (name) VALUES ( '리조');
INSERT INTO USERS (name) VALUES ( '할시');
INSERT INTO USERS (name) VALUES ( '존 메이어');
INSERT INTO USERS (name) VALUES ( '찰리 푸스');
INSERT INTO USERS (name) VALUES ( '제이지');
INSERT INTO USERS (name) VALUES ( '카디비');
INSERT INTO USERS (name) VALUES ( '브루노 마스');
INSERT INTO USERS (name) VALUES ( '셔린 디온');
INSERT INTO USERS (name) VALUES ( '엘튼 존');

