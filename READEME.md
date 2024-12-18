# Spring Boot REST API 프로젝트

## 개요
Spring Boot를 사용하여 REST API 서버를 구축한 프로젝트입니다.  
사용자는 데이터를 생성, 조회, 수정, 삭제(CRUD)할 수 있는 RESTful 엔드포인트를 제공합니다.

---

## 기술 스택
- **Java 17**: 장기 지원(LTS) 버전
- **Spring Boot 3.4.0**: 빠르고 간단한 개발 환경 제공
- **Spring Data JPA**: 데이터베이스 연동 간소화
- **H2 Database**: 메모리 기반의 임시 데이터베이스
- **Lombok**: 보일러플레이트 코드 제거
- **Gradle**: 의존성 관리 및 빌드 도구

---

## 프로젝트 구조

```
src
├── main
│   ├── java
│   │   └── com.our
│   │       ├── controller  # REST 컨트롤러
│   │       ├── entity      # 데이터 모델
│   │       ├── repository  # 데이터베이스 레이어
│   │       └── service     # 비즈니스 로직
│   └── resources
│       ├── application.properties  # 애플리케이션 설정
│       └── data.sql                # 초기 데이터 (선택사항)
└── test
    └── java
        └── com.our  # 단위 테스트
```

---
## 실행 방법

### 1. 시스템 요구 사항
- Java 17 이상
- Gradle 8.x 이상


---

## 주요 설정

### application.properties
```properties
server.port=8080

spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

---

# API 요청/응답 구조

## 일정 생성

### **요청**
- **HTTP Method**: `POST`
- **Endpoint**: `/schedules`

#### **요청 본문**
```json
{
    "name": "회의 이름",
    "start_time": "YYYY-MM-DDTHH:MM:SS",
    "end_time": "YYYY-MM-DDTHH:MM:SS",
    "room_id": 1,
    "participants": [1, 2, 3]
}
```

| 필드명         | 타입         | 설명                     |
|----------------|--------------|--------------------------|
| `name`         | `string`     | 회의 이름                |
| `start_time`   | `string`     | 시작 시간 (ISO8601 형식) |
| `end_time`     | `string`     | 종료 시간 (ISO8601 형식) |
| `room_id`      | `integer`    | 회의실 ID                |
| `participants` | `array`      | 참여자 ID 목록           |

---

### **응답**
- **HTTP Status**: `200 OK`

#### **응답 본문**
```json
{
    "id": 1,
    "name": "회의 이름",
    "start_time": "YYYY-MM-DDTHH:MM:SS",
    "end_time": "YYYY-MM-DDTHH:MM:SS",
    "room_id": 1,
    "participants": [1, 2, 3]
}
```

| 필드명         | 타입         | 설명                     |
|----------------|--------------|--------------------------|
| `id`           | `integer`    | 생성된 일정 ID           |
| `name`         | `string`     | 회의 이름                |
| `start_time`   | `string`     | 시작 시간 (ISO8601 형식) |
| `end_time`     | `string`     | 종료 시간 (ISO8601 형식) |
| `room_id`      | `integer`    | 회의실 ID                |
| `participants` | `array`      | 참여자 ID 목록           |

---
