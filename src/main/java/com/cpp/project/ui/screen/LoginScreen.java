package com.cpp.project.ui.screen;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.component.Form;
import com.cpp.project.ui.component.FormField;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.core.StatefulScreen;
import com.cpp.project.ui.factory.ComponentFactory;
import com.cpp.project.ui.strategy.RequiredFieldStrategy;
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
 * Refactored Login Screen using design patterns:
 * - State Pattern: Login vs Signup modes
 * - Component Pattern: Reusable form fields
 * - Strategy Pattern: Validation strategies
 */
public class LoginScreen extends StatefulScreen {
    private final AuthenticationService authenticationService;
    private final CourseService courseService;
    private final ToDoListService toDoListService;
    private final CalendarService calendarService;

    public LoginScreen(
            Screen screen,
            AuthenticationService authenticationService,
            CourseService courseService,
            ToDoListService toDoListService,
            CalendarService calendarService) {
        super(screen);
        this.authenticationService = authenticationService;
        this.courseService = courseService;
        this.toDoListService = toDoListService;
        this.calendarService = calendarService;

        // Start with login state
        this.currentState = new LoginState();
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

    /**
     * State Pattern: Login mode state
     */
    private class LoginState implements ScreenState {
        private final Form form;
        private final MessagePanel messagePanel;
        private final FormField emailField;
        private final FormField passwordField;

        public LoginState() {
            emailField = ComponentFactory.createEmailField("Email");
            passwordField = ComponentFactory.createPasswordField("Password");

            form = new Form()
                    .addField(emailField)
                    .addField(passwordField);

            messagePanel = new MessagePanel();
        }

        @Override
        public void onEnter() {
            form.setFocused(true);
        }

        @Override
        public void render(TextGraphics graphics) {
            TerminalSize size = screen.getTerminalSize();

            // Title
            graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
            String title = "=== STUDENTLY - LOGIN ===";
            graphics.putString((size.getColumns() - title.length()) / 2, 2, title);

            // Instructions
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(5, 5, "F1: Switch to Signup");
            graphics.putString(5, 6, "Tab: Next field");
            graphics.putString(5, 7, "ESC: Exit");

            // Form
            form.render(graphics, 5, 10);

            // Submit instruction
            graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
            graphics.putString(5, 15, "Press ENTER to Login");

            // Messages
            messagePanel.render(graphics, 5, 17);
        }

        @Override
        public ScreenState handleInput(KeyStroke keyStroke) {
            messagePanel.clear();

            if (keyStroke.getKeyType() == KeyType.F1) {
                return new SignupState();
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                close();
                return this;
            } else if (keyStroke.getKeyType() == KeyType.Enter) {
                handleLogin();
                return this;
            } else {
                form.handleInput(keyStroke);
                return this;
            }
        }

        private void handleLogin() {
            String email = emailField.getValue().trim();
            String password = passwordField.getValue();

            // Validation
            String emailError = new RequiredFieldStrategy("Email").validate(email);
            if (emailError != null) {
                messagePanel.setError(emailError);
                return;
            }

            String passwordError = new RequiredFieldStrategy("Password").validate(password);
            if (passwordError != null) {
                messagePanel.setError(passwordError);
                return;
            }

            try {
                LoginRequestDTO loginRequest = new LoginRequestDTO(email, password);
                boolean loginSuccess = authenticationService.login(loginRequest);

                if (!loginSuccess) {
                    messagePanel.setError("Invalid email or password");
                    return;
                }

                UserDTO user = authenticationService.getUserByEmail(email);
                messagePanel.setSuccess("Login successful!");

                // Wait to show success message
                Thread.sleep(500);

                // Navigate to main menu
                navigateToMainMenu(user);

            } catch (Exception e) {
                messagePanel.setError(e.getMessage());
            }
        }

        @Override
        public String getStateName() {
            return "Login";
        }
    }

    /**
     * State Pattern: Signup mode state
     */
    private class SignupState implements ScreenState {
        private final Form form;
        private final MessagePanel messagePanel;
        private final FormField nameField;
        private final FormField emailField;
        private final FormField passwordField;

        public SignupState() {
            nameField = ComponentFactory.createTextField("Name");
            emailField = ComponentFactory.createEmailField("Email");
            passwordField = ComponentFactory.createPasswordField("Password");

            form = new Form()
                    .addField(nameField)
                    .addField(emailField)
                    .addField(passwordField);

            messagePanel = new MessagePanel();
        }

        @Override
        public void onEnter() {
            form.setFocused(true);
        }

        @Override
        public void render(TextGraphics graphics) {
            TerminalSize size = screen.getTerminalSize();

            // Title
            graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
            String title = "=== STUDENTLY - SIGN UP ===";
            graphics.putString((size.getColumns() - title.length()) / 2, 2, title);

            // Instructions
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(5, 5, "F1: Switch to Login");
            graphics.putString(5, 6, "Tab: Next field");
            graphics.putString(5, 7, "ESC: Exit");

            // Form
            form.render(graphics, 5, 10);

            // Submit instruction
            graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
            graphics.putString(5, 17, "Press ENTER to Sign Up");

            // Messages
            messagePanel.render(graphics, 5, 19);
        }

        @Override
        public ScreenState handleInput(KeyStroke keyStroke) {
            messagePanel.clear();

            if (keyStroke.getKeyType() == KeyType.F1) {
                return new LoginState();
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                close();
                return this;
            } else if (keyStroke.getKeyType() == KeyType.Enter) {
                handleSignup();
                return this;
            } else {
                form.handleInput(keyStroke);
                return this;
            }
        }

        private void handleSignup() {
            String name = nameField.getValue().trim();
            String email = emailField.getValue().trim();
            String password = passwordField.getValue();

            // Validation
            String nameError = new RequiredFieldStrategy("Name").validate(name);
            if (nameError != null) {
                messagePanel.setError(nameError);
                return;
            }

            String emailError = new RequiredFieldStrategy("Email").validate(email);
            if (emailError != null) {
                messagePanel.setError(emailError);
                return;
            }

            String passwordError = new RequiredFieldStrategy("Password").validate(password);
            if (passwordError != null) {
                messagePanel.setError(passwordError);
                return;
            }

            try {
                SignUpRequestDTO signUpRequest = new SignUpRequestDTO(name, email, password);
                UserDTO user = authenticationService.signUp(signUpRequest);
                messagePanel.setSuccess("Signup successful! Logging in...");

                // Wait to show success message
                Thread.sleep(1000);

                // Navigate to main menu
                navigateToMainMenu(user);

            } catch (Exception e) {
                messagePanel.setError(e.getMessage());
            }
        }

        @Override
        public String getStateName() {
            return "Signup";
        }
    }
}
