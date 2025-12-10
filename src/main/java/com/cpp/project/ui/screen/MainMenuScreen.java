package com.cpp.project.ui.screen;

import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.component.SelectionList;
import com.cpp.project.ui.component.menu.MenuItem;
import com.cpp.project.ui.core.UIScreen;
import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user.service.UserService;
import com.cpp.project.user_credential.service.UserCredentialService;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

/**
 * Refactored Main Menu Screen using design patterns:
 * - Template Method Pattern: Extends UIScreen
 * - Component Pattern: Uses SelectionList component
 */
public class MainMenuScreen extends UIScreen {
    private final UserDTO currentUser;
    private final CourseService courseService;
    private final ToDoListService toDoListService;
    private final CalendarService calendarService;
    private final UserService userService;
    private final UserCredentialService credentialService;
    private final SelectionList<MenuItem> menuList;

    public MainMenuScreen(
            Screen screen,
            UserDTO currentUser,
            CourseService courseService,
            ToDoListService toDoListService,
            CalendarService calendarService,
            UserService userService,
            UserCredentialService credentialService) {
        super(screen);
        this.currentUser = currentUser;
        this.courseService = courseService;
        this.toDoListService = toDoListService;
        this.calendarService = calendarService;
        this.userService = userService;
        this.credentialService = credentialService;

        // Create menu items
        this.menuList = new SelectionList<>("Main Menu", MenuItem::getLabel);
        this.menuList.setItems(java.util.Arrays.asList(
                new MenuItem("1. Manage Courses", this::openCourseManagement),
                new MenuItem("2. Manage To-Do Lists", this::openToDoManagement),
                new MenuItem("3. View Calendar", this::openCalendar),
                new MenuItem("4. My Profile", this::openUserProfile),
                new MenuItem("5. Logout", this::logout)
        ));
        this.menuList.setFocused(true);
    }

    @Override
    protected void render() {
        TextGraphics graphics = screen.newTextGraphics();
        TerminalSize size = screen.getTerminalSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== STUDENTLY - MAIN MENU ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 2, title);

        // Welcome message
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        String welcome = "Welcome, " + currentUser.getName() + "!";
        graphics.putString((size.getColumns() - welcome.length()) / 2, 4, welcome);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(5, 6, "Use Arrow Keys to navigate, ENTER to select, ESC to exit");

        // Menu list
        menuList.render(graphics, 10, 9);
    }

    @Override
    protected void handleInput() throws IOException {
        KeyStroke keyStroke = screen.readInput();

        if (keyStroke.getKeyType() == KeyType.Escape) {
            close();
        } else if (keyStroke.getKeyType() == KeyType.Enter) {
            MenuItem selected = menuList.getSelectedItem();
            if (selected != null) {
                selected.executeAction();
            }
        } else {
            menuList.handleInput(keyStroke);
        }
    }

    private void openCourseManagement() {
        try {
            CourseManagementScreen courseScreen = new CourseManagementScreen(
                    screen, currentUser, courseService
            );
            courseScreen.display();
        } catch (IOException e) {
            // Handle error
        }
    }

    private void openToDoManagement() {
        try {
            ToDoListManagementScreen todoScreen = new ToDoListManagementScreen(
                    screen, currentUser, toDoListService
            );
            todoScreen.display();
        } catch (IOException e) {
            // Handle error
        }
    }

    private void openCalendar() {
        try {
            CalendarScreen calendarScreen = new CalendarScreen(
                    screen, currentUser, calendarService
            );
            calendarScreen.display();
        } catch (IOException e) {
            // Handle error
        }
    }

    private void openUserProfile() {
        try {
            UserScreen userScreen = new UserScreen(
                    screen, currentUser, userService, credentialService
            );
            userScreen.display();
        } catch (IOException e) {
            // Handle error
        }
    }

    private void logout() {
        close();
    }
}
