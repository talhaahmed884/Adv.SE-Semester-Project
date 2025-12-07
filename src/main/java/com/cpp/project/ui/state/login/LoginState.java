package com.cpp.project.ui.state.login;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.ui.component.Form;
import com.cpp.project.ui.component.FormField;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.factory.ComponentFactory;
import com.cpp.project.ui.strategy.RequiredFieldStrategy;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.util.function.Consumer;

/**
 * State Pattern: Login mode state
 */
public class LoginState implements ScreenState {
    private final Form form;
    private final MessagePanel messagePanel;
    private final FormField emailField;
    private final FormField passwordField;
    private final Screen screen;
    private final AuthenticationService authenticationService;
    private final Consumer<UserDTO> onLoginSuccess;
    private final Runnable onClose;

    public LoginState(
            Screen screen,
            AuthenticationService authenticationService,
            Consumer<UserDTO> onLoginSuccess,
            Runnable onClose) {
        this.screen = screen;
        this.authenticationService = authenticationService;
        this.onLoginSuccess = onLoginSuccess;
        this.onClose = onClose;

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
            return new SignupState(screen, authenticationService, onLoginSuccess, onClose);
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            onClose.run();
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
            onLoginSuccess.accept(user);

        } catch (Exception e) {
            messagePanel.setError(e.getMessage());
        }
    }

    @Override
    public String getStateName() {
        return "Login";
    }
}
