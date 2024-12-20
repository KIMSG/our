# 프로젝트 To-Do List

## 1. 엔티티 설계
- [ ] Users 엔티티 생성 (`feature/entity-users`)
    - 사용자 ID, 이름 필드 정의
    - MeetingRoom과의 Many-to-One 관계 매핑
- [ ] MeetingRoom 엔티티 생성 (`feature/entity-meetingroom`)
    - 회의실 ID, 이름, 수용 인원 필드 정의
    - Users와의 One-to-Many 관계 매핑
- [ ] Schedule 엔티티 생성 (`feature/entity-schedule`)
    - 일정 ID, 이름, 시작/종료 시간, 참여자 목록 필드 정의
    - MeetingRoom과의 Many-to-One 관계 매핑
    - Users와의 Many-to-Many 관계 매핑

---

## 2. 사용자 관리 API
- [ ] 사용자 생성 (`feature/user-management`)
    - POST /users
- [ ] 사용자 조회 (`feature/user-management`)
    - GET /users
    - GET /users/{id}
- [ ] 사용자 업데이트 (`feature/user-management`)
    - PUT /users/{id}
- [ ] 사용자 삭제 (`feature/user-management`)
    - DELETE /users/{id}

---

## 3. 회의실 관리 API
- [ ] 회의실 생성 (`feature/meetingroom-management`)
    - POST /rooms
- [ ] 회의실 조회 (`feature/meetingroom-management`)
    - GET /rooms
    - GET /rooms/{id}
- [ ] 회의실 업데이트 (`feature/meetingroom-management`)
    - PUT /rooms/{id}
- [ ] 회의실 삭제 (`feature/meetingroom-management`)
    - DELETE /rooms/{id}
- [ ] 회의실 예약 가능 여부 확인 (`feature/meetingroom-availability`)
    - GET /rooms/{id}/availability

---

## 4. 일정 관리 API
- [ ] 일정 생성 (`feature/schedule-management`)
    - POST /schedules
- [ ] 일정 조회 (`feature/schedule-management`)
    - GET /schedules
    - GET /schedules/{id}
- [ ] 일정 수정 (`feature/schedule-management`)
    - PUT /schedules/{id}
- [ ] 일정 삭제 (`feature/schedule-management`)
    - DELETE /schedules/{id}
- [ ] 일정에 참여자 추가 (`feature/schedule-participants`)
    - POST /schedules/{id}/participants
- [ ] 일정에서 참여자 삭제 (`feature/schedule-participants`)
    - DELETE /schedules/{id}/participants/{userId}
- [ ] 일정 충돌 검사 (`feature/schedule-conflict-check`)
    - POST /schedules/conflict-check

---

## 5. 로그 관리
- [ ] 일정 로그 조회 (`feature/schedule-logs`)
    - GET /schedules/logs
    - 필터: 날짜 범위, 회의실 ID, 사용자 ID

---

## 6. 테스트 및 디버깅
- [ ] 엔티티와 초기 데이터 테스트 (`feature/test-database`)
    - JUnit과 H2 콘솔을 활용하여 데이터 무결성 확인
- [ ] API 통합 테스트 (`feature/test-api`)
    - MockMvc로 API 호출 및 응답 테스트
    - 상태 코드 및 응답 데이터 검증

---

## 7. 문서화
- [ ] API 명세 작성 (`feature/docs-api`)
    - Swagger UI 연동
    - 각 API에 대한 요청/응답 정의
- [ ] 프로젝트 README 작성 (`docs/readme`)
    - 프로젝트 개요 및 실행 방법
    - 주요 기능 및 구조 설명

---

## 8. 최종 마무리
- [ ] 코드 리팩토링 (`chore/refactor-code`)
    - 코드 정리 및 주석 추가
    - 불필요한 코드 및 로그 제거
- [ ] 최종 배포 준비 (`main`)
    - 배포 가능한 상태로 병합
