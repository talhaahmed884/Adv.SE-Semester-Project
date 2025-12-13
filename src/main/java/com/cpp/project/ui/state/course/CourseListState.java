package com.cpp.project.ui.state.course;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.component.SelectionList;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.mediator.CourseMediator;
import com.cpp.project.ui.util.UILayoutConstants;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.List;

/**
 * State 1: Course List View
 *
 * Responsibilities:
 * - Display list of all courses
 * - Handle navigation to details or add course
 * - No data ownership - fetches fresh from mediator
 */
public class CourseListState implements ScreenState {
    private final CourseMediator mediator;
    private final SelectionList<CourseDTO> courseList;
    private final MessagePanel messagePanel;

    public CourseListState(CourseMediator mediator, String successMessage) {
        this.mediator = mediator;

        courseList = new SelectionList<>("Your Courses", course ->
                course.getCode() + " - " + course.getName()
        );
        courseList.setFocused(true);

        messagePanel = new MessagePanel();
        if (successMessage != null) {
            messagePanel.setSuccess(successMessage);
        }
    }

    @Override
    public void onEnter() {
        // Fetch fresh data when entering this state
        List<CourseDTO> courses = mediator.getAllCourses();
        courseList.setItems(courses);
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== COURSE MANAGEMENT ===";
        graphics.putString(UILayoutConstants.centerX(size, title.length()), UILayoutConstants.TITLE_ROW, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, UILayoutConstants.INSTRUCTIONS_ROW, "F1: Add Course | ESC: Back to Main Menu");

        // Course list
        courseList.render(graphics, UILayoutConstants.LEFT_MARGIN, UILayoutConstants.CONTENT_START_ROW);

        if (!courseList.isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(UILayoutConstants.LEFT_MARGIN, 17, "Press ENTER to view course details");
        }

        // Messages
        messagePanel.render(graphics, UILayoutConstants.LEFT_MARGIN, UILayoutConstants.messageRow(size));
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.F1) {
            // Notify mediator - it will create and transition to AddCourseState
            mediator.onAddNewCourse();
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            mediator.closeScreen();
            return null;
        } else if (keyStroke.getKeyType() == KeyType.Enter && !courseList.isEmpty()) {
            // Notify mediator to show details
            mediator.onViewCourseDetails(courseList.getSelectedItem().getId());
            return null; // Mediator handles transition
        } else {
            courseList.handleInput(keyStroke);
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "CourseList";
    }
}
