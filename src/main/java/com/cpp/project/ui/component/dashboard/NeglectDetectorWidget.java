package com.cpp.project.ui.component.dashboard;

import com.cpp.project.dashboard.dto.CourseStudyTimeDTO;
import com.cpp.project.ui.component.AbstractComponent;
import com.cpp.project.ui.strategy.BarChartFormatterStrategy;
import com.cpp.project.ui.util.TimerFormatUtils;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.List;

/**
 * Widget for displaying course study time distribution
 * "Neglect Detector" - Shows which courses are being neglected (0 hours)
 * Component Pattern - Reusable UI component
 * Uses Strategy Pattern (BarChartFormatterStrategy) for formatting
 */
public class NeglectDetectorWidget extends AbstractComponent {
    private static final int HEADER_HEIGHT = 3;
    private static final int MAX_COURSES_DISPLAYED = 10;

    private List<CourseStudyTimeDTO> courseStudyTimes;
    private final BarChartFormatterStrategy barChartFormatter;

    public NeglectDetectorWidget(List<CourseStudyTimeDTO> courseStudyTimes) {
        super(calculateHeight(courseStudyTimes));
        this.courseStudyTimes = courseStudyTimes;
        this.barChartFormatter = new BarChartFormatterStrategy();
    }

    /**
     * Calculate component height based on number of courses
     */
    private static int calculateHeight(List<CourseStudyTimeDTO> courseStudyTimes) {
        int courseCount = courseStudyTimes != null ?
                Math.min(courseStudyTimes.size(), MAX_COURSES_DISPLAYED) : 0;
        return HEADER_HEIGHT + courseCount;
    }

    /**
     * Update the course study time data
     */
    public void setCourseStudyTimes(List<CourseStudyTimeDTO> courseStudyTimes) {
        this.courseStudyTimes = courseStudyTimes;
        this.height = calculateHeight(courseStudyTimes);
    }

    @Override
    public void render(TextGraphics graphics, int x, int y) {
        if (courseStudyTimes == null || courseStudyTimes.isEmpty()) {
            renderNoData(graphics, x, y);
            return;
        }

        // Title
        graphics.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
        graphics.putString(x, y, "╔══════════════════════════════════════════════════════════════╗");
        graphics.putString(x, y + 1, "║  NEGLECT DETECTOR - Course Study Time Distribution           ║");
        graphics.putString(x, y + 2, "╚══════════════════════════════════════════════════════════════╝");

        int currentY = y + HEADER_HEIGHT;

        // Display each course with bar chart
        int displayCount = Math.min(courseStudyTimes.size(), MAX_COURSES_DISPLAYED);
        for (int i = 0; i < displayCount; i++) {
            CourseStudyTimeDTO course = courseStudyTimes.get(i);
            renderCourseBar(graphics, x, currentY, course);
            currentY++;
        }

        // Show warning if there are more courses
        if (courseStudyTimes.size() > MAX_COURSES_DISPLAYED) {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            String moreText = String.format("... and %d more courses",
                    courseStudyTimes.size() - MAX_COURSES_DISPLAYED);
            graphics.putString(x, currentY, moreText);
        }
    }

    /**
     * Render a single course bar chart
     */
    private void renderCourseBar(TextGraphics graphics, int x, int y, CourseStudyTimeDTO course) {
        // Format course label (code + name, truncated to 20 chars)
        String label = String.format("%s: %s", course.getCourseCode(), course.getCourseName());
        if (label.length() > 20) {
            label = label.substring(0, 17) + "...";
        }

        // Format time string
        String timeStr = course.getTotalMillis() > 0 ?
                TimerFormatUtils.formatDuration(course.getTotalMillis()) : "0s";

        // Use bar chart formatter strategy
        String barChart = barChartFormatter.formatWithTime(
                course.getPercentage(),
                label,
                timeStr
        );

        // Color code: Red if 0 hours (neglected), Green if has time
        if (course.getTotalMillis() == 0) {
            graphics.setForegroundColor(TextColor.ANSI.RED);
        } else if (course.getPercentage() > 50) {
            graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
        } else if (course.getPercentage() > 20) {
            graphics.setForegroundColor(TextColor.ANSI.GREEN);
        } else {
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        }

        graphics.putString(x, y, barChart);
    }

    private void renderNoData(TextGraphics graphics, int x, int y) {
        graphics.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
        graphics.putString(x, y, "╔══════════════════════════════════════════════════════════════╗");
        graphics.putString(x, y + 1, "║  NEGLECT DETECTOR - Course Study Time Distribution          ║");
        graphics.putString(x, y + 2, "╚══════════════════════════════════════════════════════════════╝");

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(x, y + 3, "No courses found. Create a course to start tracking study time!");
    }

    @Override
    public boolean handleInput(KeyStroke keyStroke) {
        // This component doesn't handle input (display only)
        return false;
    }
}
