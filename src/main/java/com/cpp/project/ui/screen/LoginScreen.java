package com.cpp.project.ui.screen;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

/**
 * Login screen for authentication
 * Allows users to login or signup
 */
public class LoginScreen {
    private final Screen screen;
    private final AuthenticationService authenticationService;
    private final CourseService courseService;
    private final ToDoListService toDoListService;
    private final CalendarService calendarService;

    private String email = "";
    private String password = "";
    private String name = "";
    private int selectedField = 0; // 0=email, 1=password, 2=name (for signup)
    private boolean isSignupMode = false;
    private String errorMessage = "";
    private String successMessage = "";

    public LoginScreen(
            Screen screen,
            AuthenticationService authenticationService,
            CourseService courseService,
            ToDoListService toDoListService,
            CalendarService calendarService) {
        this.screen = screen;
        this.authenticationService = authenticationService;
        this.courseService = courseService;
        this.toDoListService = toDoListService;
        this.calendarService = calendarService;
    }

    public void display() throws IOException {
        boolean running = true;

        while (running) {
            screen.clear();
            render();
            screen.refresh();

            KeyStroke keyStroke = screen.readInput();
            if (keyStroke.getKeyType() == KeyType.Character) {
                handleCharacterInput(keyStroke.getCharacter());
            } else if (keyStroke.getKeyType() == KeyType.Backspace) {
                handleBackspace();
            } else if (keyStroke.getKeyType() == KeyType.Tab) {
                nextField();
            } else if (keyStroke.getKeyType() == KeyType.Enter) {
                running = !handleEnter();
            } else if (keyStroke.getKeyType() == KeyType.F1) {
                toggleMode();
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                running = false;
            }
        }
    }

    private void render() {
        TextGraphics graphics = screen.newTextGraphics();
        TerminalSize size = screen.getTerminalSize();

        // Title
        String title = "=== STUDENTLY - " + (isSignupMode ? "SIGN UP" : "LOGIN") + " ===";
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        graphics.putString((size.getColumns() - title.length()) / 2, 2, title);

        graphics.setForegroundColor(TextColor.ANSI.WHITE);

        int startY = 5;

        // Instructions
        graphics.putString(5, startY, "F1: Switch to " + (isSignupMode ? "Login" : "Signup"));
        graphics.putString(5, startY + 1, "Tab: Next field");
        graphics.putString(5, startY + 2, "ESC: Exit");

        startY += 4;

        // Name field (only in signup mode)
        if (isSignupMode) {
            graphics.setForegroundColor(selectedField == 2 ? TextColor.ANSI.YELLOW_BRIGHT : TextColor.ANSI.WHITE);
            graphics.putString(5, startY, "Name: ");
            graphics.putString(15, startY, name + (selectedField == 2 ? "_" : ""));
            startY += 2;
        }

        // Email field
        graphics.setForegroundColor(selectedField == 0 ? TextColor.ANSI.YELLOW_BRIGHT : TextColor.ANSI.WHITE);
        graphics.putString(5, startY, "Email: ");
        graphics.putString(15, startY, email + (selectedField == 0 ? "_" : ""));
        startY += 2;

        // Password field
        graphics.setForegroundColor(selectedField == 1 ? TextColor.ANSI.YELLOW_BRIGHT : TextColor.ANSI.WHITE);
        graphics.putString(5, startY, "Password: ");
        graphics.putString(15, startY, "*".repeat(password.length()) + (selectedField == 1 ? "_" : ""));
        startY += 2;

        // Submit button
        graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
        graphics.putString(5, startY + 1, "Press ENTER to " + (isSignupMode ? "Sign Up" : "Login"));

        // Error/Success messages
        if (!errorMessage.isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
            graphics.putString(5, startY + 3, "Error: " + errorMessage);
        }
        if (!successMessage.isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
            graphics.putString(5, startY + 3, successMessage);
        }
    }

    private void handleCharacterInput(Character c) {
        errorMessage = "";
        successMessage = "";

        if (isSignupMode && selectedField == 2) {
            name += c;
        } else if (selectedField == 0) {
            email += c;
        } else if (selectedField == 1) {
            password += c;
        }
    }

    private void handleBackspace() {
        errorMessage = "";
        successMessage = "";

        if (isSignupMode && selectedField == 2 && !name.isEmpty()) {
            name = name.substring(0, name.length() - 1);
        } else if (selectedField == 0 && !email.isEmpty()) {
            email = email.substring(0, email.length() - 1);
        } else if (selectedField == 1 && !password.isEmpty()) {
            password = password.substring(0, password.length() - 1);
        }
    }

    private void nextField() {
        if (isSignupMode) {
            selectedField = (selectedField + 1) % 3;
        } else {
            selectedField = (selectedField + 1) % 2;
        }
    }

    private void toggleMode() {
        isSignupMode = !isSignupMode;
        selectedField = isSignupMode ? 2 : 0;
        errorMessage = "";
        successMessage = "";
        name = "";
        email = "";
        password = "";
    }

    private boolean handleEnter() {
        errorMessage = "";
        successMessage = "";

        try {
            if (isSignupMode) {
                // Signup
                if (name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
                    errorMessage = "All fields are required";
                    return false;
                }

                SignUpRequestDTO signUpRequest = new SignUpRequestDTO(name.trim(), email.trim(), password);
                UserDTO response = authenticationService.signUp(signUpRequest);
                successMessage = "Signup successful! Logging in...";

                // Wait a moment to show success message
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Navigate to main menu
                navigateToMainMenu(response);
                return true;

            } else {
                // Login
                if (email.trim().isEmpty() || password.trim().isEmpty()) {
                    errorMessage = "Email and password are required";
                    return false;
                }

                LoginRequestDTO loginRequest = new LoginRequestDTO(email.trim(), password);
                boolean loginSuccess = authenticationService.login(loginRequest);

                if (!loginSuccess) {
                    errorMessage = "Invalid email or password";
                    return false;
                }

                successMessage = "Login successful!";

                UserDTO user = authenticationService.getUserByEmail(email.trim());

                // Wait a moment to show success message
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Navigate to main menu
                navigateToMainMenu(user);
                return true;
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            return false;
        }
    }

    private void navigateToMainMenu(UserDTO user) throws IOException {
        MainMenuScreen mainMenu = new MainMenuScreen(
                screen,
                user,
                courseService,
                toDoListService,
                calendarService
        );
        mainMenu.display();
    }
}
