package com.cpp.project.uc_10_calendar_items_for_month;

import com.cpp.project.calendar.service.CalendarServiceImpl;
import com.cpp.project.course.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UC-10.23: Attempts to fetch all tasks when repository fails
 */
@ExtendWith(MockitoExtension.class)
public class UC_10_23_ItemsForMonth_UnavailableRepositoryFails_Test {
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CalendarServiceImpl calendarService;

    @Test
    @DisplayName("UC-10.23: Handles repository failure gracefully")
    public void testItemsForMonthUnavailableRepositoryFails() {
        // Arrange
        UUID userId = UUID.randomUUID();

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        // Mock repository to throw exception
        when(courseRepository.findByUserId(any(UUID.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert - Should throw exception when repository fails
        assertThrows(RuntimeException.class, () -> {
            calendarService.getItemsForMonth(year, month, userId);
        });

        verify(courseRepository, times(1)).findByUserId(userId);
    }
}
