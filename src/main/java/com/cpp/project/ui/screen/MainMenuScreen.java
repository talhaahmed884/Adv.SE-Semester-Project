package com.cpp.project.ui.screen;

import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

/**
 * Main menu screen for navigation
 */
public class MainMenuScreen {
    private final Screen screen;
    private final UserDTO currentUser;
    private final CourseService courseService;
    private final ToDoListService toDoListService;
    private final CalendarService calendarService;
    private final String[] menuOptions = {
            "1. Manage Courses",
            "2. Manage To-Do Lists",
            "3. View Calendar",
            "4. Logout"
    };
    private int selectedOption = 0;

    public MainMenuScreen(
            Screen screen,
            UserDTO currentUser,
            CourseService courseService,
            ToDoListService toDoListService,
            CalendarService calendarService) {
        this.screen = screen;
        this.currentUser = currentUser;
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
            if (keyStroke.getKeyType() == KeyType.ArrowUp) {
                selectedOption = (selectedOption - 1 + menuOptions.length) % menuOptions.length;
            } else if (keyStroke.getKeyType() == KeyType.ArrowDown) {
                selectedOption = (selectedOption + 1) % menuOptions.length;
            } else if (keyStroke.getKeyType() == KeyType.Enter) {
                running = !handleSelection();
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                running = false;
            }
        }
    }

    private void render() {
        TextGraphics graphics = screen.newTextGraphics();
        TerminalSize size = screen.getTerminalSize();

        // Title
        String title = "=== STUDENTLY - MAIN MENU ===";
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        graphics.putString((size.getColumns() - title.length()) / 2, 2, title);

        // Welcome message
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        String welcome = "Welcome, " + currentUser.getName() + "!";
        graphics.putString((size.getColumns() - welcome.length()) / 2, 4, welcome);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(5, 6, "Use Arrow Keys to navigate, ENTER to select, ESC to exit");

        int startY = 9;

        // Menu options
        for (int i = 0; i < menuOptions.length; i++) {
            if (i == selectedOption) {
                graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
                graphics.putString(10, startY + i * 2, ">> " + menuOptions[i] + " <<");
            } else {
                graphics.setForegroundColor(TextColor.ANSI.WHITE);
                graphics.putString(10, startY + i * 2, "   " + menuOptions[i]);
            }
        }
    }

    private boolean handleSelection() throws IOException {
        switch (selectedOption) {
            case 0: // Manage Courses
                CourseManagementScreen courseScreen = new CourseManagementScreen(
                        screen, currentUser, courseService
                );
                courseScreen.display();
                return false;

            case 1: // Manage To-Do Lists
                ToDoListManagementScreen todoScreen = new ToDoListManagementScreen(
                        screen, currentUser, toDoListService
                );
                todoScreen.display();
                return false;

            case 2: // View Calendar
                CalendarScreen calendarScreen = new CalendarScreen(
                        screen, currentUser, calendarService
                );
                calendarScreen.display();
                return false;

            case 3: // Logout
                return true;

            default:
                return false;
        }
    }
}
