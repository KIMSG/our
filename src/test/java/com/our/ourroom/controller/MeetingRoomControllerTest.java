package com.our.ourroom.controller;

import com.our.ourroom.entity.MeetingRoom;
import com.our.ourroom.entity.Users;
import com.our.ourroom.service.MeetingRoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MeetingRoomController.class)
public class MeetingRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeetingRoomService meetingRoomService;

    @Test
    public void testGetAllMeetingRooms() throws Exception {
        List<MeetingRoom> meetingRooms = List.of(
                new MeetingRoom() {{
                    setId(1L);
                    setName("회의실 A");
                    setCapacity(10);
                }},
                new MeetingRoom() {{
                    setId(2L);
                    setName("회의실 B");
                    setCapacity(5);
                }}
        );

        when(meetingRoomService.getAllMeetingRooms()).thenReturn(meetingRooms);

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(meetingRooms.size()))
                .andExpect(jsonPath("$[0].name").value("회의실 A"))
                .andExpect(jsonPath("$[1].capacity").value(5));
    }


    @Test
    public void testGetRoomById() throws Exception {
        MeetingRoom meetingRoom = new MeetingRoom();
        meetingRoom.setId(1L);

        when(meetingRoomService.getRoomById(1L)).thenReturn(meetingRoom);

        mockMvc.perform(get("/api/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testGetMeetingRoomById_NotFound() throws Exception {
        // Mock: 존재하지 않는 ID로 조회
        when(meetingRoomService.getRoomById(1L)).thenReturn(null);

        // GET 요청
        mockMvc.perform(get("/api/rooms/1"))
                .andExpect(status().isNotFound()) // HTTP 404 상태 확인
                .andExpect(jsonPath("$").doesNotExist()); // body가 비어 있는지 확인
    }

}
