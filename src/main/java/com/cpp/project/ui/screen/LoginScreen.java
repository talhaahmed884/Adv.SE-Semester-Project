package com.cpp.project.ui.screen;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.dashboard.service.DashboardService;
import com.cpp.project.timer.service.TimerService;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.core.StatefulScreen;
import com.cpp.project.ui.state.login.LoginState;
import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user.service.UserService;
import com.cpp.project.user_credential.service.UserCredentialService;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

/**
 * Refactored Login Screen using design patterns:
 * - State Pattern: Login and Signup state classes in ui.state.login package
 * - Component Pattern: Reusable form fields
 * - Strategy Pattern: Validation strategies
 */
public class LoginScreen extends StatefulScreen {
    private final AuthenticationService authenticationService;
    private final CourseService courseService;
    private final ToDoListService toDoListService;
    private final CalendarService calendarService;
    private final DashboardService dashboardService;
    private final UserService userService;
    private final UserCredentialService credentialService;
    private final TimerService timerService;

    public LoginScreen(
            Screen screen,
            AuthenticationService authenticationService,
            CourseService courseService,
            ToDoListService toDoListService,
            CalendarService calendarService,
            DashboardService dashboardService,
            UserService userService,
            UserCredentialService credentialService,
            TimerService timerService) {
        super(screen);
        this.authenticationService = authenticationService;
        this.courseService = courseService;
        this.toDoListService = toDoListService;
        this.calendarService = calendarService;
        this.dashboardService = dashboardService;
        this.userService = userService;
        this.credentialService = credentialService;
        this.timerService = timerService;

        // Start with login state
        this.currentState = new LoginState(
                screen,
                this.authenticationService,
                this::navigateToMainMenu,
                this::close
        );
        // Call onEnter to set initial focus
        this.currentState.onEnter();
    }

    private void navigateToMainMenu(UserDTO user) {
        try {
            MainMenuScreen mainMenu = new MainMenuScreen(
                    screen,
                    user,
                    courseService,
                    toDoListService,
                    calendarService,
                    dashboardService,
                    userService,
                    credentialService,
                    timerService
            );
            mainMenu.display();
        } catch (IOException e) {
            // Handle error
        }
    }
}
