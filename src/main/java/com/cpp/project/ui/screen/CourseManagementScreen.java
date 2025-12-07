package com.cpp.project.ui.screen;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.core.StatefulScreen;
import com.cpp.project.ui.state.course.*;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Refactored Course Management Screen using design patterns:
 * - State Pattern: Separate state classes in ui.state.course package
 * - Component Pattern: Reusable UI components
 * - Strategy Pattern: Validation strategies
 */
public class CourseManagementScreen extends StatefulScreen {
    private final UserDTO currentUser;
    private final CourseService courseService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    private List<CourseDTO> courses;

    public CourseManagementScreen(Screen screen, UserDTO currentUser, CourseService courseService) {
        super(screen);
        this.currentUser = currentUser;
        this.courseService = courseService;
        reloadCourses();
        this.currentState = createCourseListState();
        // Call onEnter to set initial focus
        this.currentState.onEnter();
    }

    private void reloadCourses() {
        courses = courseService.getCoursesByUserId(currentUser.getId());
    }

    private List<CourseDTO> getCourses() {
        return courses;
    }

    private CourseListState createCourseListState() {
        return new CourseListStateAdapter();
    }

    /**
     * Adapter that handles state transitions with proper dependencies
     */
    private class CourseListStateAdapter extends CourseListState {
        public CourseListStateAdapter() {
            super(screen, courses, CourseManagementScreen.this::close);
        }

        @Override
        public ScreenState handleInput(KeyStroke keyStroke) {
            // Intercept F1 to provide all dependencies to AddCourseState
            if (keyStroke.getKeyType() == KeyType.F1) {
                return new AddCourseState(
                        screen,
                        this,
                        courseService,
                        currentUser,
                        CourseManagementScreen.this::reloadCourses,
                        CourseManagementScreen.this::createCourseListState
                );
            }
            // Let parent handle other inputs but intercept Enter for CourseDetails
            return super.handleInput(keyStroke);
        }

        @Override
        protected CourseDetailsState createCourseDetailsState(CourseDTO course) {
            return new CourseDetailsStateAdapter(course, this);
        }
    }

    /**
     * Adapter that handles course details state transitions with proper dependencies
     */
    private class CourseDetailsStateAdapter extends CourseDetailsState {
        public CourseDetailsStateAdapter(CourseDTO course, CourseListState listState) {
            super(screen, course, listState, dateFormat, CourseManagementScreen.this::reloadCourses);
        }

        @Override
        public ScreenState handleInput(KeyStroke keyStroke) {
            // Intercept F2 to provide all dependencies to AddTaskState
            if (keyStroke.getKeyType() == KeyType.F2) {
                return new AddTaskState(
                        screen,
                        getCourse(),
                        this,
                        courseService,
                        CourseManagementScreen.this::reloadCourses,
                        CourseManagementScreen.this::getCourses,
                        dateFormat,
                        CourseManagementScreen.this::createCourseListState
                );
            }
            // Let parent handle other inputs
            return super.handleInput(keyStroke);
        }

        @Override
        protected UpdateProgressState createUpdateProgressState(CourseTaskDTO task) {
            return new UpdateProgressState(
                    screen,
                    getCourse(),
                    task,
                    this,
                    courseService,
                    CourseManagementScreen.this::reloadCourses,
                    CourseManagementScreen.this::getCourses,
                    dateFormat,
                    CourseManagementScreen.this::createCourseListState
            );
        }
    }
}
