package com.cpp.project.common.controller.service;

import com.cpp.project.common.controller.dto.ApiSuccessResponse;
import com.cpp.project.user.dto.UpdateUserRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user.entity.UserErrorCode;
import com.cpp.project.user.entity.UserException;
import com.cpp.project.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<UserDTO>> getUserById(
            @PathVariable UUID id) {

        UserDTO user = userService.getUserById(id);

        ApiSuccessResponse<UserDTO> response = ApiSuccessResponse.<UserDTO>builder()
                .data(user)
                .message("User retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiSuccessResponse<UserDTO>> getUserByEmail(
            @PathVariable String email) {

        UserDTO user = userService.getUserByEmail(email);

        ApiSuccessResponse<UserDTO> response = ApiSuccessResponse.<UserDTO>builder()
                .data(user)
                .message("User retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<String>> updateUser(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequestDTO request) {

        // Validate at least one field is provided
        if (request.isEmpty()) {
            throw new UserException(UserErrorCode.INVALID_USER_DATA,
                    "At least one field (name or email) must be provided");
        }

        userService.updateUser(id, request.getName(), request.getEmail());

        ApiSuccessResponse<String> response = ApiSuccessResponse.<String>builder()
                .data("User updated successfully")
                .message("Update successful")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<String>> deleteUser(
            @PathVariable UUID id) {

        userService.deleteUser(id);

        ApiSuccessResponse<String> response = ApiSuccessResponse.<String>builder()
                .data("User deleted successfully")
                .message("Deletion successful")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }
}
