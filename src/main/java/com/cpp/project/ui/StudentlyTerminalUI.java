package com.cpp.project.ui;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.screen.LoginScreen;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main application class for Studently Terminal UI
 * Uses Lanterna framework for text-based terminal interface
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.cpp.project")
@EntityScan(basePackages = "com.cpp.project")
@EnableJpaRepositories(basePackages = "com.cpp.project")
public class StudentlyTerminalUI implements CommandLineRunner {
    private final ApplicationContext applicationContext;

    public StudentlyTerminalUI(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static void main(String[] args) {
        // Set system property to indicate UI mode
        System.setProperty("studently.ui.mode", "terminal");

        SpringApplication app = new SpringApplication(StudentlyTerminalUI.class);
        // Disable web server since we're running terminal UI
        app.setAdditionalProfiles("ui");
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Get required services from Spring context
        AuthenticationService authenticationService = applicationContext.getBean(AuthenticationService.class);
        CourseService courseService = applicationContext.getBean(CourseService.class);
        ToDoListService toDoListService = applicationContext.getBean(ToDoListService.class);
        CalendarService calendarService = applicationContext.getBean(CalendarService.class);

        // Initialize terminal
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        try {
            // Start with login screen
            LoginScreen loginScreen = new LoginScreen(
                    screen,
                    authenticationService,
                    courseService,
                    toDoListService,
                    calendarService
            );

            loginScreen.display();

        } finally {
            screen.stopScreen();
        }

        // Exit application after UI closes
        System.exit(0);
    }
}
