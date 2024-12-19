package com.our.ourroom.exception;

import com.our.ourroom.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(GlobalExceptionHandler.class)
public class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

    @Test
    public void testHandleInternalServerError() throws Exception {
        // ScheduleService의 getAllSchedules() 호출 시 예외 발생하도록 설정
        Mockito.when(scheduleService.getAllSchedules()).thenThrow(new RuntimeException("Test Exception"));

        // /api/schedules GET 요청 시 예외 처리 테스트
        mockMvc.perform(get("/api/schedules"))
                .andExpect(status().isInternalServerError()) // HTTP 500 상태 코드 확인
                .andExpect(jsonPath("$.error").value("Internal server error")) // 응답 메시지 확인
                .andExpect(jsonPath("$.details").value("서버에서 예기치 않은 오류가 발생했습니다. 나중에 다시 시도해 주세요."));
    }
}
