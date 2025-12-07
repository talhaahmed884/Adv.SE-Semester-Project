package com.cpp.project.ui.state.course;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.component.SelectionList;
import com.cpp.project.ui.core.ScreenState;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.text.SimpleDateFormat;

/**
 * State 3: Course Details View
 */
public class CourseDetailsState implements ScreenState {
    private final Screen screen;
    private final CourseDTO course;
    private final CourseListState listState;
    private final SimpleDateFormat dateFormat;
    private final Runnable reloadCourses;
    private final SelectionList<CourseTaskDTO> taskList;
    private final MessagePanel messagePanel;

    public CourseDetailsState(Screen screen, CourseDTO course, CourseListState listState,
                              SimpleDateFormat dateFormat, Runnable reloadCourses) {
        this.screen = screen;
        this.course = course;
        this.listState = listState;
        this.dateFormat = dateFormat;
        this.reloadCourses = reloadCourses;

        this.taskList = new SelectionList<>("Tasks", task ->
                String.format("%s - %s [%d%%] (%s)",
                        task.getName(),
                        task.getStatus(),
                        task.getProgress(),
                        dateFormat.format(task.getDeadline()))
        );

        if (course.getTasks() != null) {
            taskList.setItems(course.getTasks());
        }
        taskList.setFocused(true);
        messagePanel = new MessagePanel();
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = screen.getTerminalSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== COURSE DETAILS ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "F2: Add Task | F3: Update Progress | ESC: Back");

        // Course info
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        String courseTitle = course.getCode() + " - " + course.getName() +
                " [" + course.getProgress() + "% complete]";
        graphics.putString(3, 5, "Course: " + courseTitle);

        // Task list
        taskList.render(graphics, 3, 7);

        // Messages
        messagePanel.render(graphics, 3, size.getRows() - 2);
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.F2) {
            // Subclasses should override to provide proper dependencies
            throw new UnsupportedOperationException("Subclass must handle F2 key");
        } else if (keyStroke.getKeyType() == KeyType.F3) {
            if (taskList.isEmpty()) {
                messagePanel.setError("No tasks available to update");
                return this;
            }
            return createUpdateProgressState(taskList.getSelectedItem());
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            reloadCourses.run();
            return listState;
        } else {
            taskList.handleInput(keyStroke);
            return this;
        }
    }

    /**
     * Factory method for creating UpdateProgressState - subclasses can override to provide proper dependencies
     */
    protected UpdateProgressState createUpdateProgressState(CourseTaskDTO task) {
        throw new UnsupportedOperationException("Subclass must override createUpdateProgressState");
    }

    /**
     * Protected getter for course - allows subclasses to access the course
     */
    protected CourseDTO getCourse() {
        return course;
    }

    @Override
    public String getStateName() {
        return "CourseDetails";
    }

    public void setSuccessMessage(String message) {
        messagePanel.setSuccess(message);
    }
}
