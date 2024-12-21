# Spring Boot REST API 프로젝트

## 개요
Spring Boot를 사용하여 REST API 서버를 구축한 프로젝트입니다.  
사용자는 데이터를 생성, 조회, 수정, 삭제(CRUD)할 수 있는 RESTful 엔드포인트를 제공합니다.
또한 JaCoCo를 활용하여 코드 커버리지를 측정할 수 있습니다.
---

## 기술 스택
- **Java 17**: 장기 지원(LTS) 버전
- **Spring Boot 3.4.0**: 빠르고 간단한 개발 환경 제공
- **Spring Data JPA**: 데이터베이스 연동 간소화
- **H2 Database**: 메모리 기반의 임시 데이터베이스
- **Lombok**: 보일러플레이트 코드 제거
- **Gradle**: 의존성 관리 및 빌드 도구
- **JaCoCo**: 테스트 코드 커버리지 측정 도구

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

## 테스트 및 코드

### 단위 테스트:
- src/test/java에서 테스트 코드 작성 및 실행.

### 코드 커버리지 리포트 (JaCoCo):
- 다음 명령어를 실행하여 테스트 및 커버리지 리포트를 생성합니다:
```
./gradlew clean test jacocoTestReport
```

- HTML 리포트 위치:
```
build/reports/jacoco/test/html/index.html
```

- 브라우저에서 index.html 파일을 열어 코드 커버리지 리포트를 확인합니다.

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


## API 명세

| HTTP Method | 엔드포인트              | 설명                   | 요청 본문 예시                                         |
|-------------|-------------------------|------------------------|--------------------------------------------------|
| **GET**     | `/api/users`           | 모든 데이터 조회        | 없음                                               |
| **GET**     | `/api/users/{id}`      | 특정 데이터 조회        | 없음                                               |
| **POST**    | `/api/users`           | 데이터 생성             | `{ "name": "포스트말론", "email": "post@email.com" }` |
| **PUT**     | `/api/users/{id}`      | 데이터 수정             | `{ "name": "도자캣", "email": "dodo@mail.com" }`    |
| **DELETE**  | `/api/users/{id}`      | 데이터 삭제             | 없음                                               |

---

## todo list 관리하기

#### 마크다운으로 todo list를 관리
- **접속URL** :  /markdown
- **마크다운 파일 위치** : resources/static/TODO.md

---

## commit 관리하기

#### 타입(Type) 종류
- feat: 새로운 기능 추가
- fix: 버그 수정
- refactor: 코드 리팩토링 (기능 변화 없음)
- docs: 문서 수정 (README.md 등)
- test: 테스트 코드 추가/수정
- chore: 빌드 설정, 패키지 관리, 기타 작업
- style: 코드 스타일 수정 (포맷팅, 세미콜론 추가 등)
- perf: 성능 개선
- ci: CI/CD 설정 수정


---

## 참고 문서

- [Gradle 공식 문서](https://docs.gradle.org)
- [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.4.0/reference/using/devtools.html)
