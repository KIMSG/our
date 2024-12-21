package com.our.ourroom.controller;

import com.our.ourroom.entity.Users;
import com.our.ourroom.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "사용자 API", description = "사용자 관리와 관련된 API 제공")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 사용자 생성 API
     * @param user 사용자 데이터 (JSON)
     * @return 생성된 사용자 데이터
     */
    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 데이터 반환"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<Users> createUser(
            @Parameter(description = "등록할 사용자 이름", required = true)
            @RequestBody Users user) {
        Users createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * 모든 사용자 조회 API
     * - 등록된 모든 사용자 데이터를 반환합니다.
     *
     * @return ResponseEntity<List<Users>> - 사용자 목록과 HTTP 상태 코드
     */
    @Operation(summary = "모든 사용자 조회", description = "등록된 모든 사용자를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 사용자 목록 반환")
    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * 특정 사용자 조회 API
     * - 주어진 ID로 특정 사용자를 조회합니다.
     *
     * @param id Long - 조회할 사용자 ID
     * @return ResponseEntity<Users> - 조회된 사용자 데이터와 HTTP 상태 코드
     */
    @Operation(summary = "특정 사용자 조회", description = "ID로 특정 사용자를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 사용자 반환"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(
            @Parameter(description = "조회할 사용자 ID", required = true)
            @PathVariable Long id) {
        Users user = userService.getUserById(id);
        if (user == null) {
            // 사용자가 존재하지 않을 경우 HTTP 404 Not Found를 반환합니다.
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(user);
    }

}
