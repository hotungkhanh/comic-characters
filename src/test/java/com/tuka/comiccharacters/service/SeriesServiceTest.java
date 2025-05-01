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
        @DisplayName("Given null series when validateEntity called then IllegalArgumentException is thrown")
        void givenNullSeries_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(null));
        }

        @Test
        @DisplayName("Given null title when validateEntity called then IllegalArgumentException is thrown")
        void givenNullTitle_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            Series series = new Series();
            series.setTitle(null);
            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(series));
        }

        @Test
        @DisplayName("Given blank title when validateEntity called then IllegalArgumentException is thrown")
        void givenBlankTitle_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            Series series = new Series();
            series.setTitle("   ");
            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(series));
        }

        @Test
        @DisplayName("Given invalid ID when validateEntity called then IllegalArgumentException is thrown")
        void givenInvalidId_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            Series series = new Series();
            series.setTitle("X-Men");
            setPrivateId(series, -5L);
            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(series));
        }

        @Test
        @DisplayName("Given start year out of valid range when validateEntity called then IllegalArgumentException is thrown")
        void givenInvalidStartYear_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            Series series = new Series();
            series.setTitle("Fantastic Four");
            series.setStartYear(999);
            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(series));

            series.setStartYear(Year.now().getValue() + 6);
            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(series));
        }

        @Test
        @DisplayName("Given end year before start year when validateEntity called then IllegalArgumentException is thrown")
        void givenEndYearBeforeStartYear_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            Series series = new Series();
            series.setTitle("Hulk");
            series.setStartYear(2000);
            series.setEndYear(1999);
            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(series));
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
        @DisplayName("Given null series when getIssuesBySeries called then IllegalArgumentException is thrown")
        void givenNullSeries_whenGetIssuesBySeriesCalled_thenIllegalArgumentExceptionThrown() {
            assertThrows(IllegalArgumentException.class, () -> service.getIssuesBySeries(null));
        }

        @Test
        @DisplayName("Given series with null ID when getIssuesBySeries called then IllegalArgumentException is thrown")
        void givenSeriesWithNullId_whenGetIssuesBySeriesCalled_thenIllegalArgumentExceptionThrown() {
            Series series = new Series();
            setPrivateId(series, null);
            assertThrows(IllegalArgumentException.class, () -> service.getIssuesBySeries(series));
        }
    }
}
