package com.cpp.project.ui.state.course;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.component.SelectionList;
import com.cpp.project.ui.core.ScreenState;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.util.List;

/**
 * State 1: Course List View
 */
public class CourseListState implements ScreenState {
    private final Screen screen;
    private final List<CourseDTO> courses;
    private final Runnable onClose;
    private final SelectionList<CourseDTO> courseList;
    private final MessagePanel messagePanel;

    public CourseListState(Screen screen, List<CourseDTO> courses, Runnable onClose) {
        this.screen = screen;
        this.courses = courses;
        this.onClose = onClose;

        courseList = new SelectionList<>("Your Courses", course ->
                course.getCode() + " - " + course.getName()
        );
        courseList.setItems(courses);
        courseList.setFocused(true);
        messagePanel = new MessagePanel();
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = screen.getTerminalSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== COURSE MANAGEMENT ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "F1: Add Course | ESC: Back to Main Menu");

        // Course list
        courseList.render(graphics, 3, 5);

        if (!courseList.isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(3, 17, "Press ENTER to view course details");
        }

        // Messages
        messagePanel.render(graphics, 3, size.getRows() - 2);
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.F1) {
            // Subclasses should override to provide proper dependencies
            throw new UnsupportedOperationException("Subclass must handle F1 key");
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            onClose.run();
            return this;
        } else if (keyStroke.getKeyType() == KeyType.Enter && !courseList.isEmpty()) {
            return createCourseDetailsState(courseList.getSelectedItem());
        } else {
            courseList.handleInput(keyStroke);
            return this;
        }
    }

    /**
     * Factory method for creating CourseDetailsState - subclasses can override to provide proper dependencies
     */
    protected CourseDetailsState createCourseDetailsState(CourseDTO course) {
        throw new UnsupportedOperationException("Subclass must override createCourseDetailsState");
    }

    @Override
    public String getStateName() {
        return "CourseList";
    }

    public void setSuccessMessage(String message) {
        messagePanel.setSuccess(message);
    }
}
