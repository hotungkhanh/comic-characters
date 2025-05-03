package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Series;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Year;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

class SeriesServiceTest {

    private final SeriesService service = Mockito.spy(new SeriesService());

    private void setPrivateId(Object target, Long idValue) {
        try {
            var field = target.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(target, idValue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID via reflection", e);
        }
    }

    @Nested
    @DisplayName("validateEntity method tests")
    class ValidateEntityTests {

        @Test
        @DisplayName("Given valid series when validateEntity called then no exception is thrown")
        void givenValidSeries_whenValidateEntityCalled_thenNoExceptionThrown() {
            Series series = new Series();
            series.setTitle("The Amazing Spider-Man");
            series.setStartYear(1963);
            series.setEndYear(1970);
            assertDoesNotThrow(() -> service.validateEntity(series));
        }

        @Test
        @DisplayName("Given null series when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenNullSeries_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(null));
            assertEquals("Series cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Given null title when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenNullTitle_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            Series series = new Series();
            series.setTitle(null);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(series));
            assertEquals("Series title cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Given blank title when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenBlankTitle_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            Series series = new Series();
            series.setTitle("   ");
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(series));
            assertEquals("Series title cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Given invalid ID when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenInvalidId_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            Series series = new Series();
            series.setTitle("X-Men");
            setPrivateId(series, -5L);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(series));
            assertEquals("Invalid series ID", exception.getMessage());
        }

        @Test
        @DisplayName("Given start year out of valid range when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenInvalidStartYear_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            Series series = new Series();
            series.setTitle("Fantastic Four");
            series.setStartYear(999);
            IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(series));
            assertEquals("Series start year must be between 1000 and " + (Year.now().getValue() + 5), exception1.getMessage());

            series.setStartYear(Year.now().getValue() + 6);
            IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(series));
            assertEquals("Series start year must be between 1000 and " + (Year.now().getValue() + 5), exception2.getMessage());
        }

        @Test
        @DisplayName("Given end year before start year when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenEndYearBeforeStartYear_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            Series series = new Series();
            series.setTitle("Hulk");
            series.setStartYear(2000);
            series.setEndYear(1999);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(series));
            assertEquals("Series end year must be after or equal to start year", exception.getMessage());
        }

        @Test
        @DisplayName("Given overview over 3000 characters when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenLongOverview_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            // Given
            Series series = new Series();
            series.setTitle("Long Title");
            series.setOverview("a".repeat(3001));

            // When
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(series));

            // Then
            assertEquals("Series overview must be 3000 characters or fewer.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("getIssuesBySeries method tests")
    class GetIssuesBySeriesTests {

        @Test
        @DisplayName("Given valid series with ID when getIssuesBySeries called then issues are returned")
        void givenValidSeries_whenGetIssuesBySeriesCalled_thenIssuesReturned() {
            Series series = new Series();
            setPrivateId(series, 1L);

            Issue issue1 = new Issue();
            Issue issue2 = new Issue();
            Series loadedSeries = new Series();
            loadedSeries.setIssues(Set.of(issue1, issue2));

            doReturn(loadedSeries).when(service).getByIdWithDetails(1L);

            List<Issue> issues = service.getIssuesBySeries(series);

            assertNotNull(issues);
            assertEquals(2, issues.size());
        }

        @Test
        @DisplayName("Given null series when getIssuesBySeries called then IllegalArgumentException with correct message is thrown")
        void givenNullSeries_whenGetIssuesBySeriesCalled_thenIllegalArgumentExceptionThrown() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.getIssuesBySeries(null));
            assertEquals("Invalid series provided.", exception.getMessage());
        }

        @Test
        @DisplayName("Given series with null ID when getIssuesBySeries called then IllegalArgumentException with correct message is thrown")
        void givenSeriesWithNullId_whenGetIssuesBySeriesCalled_thenIllegalArgumentExceptionThrown() {
            Series series = new Series();
            setPrivateId(series, null);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.getIssuesBySeries(series));
            assertEquals("Invalid series provided.", exception.getMessage());
        }
    }
}
