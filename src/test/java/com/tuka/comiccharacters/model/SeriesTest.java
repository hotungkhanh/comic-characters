package com.tuka.comiccharacters.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SeriesTest {

    private static final String VALID_TITLE = "X-Men";
    private static final Integer VALID_START_YEAR = 1963;
    private Series series;

    @BeforeEach
    void setUp() {
        series = new Series(VALID_TITLE, VALID_START_YEAR);
    }

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("Given valid title and startYear when constructor called with basic params then series is created with those properties")
        void givenValidTitleAndStartYear_whenConstructorCalledWithBasicParams_thenSeriesIsCreatedWithThoseProperties() {
            // Given
            String title = "Uncanny X-Men";
            Integer startYear = 1975;

            // When
            Series series = new Series(title, startYear);

            // Then
            assertEquals(title, series.getTitle(), "Series title should match provided title");
            assertEquals(startYear, series.getStartYear(), "Series startYear should match provided startYear");
            assertNull(series.getEndYear(), "Series endYear should be null");
            assertNull(series.getOverview(), "Series overview should be null");
            assertNull(series.getPublisher(), "Series publisher should be null");
            assertNotNull(series.getIssues(), "Series issues should be initialized");
            assertTrue(series.getIssues().isEmpty(), "Series issues should be empty");
        }

        @Test
        @DisplayName("Given all params when full constructor called then series is created with all properties")
        void givenAllParams_whenFullConstructorCalled_thenSeriesIsCreatedWithAllProperties() {
            // Given
            String title = "Amazing Spider-Man";
            Integer startYear = 1963;
            Integer endYear = 1998;
            String overview = "The flagship Spider-Man title";
            Publisher publisher = new Publisher("Marvel Comics");

            // When
            Series series = new Series(title, startYear, endYear, overview, publisher);

            // Then
            assertEquals(title, series.getTitle(), "Series title should match provided title");
            assertEquals(startYear, series.getStartYear(), "Series startYear should match provided startYear");
            assertEquals(endYear, series.getEndYear(), "Series endYear should match provided endYear");
            assertEquals(overview, series.getOverview(), "Series overview should match provided overview");
            assertEquals(publisher, series.getPublisher(), "Series publisher should match provided publisher");
            assertNotNull(series.getIssues(), "Series issues should be initialized");
            assertTrue(series.getIssues().isEmpty(), "Series issues should be empty");
        }

        @Test
        @DisplayName("Given no parameters when default constructor called then series is created with null properties")
        void givenNoParameters_whenDefaultConstructorCalled_thenSeriesIsCreatedWithNullProperties() {
            // When
            Series series = new Series();

            // Then
            assertNull(series.getTitle(), "Series title should be null");
            assertNull(series.getStartYear(), "Series startYear should be null");
            assertNull(series.getEndYear(), "Series endYear should be null");
            assertNull(series.getOverview(), "Series overview should be null");
            assertNull(series.getPublisher(), "Series publisher should be null");
            assertNotNull(series.getIssues(), "Series issues should be initialized");
            assertTrue(series.getIssues().isEmpty(), "Series issues should be empty");
        }
    }

    @Nested
    @DisplayName("Getter and setter tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Given series with ID set implicitly when getId called then correct ID is returned")
        void givenSeriesWithIdSet_whenGetIdCalled_thenCorrectIdIsReturned() {
            // Note: In a real application, the ID would be set by the JPA provider
            // For testing purposes, we're using reflection to set a value

            // Given
            Long expectedId = 42L;
            try {
                java.lang.reflect.Field idField = Series.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(series, expectedId);
            } catch (Exception e) {
                fail("Failed to set up test: " + e.getMessage());
            }

            // When
            Long actualId = series.getId();

            // Then
            assertEquals(expectedId, actualId, "getId should return the set ID");
        }

        @Test
        @DisplayName("Given series with title set when getTitle called then correct title is returned")
        void givenSeriesWithTitleSet_whenGetTitleCalled_thenCorrectTitleIsReturned() {
            // Given - series already has title set in setUp

            // When
            String actualTitle = series.getTitle();

            // Then
            assertEquals(VALID_TITLE, actualTitle, "getTitle should return the set title");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Batman", "Superman", "Wonder Woman"})
        @DisplayName("Given different titles when setTitle called then title is updated correctly")
        void givenDifferentTitles_whenSetTitleCalled_thenTitleIsUpdatedCorrectly(String newTitle) {
            // When
            series.setTitle(newTitle);

            // Then
            assertEquals(newTitle, series.getTitle(), "setTitle should update the title property");
        }

        @Test
        @DisplayName("Given series with startYear set when getStartYear called then correct startYear is returned")
        void givenSeriesWithStartYearSet_whenGetStartYearCalled_thenCorrectStartYearIsReturned() {
            // Given - series already has startYear set in setUp

            // When
            Integer actualStartYear = series.getStartYear();

            // Then
            assertEquals(VALID_START_YEAR, actualStartYear, "getStartYear should return the set startYear");
        }

        @ParameterizedTest
        @ValueSource(ints = {1938, 1939, 1940})
        @DisplayName("Given different startYears when setStartYear called then startYear is updated correctly")
        void givenDifferentStartYears_whenSetStartYearCalled_thenStartYearIsUpdatedCorrectly(Integer newStartYear) {
            // When
            series.setStartYear(newStartYear);

            // Then
            assertEquals(newStartYear, series.getStartYear(), "setStartYear should update the startYear property");
        }

        @Test
        @DisplayName("Given series with no endYear when getEndYear called then null is returned")
        void givenSeriesWithNoEndYear_whenGetEndYearCalled_thenNullIsReturned() {
            // Given - series already has no endYear set in setUp

            // When
            Integer actualEndYear = series.getEndYear();

            // Then
            assertNull(actualEndYear, "getEndYear should return null when not set");
        }

        @ParameterizedTest
        @ValueSource(ints = {1986, 1999, 2010})
        @DisplayName("Given different endYears when setEndYear called then endYear is updated correctly")
        void givenDifferentEndYears_whenSetEndYearCalled_thenEndYearIsUpdatedCorrectly(Integer newEndYear) {
            // When
            series.setEndYear(newEndYear);

            // Then
            assertEquals(newEndYear, series.getEndYear(), "setEndYear should update the endYear property");
        }

        @Test
        @DisplayName("Given series with no overview when getOverview called then null is returned")
        void givenSeriesWithNoOverview_whenGetOverviewCalled_thenNullIsReturned() {
            // Given - series already has no overview set in setUp

            // When
            String actualOverview = series.getOverview();

            // Then
            assertNull(actualOverview, "getOverview should return null when not set");
        }

        @ParameterizedTest
        @ValueSource(strings = {"A long-running superhero series", "A limited series about zombies", "A flagship title"})
        @DisplayName("Given different overviews when setOverview called then overview is updated correctly")
        void givenDifferentOverviews_whenSetOverviewCalled_thenOverviewIsUpdatedCorrectly(String newOverview) {
            // When
            series.setOverview(newOverview);

            // Then
            assertEquals(newOverview, series.getOverview(), "setOverview should update the overview property");
        }

        @Test
        @DisplayName("Given series with no publisher when getPublisher called then null is returned")
        void givenSeriesWithNoPublisher_whenGetPublisherCalled_thenNullIsReturned() {
            // Given - series already has no publisher set in setUp

            // When
            Publisher actualPublisher = series.getPublisher();

            // Then
            assertNull(actualPublisher, "getPublisher should return null when not set");
        }

        @Test
        @DisplayName("Given publisher when setPublisher called then publisher is updated correctly")
        void givenPublisher_whenSetPublisherCalled_thenPublisherIsUpdatedCorrectly() {
            // Given
            Publisher publisher = new Publisher("Marvel Comics");

            // When
            series.setPublisher(publisher);

            // Then
            assertEquals(publisher, series.getPublisher(), "setPublisher should update the publisher property");
        }

        @Test
        @DisplayName("Given series when getIssues called then initialized issues collection is returned")
        void givenSeries_whenGetIssuesCalled_thenInitializedIssuesCollectionIsReturned() {
            // When
            Set<Issue> issues = series.getIssues();

            // Then
            assertNotNull(issues, "getIssues should not return null");
            assertTrue(issues.isEmpty(), "Initially, issues should be empty");
        }

        @Test
        @DisplayName("Given new issues collection when setIssues called then issues are updated correctly")
        void givenNewIssuesCollection_whenSetIssuesCalled_thenIssuesAreUpdatedCorrectly() {
            // Given
            Set<Issue> newIssues = new HashSet<>();
            Issue issue1 = new Issue();
            Issue issue2 = new Issue();
            newIssues.add(issue1);
            newIssues.add(issue2);

            // When
            series.setIssues(newIssues);

            // Then
            assertEquals(newIssues, series.getIssues(), "setIssues should update the issues collection");
            assertEquals(2, series.getIssues().size(), "Series should have 2 issues after setIssues");
        }
    }

    @Nested
    @DisplayName("Collection management tests")
    class CollectionManagementTests {

        @Test
        @DisplayName("Given series with issue when relationship is established then it's reflected in collections")
        void givenSeriesWithIssue_whenRelationshipEstablished_thenReflectedInCollections() {
            // Given
            Series series = new Series("X-Men", 1963);
            Issue issue = new Issue();
            issue.setSeries(series);

            // This would normally be done by the JPA provider
            try {
                java.lang.reflect.Field field = Series.class.getDeclaredField("issues");
                field.setAccessible(true);
                @SuppressWarnings("unchecked") Set<Issue> issues_set = (Set<Issue>) field.get(series);
                issues_set.add(issue);
            } catch (Exception e) {
                fail("Failed to set up test relationship: " + e.getMessage());
            }

            // When
            Set<Issue> issues = series.getIssues();

            // Then
            assertFalse(issues.isEmpty(), "issues should not be empty after relationship established");
            assertEquals(1, issues.size(), "issues should contain exactly one issue");
            assertTrue(issues.contains(issue), "issues should contain the added issue");
        }
    }

    @Nested
    @DisplayName("toString method tests")
    class ToStringTests {

        @Test
        @DisplayName("Given series with title and startYear when toString called then correct format is returned for ongoing series")
        void givenSeriesWithTitleAndStartYear_whenToStringCalled_thenCorrectFormatReturnedForOngoingSeries() {
            // Given - series already has title and startYear set in setUp
            // endYear is null by default, indicating an ongoing series

            // When
            String result = series.toString();

            // Then
            String expected = VALID_TITLE + " (" + VALID_START_YEAR + " - Present)";
            assertEquals(expected, result, "toString should return title with start year to present for ongoing series");
        }

        @Test
        @DisplayName("Given series with title, startYear and endYear when toString called then correct format is returned for finished series")
        void givenSeriesWithTitleStartYearAndEndYear_whenToStringCalled_thenCorrectFormatReturnedForFinishedSeries() {
            // Given
            Integer endYear = 2011;
            series.setEndYear(endYear);

            // When
            String result = series.toString();

            // Then
            String expected = VALID_TITLE + " (" + VALID_START_YEAR + " - " + endYear + ")";
            assertEquals(expected, result, "toString should return title with start and end years for finished series");
        }

        @Test
        @DisplayName("Given series with title, startYear equal to endYear when toString called then correct format with single year is returned")
        void givenSeriesWithTitleStartYearEqualToEndYear_whenToStringCalled_thenCorrectFormatWithSingleYearReturned() {
            // Given
            series.setEndYear(VALID_START_YEAR);  // Same as start year

            // When
            String result = series.toString();

            // Then
            String expected = VALID_TITLE + " (" + VALID_START_YEAR + ")";
            assertEquals(expected, result, "toString should return title with single year when start and end are the same");
        }

        @Test
        @DisplayName("Given series with null title when toString called then Untitled is shown")
        void givenSeriesWithNullTitle_whenToStringCalled_thenUntitledIsShown() {
            // Given
            Series nullTitleSeries = new Series();
            nullTitleSeries.setStartYear(2020);

            // When
            String result = nullTitleSeries.toString();

            // Then
            String expected = "Untitled (2020 - Present)";
            assertEquals(expected, result, "toString should use 'Untitled' when title is null");
        }
    }

    @Nested
    @DisplayName("equals and hashCode tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Given two series with same ID when equals called then true is returned")
        void givenTwoSeriesWithSameId_whenEqualsCalled_thenTrueIsReturned() {
            // Given
            Series series1 = new Series("X-Men", 1963);
            Series series2 = new Series("Avengers", 1963);

            // Set same ID on both series using reflection
            try {
                java.lang.reflect.Field idField = Series.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(series1, 1L);
                idField.set(series2, 1L);
            } catch (Exception e) {
                fail("Failed to set up test: " + e.getMessage());
            }

            // When & Then
            assertEquals(series1, series2, "Series with same ID should be equal");
            assertEquals(series1.hashCode(), series2.hashCode(), "Series with same ID should have same hash code");
        }

        @Test
        @DisplayName("Given two series with different IDs when equals called then false is returned")
        void givenTwoSeriesWithDifferentIds_whenEqualsCalled_thenFalseIsReturned() {
            // Given
            Series series1 = new Series("X-Men", 1963);
            Series series2 = new Series("Avengers", 1963);

            // Set different IDs on both series using reflection
            try {
                java.lang.reflect.Field idField = Series.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(series1, 1L);
                idField.set(series2, 2L);
            } catch (Exception e) {
                fail("Failed to set up test: " + e.getMessage());
            }

            // When & Then
            assertNotEquals(series1, series2, "Series with different IDs should not be equal");
            assertNotEquals(series1.hashCode(), series2.hashCode(), "Series with different IDs should have different hash codes");
        }

        @Test
        @DisplayName("Given series and null when equals called then false is returned")
        void givenSeriesAndNull_whenEqualsCalled_thenFalseIsReturned() {
            // When & Then
            assertNotEquals(null, series, "Series should not be equal to null");
        }

        @Test
        @DisplayName("Given series compared to itself when equals called then true is returned")
        void givenSeriesComparedToItself_whenEqualsCalled_thenTrueIsReturned() {
            // When & Then
            assertEquals(series, series, "Series should be equal to itself");
        }
    }
}
