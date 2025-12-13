package com.cpp.project.uc_15_start_timer;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.timer.entity.TimerException;
import com.cpp.project.timer.service.TimerService;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-11.03: Cannot start a timer for a non-existent task
 */
public class UC_15_03_StartTimer_Fail_TaskNotFound_Test extends BaseIntegrationTest {
    @Autowired
    private TimerService timerService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-11.03: Cannot start timer for non-existent task")
    public void testStartTimerFailTaskNotFound() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc1103@test.com",
                "Password123!"
        ));

        UUID nonExistentTaskId = UUID.randomUUID();

        // Act & Assert
        TimerException exception = assertThrows(TimerException.class, () -> timerService.startTimer(user.getId(), nonExistentTaskId));

        assertEquals("TIMER_007", exception.getCode());
        assertTrue(exception.getMessage().contains("not found"));
    }
}
