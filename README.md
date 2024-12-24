# 사내 회의실 예약 프로젝트

## **프로젝트 개요**

**우리 방 (OurRoom)**은 사내 회의실 예약 및 일정 관리를 위한 최적화된 솔루션입니다.  
효율적인 API와 간단한 UI를 통해 사용자와 회의실, 그리고 일정을 효과적으로 관리할 수 있습니다.

Spring Boot를 사용하여 REST API 서버를 구축한 프로젝트입니다.  
사용자는 데이터를 생성, 조회, 수정, 삭제(CRUD)할 수 있는 RESTful 엔드포인트를 제공합니다.
또한 JaCoCo를 활용하여 코드 커버리지를 측정할 수 있습니다.
---

### **✨ 주요 특징**
- **회의실 관리**: 다양한 크기의 회의실을 등록하고 예약 가능 여부를 실시간으로 확인.
- **일정 관리**: 일정 생성, 수정, 삭제뿐만 아니라 참여자 관리 기능 지원.
- **사용자 관리**: 사용자 데이터의 생성, 조회, 수정 및 삭제 기능 제공.
- **JaCoCo**를 통해 테스트 커버리지 리포트를 생성하여 코드 품질을 관리.

## 기술 스택
- **Java 17**: 장기 지원(LTS) 버전
- **Spring Boot 3.4.0**: 빠르고 간단한 개발 환경 제공
- **Spring Data JPA**: 데이터베이스 연동 간소화
- **H2 Database**: 메모리 기반의 임시 데이터베이스
- **Swagger UI**로 간편한 API 테스트 및 문서 확인.
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
│   │       ├── dto         # 데이터 응용 모델
│   │       ├── entity      # 데이터 모델
│   │       ├── exception   # 공통 예외 처리 로직
│   │       ├── repository  # 데이터베이스 레이어
│   │       ├── service     # 비즈니스 로직
│   │       └── utils       # 공통유틸 모음
│   └── resources
│       ├── application.properties  # 애플리케이션 설정
│       ├── data.sql                # 초기 데이터 (선택사항)
│       └── static/TODO.md          # TODO 관리 파일
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

### 프로젝트 실행
   ```bash
   # 프로젝트 복제 및 실행
   git clone https://github.com/your-repo/our-room.git
   cd our-room
   ./gradlew bootRun
   ```

### Swagger 및 H2 Console 접속
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- H2 Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)


---

# 주요 기능 및 구조 설명

## 사용자 관리 API:

- 사용자 생성: POST /users
- 모든 사용자 조회: GET /users 
- 특정 사용자 조회: GET /users/{id}

## 회의실 관리 API:
- 회의실 목록 조회: GET /rooms
- 특정 회의실 조회: GET /rooms/{id}
- 회의실 예약 가능 여부: GET /rooms/{id}/availability

### 일정 관리 API:
- 일정 생성: POST /schedules
- 전체 일정 조회: GET /schedules
- 특정 일정 조회: GET /schedules/{id}
- 일정 수정: PUT /schedules/{id}
- 일정 삭제: DELETE /schedules/{id}
- 참여자 관리:
  - 추가: POST /schedules/{id}/participants
  - 삭제: DELETE /schedules/{id}/participants/{userId}

#### **요청 본문 예시**
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
