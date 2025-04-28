package com.tuka.comiccharacters.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IssueTest {

    private static final Series VALID_SERIES = new Series("X-Men", 1963);
    private static final BigDecimal VALID_ISSUE_NUMBER = new BigDecimal("1.0");
    private static final String VALID_OVERVIEW = "First appearance of the X-Men team";
    private static final String VALID_IMAGE_URL = "https://example.com/x-men-1.jpg";
    private static final LocalDate VALID_RELEASE_DATE = LocalDate.of(1963, 9, 10);
    private static final BigDecimal VALID_PRICE = new BigDecimal("0.12");

    private Issue issue;

    @BeforeEach
    void setUp() {
        issue = new Issue(VALID_SERIES, VALID_ISSUE_NUMBER);
        issue.setOverview(VALID_OVERVIEW);
        issue.setImageUrl(VALID_IMAGE_URL);
        issue.setReleaseDate(VALID_RELEASE_DATE);
        issue.setPriceUsd(VALID_PRICE);
    }

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("Given valid series and issueNumber when constructor called then issue is created with those properties")
        void givenValidSeriesAndIssueNumber_whenConstructorCalled_thenIssueIsCreatedWithThoseProperties() {
            // Given
            Series series = new Series("Fantastic Four", 1961);
            BigDecimal issueNumber = new BigDecimal("5.0");

            // When
            Issue issue = new Issue(series, issueNumber);

            // Then
            assertEquals(series, issue.getSeries(), "Issue series should match provided series");
            assertEquals(issueNumber, issue.getIssueNumber(), "Issue number should match provided issue number");
            assertNull(issue.getOverview(), "Overview should be null");
            assertNull(issue.getImageUrl(), "Image URL should be null");
            assertNull(issue.getReleaseDate(), "Release date should be null");
            assertNull(issue.getPriceUsd(), "Price should be null");
            assertNotNull(issue.getIssueCreators(), "Issue creators should be initialized");
            assertTrue(issue.getIssueCreators().isEmpty(), "Issue creators should be empty");
            assertNotNull(issue.getCharacters(), "Characters should be initialized");
            assertTrue(issue.getCharacters().isEmpty(), "Characters should be empty");
            assertFalse(issue.getAnnual(), "isAnnual should be false by default");
        }


        @Test
        @DisplayName("Given no parameters when default constructor called then issue is created with null properties")
        void givenNoParameters_whenDefaultConstructorCalled_thenIssueIsCreatedWithNullProperties() {
            // When
            Issue issue = new Issue();

            // Then
            assertNull(issue.getSeries(), "Series should be null");
            assertNull(issue.getIssueNumber(), "Issue number should be null");
            assertNull(issue.getOverview(), "Overview should be null");
            assertNull(issue.getImageUrl(), "Image URL should be null");
            assertNull(issue.getReleaseDate(), "Release date should be null");
            assertNull(issue.getPriceUsd(), "Price should be null");
            assertNotNull(issue.getIssueCreators(), "Issue creators should be initialized");
            assertTrue(issue.getIssueCreators().isEmpty(), "Issue creators should be empty");
            assertNotNull(issue.getCharacters(), "Characters should be initialized");
            assertTrue(issue.getCharacters().isEmpty(), "Characters should be empty");
            assertFalse(issue.getAnnual(), "isAnnual should be false by default");
        }
    }

    @Nested
    @DisplayName("Getter and setter tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Given issue with ID set implicitly when getId called then correct ID is returned")
        void givenIssueWithIdSet_whenGetIdCalled_thenCorrectIdIsReturned() {
            // Given
            Long expectedId = 42L;
            try {
                java.lang.reflect.Field idField = Issue.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(issue, expectedId);
            } catch (Exception e) {
                fail("Failed to set up test: " + e.getMessage());
            }

            // When
            Long actualId = issue.getId();

            // Then
            assertEquals(expectedId, actualId, "getId should return the set ID");
        }

        @Test
        @DisplayName("Given issue with series set when getSeries called then correct series is returned")
        void givenIssueWithSeriesSet_whenGetSeriesCalled_thenCorrectSeriesIsReturned() {
            // Given - issue already has series set in setUp

            // When
            Series actualSeries = issue.getSeries();

            // Then
            assertEquals(VALID_SERIES, actualSeries, "getSeries should return the set series");
        }

        @Test
        @DisplayName("Given different series when setSeries called then series is updated correctly")
        void givenDifferentSeries_whenSetSeriesCalled_thenSeriesIsUpdatedCorrectly() {
            // Given
            Series newSeries = new Series("Daredevil", 1964);

            // When
            issue.setSeries(newSeries);

            // Then
            assertEquals(newSeries, issue.getSeries(), "setSeries should update the series property");
        }

        @Test
        @DisplayName("Given issue with issueNumber set when getIssueNumber called then correct issueNumber is returned")
        void givenIssueWithIssueNumberSet_whenGetIssueNumberCalled_thenCorrectIssueNumberIsReturned() {
            // Given - issue already has issueNumber set in setUp

            // When
            BigDecimal actualIssueNumber = issue.getIssueNumber();

            // Then
            assertEquals(VALID_ISSUE_NUMBER, actualIssueNumber, "getIssueNumber should return the set issueNumber");
        }

        @ParameterizedTest
        @ValueSource(strings = {"2.0", "3.5", "300.0"})
        @DisplayName("Given different issueNumbers when setIssueNumber called then issueNumber is updated correctly")
        void givenDifferentIssueNumbers_whenSetIssueNumberCalled_thenIssueNumberIsUpdatedCorrectly(String newIssueNumberStr) {
            // Given
            BigDecimal newIssueNumber = new BigDecimal(newIssueNumberStr);

            // When
            issue.setIssueNumber(newIssueNumber);

            // Then
            assertEquals(newIssueNumber, issue.getIssueNumber(), "setIssueNumber should update the issueNumber property");
        }

        @Test
        @DisplayName("Given issue with overview set when getOverview called then correct overview is returned")
        void givenIssueWithOverviewSet_whenGetOverviewCalled_thenCorrectOverviewIsReturned() {
            // Given - issue already has overview set in setUp

            // When
            String actualOverview = issue.getOverview();

            // Then
            assertEquals(VALID_OVERVIEW, actualOverview, "getOverview should return the set overview");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Updated issue description", "A longer summary of the issue's plot", ""})
        @DisplayName("Given different overviews when setOverview called then overview is updated correctly")
        void givenDifferentOverviews_whenSetOverviewCalled_thenOverviewIsUpdatedCorrectly(String newOverview) {
            // When
            issue.setOverview(newOverview);

            // Then
            assertEquals(newOverview, issue.getOverview(), "setOverview should update the overview property");
        }

        @Test
        @DisplayName("Given issue with imageUrl set when getImageUrl called then correct imageUrl is returned")
        void givenIssueWithImageUrlSet_whenGetImageUrlCalled_thenCorrectImageUrlIsReturned() {
            // Given - issue already has imageUrl set in setUp

            // When
            String actualImageUrl = issue.getImageUrl();

            // Then
            assertEquals(VALID_IMAGE_URL, actualImageUrl, "getImageUrl should return the set imageUrl");
        }

        @ParameterizedTest
        @ValueSource(strings = {"https://example.com/new-cover.jpg", "https://cdn.comics.com/issues/12345.png", ""})
        @DisplayName("Given different imageUrls when setImageUrl called then imageUrl is updated correctly")
        void givenDifferentImageUrls_whenSetImageUrlCalled_thenImageUrlIsUpdatedCorrectly(String newImageUrl) {
            // When
            issue.setImageUrl(newImageUrl);

            // Then
            assertEquals(newImageUrl, issue.getImageUrl(), "setImageUrl should update the imageUrl property");
        }

        @Test
        @DisplayName("Given issue with releaseDate set when getReleaseDate called then correct releaseDate is returned")
        void givenIssueWithReleaseDateSet_whenGetReleaseDateCalled_thenCorrectReleaseDateIsReturned() {
            // Given - issue already has releaseDate set in setUp

            // When
            LocalDate actualReleaseDate = issue.getReleaseDate();

            // Then
            assertEquals(VALID_RELEASE_DATE, actualReleaseDate, "getReleaseDate should return the set releaseDate");
        }

        @Test
        @DisplayName("Given different releaseDate when setReleaseDate called then releaseDate is updated correctly")
        void givenDifferentReleaseDate_whenSetReleaseDateCalled_thenReleaseDateIsUpdatedCorrectly() {
            // Given
            LocalDate newReleaseDate = LocalDate.of(1975, 5, 15);

            // When
            issue.setReleaseDate(newReleaseDate);

            // Then
            assertEquals(newReleaseDate, issue.getReleaseDate(), "setReleaseDate should update the releaseDate property");
        }

        @Test
        @DisplayName("Given issue with priceUsd set when getPriceUsd called then correct priceUsd is returned")
        void givenIssueWithPriceUsdSet_whenGetPriceUsdCalled_thenCorrectPriceUsdIsReturned() {
            // Given - issue already has priceUsd set in setUp

            // When
            BigDecimal actualPriceUsd = issue.getPriceUsd();

            // Then
            assertEquals(VALID_PRICE, actualPriceUsd, "getPriceUsd should return the set priceUsd");
        }

        @ParameterizedTest
        @ValueSource(strings = {"0.25", "1.50", "4.99"})
        @DisplayName("Given different priceUsds when setPriceUsd called then priceUsd is updated correctly")
        void givenDifferentPriceUsds_whenSetPriceUsdCalled_thenPriceUsdIsUpdatedCorrectly(String newPriceUsdStr) {
            // Given
            BigDecimal newPriceUsd = new BigDecimal(newPriceUsdStr);

            // When
            issue.setPriceUsd(newPriceUsd);

            // Then
            assertEquals(newPriceUsd, issue.getPriceUsd(), "setPriceUsd should update the priceUsd property");
        }

        @Test
        @DisplayName("Given issue when getIssueCreators called then initialized issueCreators collection is returned")
        void givenIssue_whenGetIssueCreatorsCalled_thenInitializedIssueCreatorsCollectionIsReturned() {
            // When
            Set<IssueCreator> issueCreators = issue.getIssueCreators();

            // Then
            assertNotNull(issueCreators, "getIssueCreators should not return null");
            assertTrue(issueCreators.isEmpty(), "Initially, issueCreators should be empty");
        }

        @Test
        @DisplayName("Given issue when getCharacters called then initialized characters collection is returned")
        void givenIssue_whenGetCharactersCalled_thenInitializedCharactersCollectionIsReturned() {
            // When
            Set<ComicCharacter> characters = issue.getCharacters();

            // Then
            assertNotNull(characters, "getCharacters should not return null");
            assertTrue(characters.isEmpty(), "Initially, characters should be empty");
        }

        @Test
        @DisplayName("Given new characters collection when setCharacters called then characters are updated correctly")
        void givenNewCharactersCollection_whenSetCharactersCalled_thenCharactersAreUpdatedCorrectly() {
            // Given
            Set<ComicCharacter> newCharacters = new HashSet<>();
            Publisher marvel = new Publisher("Marvel Comics");
            ComicCharacter character1 = new ComicCharacter("Wolverine", "Logan", marvel, "Mutant with healing factor");
            ComicCharacter character2 = new ComicCharacter("Cyclops", "Scott Summers", marvel, "Mutant with optic blasts");
            newCharacters.add(character1);
            newCharacters.add(character2);

            // When
            issue.setCharacters(newCharacters);

            // Then
            assertEquals(newCharacters, issue.getCharacters(), "setCharacters should update the characters collection");
            assertEquals(2, issue.getCharacters().size(), "Issue should have 2 characters after setCharacters");
        }

        @Test
        @DisplayName("Given issue with isAnnual set to default when getAnnual called then false is returned")
        void givenIssueWithIsAnnualSetToDefault_whenGetAnnualCalled_thenFalseIsReturned() {
            // Given - issue already has isAnnual set to false by default

            // When
            Boolean isAnnual = issue.getAnnual();

            // Then
            assertFalse(isAnnual, "getAnnual should return false by default");
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @DisplayName("Given different isAnnual values when setAnnual called then isAnnual is updated correctly")
        void givenDifferentIsAnnualValues_whenSetAnnualCalled_thenIsAnnualIsUpdatedCorrectly(Boolean newIsAnnual) {
            // When
            issue.setAnnual(newIsAnnual);

            // Then
            assertEquals(newIsAnnual, issue.getAnnual(), "setAnnual should update the isAnnual property");
        }
    }

    @Nested
    @DisplayName("Collection management tests")
    class CollectionManagementTests {

        @Test
        @DisplayName("Given issue and character when addCharacter called then character is added to issue and issue to character")
        void givenIssueAndCharacter_whenAddCharacterCalled_thenCharacterIsAddedToIssueAndIssueToCharacter() {
            // Given
            Publisher marvel = new Publisher("Marvel Comics");
            ComicCharacter character = new ComicCharacter("Cyclops", "Scott Summers", marvel, "Mutant with optic blasts");

            // When
            issue.addCharacter(character);

            // Then
            assertTrue(issue.getCharacters().contains(character), "Issue characters should contain the added character");
            assertTrue(character.getIssues().contains(issue), "Character issues should contain the issue");
        }
    }

    @Nested
    @DisplayName("toString method tests")
    class ToStringTests {

        @Test
        @DisplayName("Given regular issue with series and issueNumber when toString called then correct format is returned")
        void givenRegularIssueWithSeriesAndIssueNumber_whenToStringCalled_thenCorrectFormatIsReturned() {
            // Given - issue already has series and issueNumber set in setUp

            // When
            String result = issue.toString();

            // Then
            String expected = VALID_SERIES + " #" + VALID_ISSUE_NUMBER.stripTrailingZeros();
            assertEquals(expected, result, "toString should return series name followed by issue number");
        }

        @Test
        @DisplayName("Given annual issue when toString called then correct format with Annual is returned")
        void givenAnnualIssue_whenToStringCalled_thenCorrectFormatWithAnnualIsReturned() {
            // Given
            issue.setAnnual(true);

            // When
            String result = issue.toString();

            // Then
            String expected = VALID_SERIES + " Annual #" + VALID_ISSUE_NUMBER.stripTrailingZeros();
            assertEquals(expected, result, "toString should include 'Annual' for annual issues");
        }

        @Test
        @DisplayName("Given issue with non-integer issue number when toString called then decimal places are removed if zeros")
        void givenIssueWithNonIntegerIssueNumber_whenToStringCalled_thenDecimalPlacesAreRemovedIfZeros() {
            // Given
            BigDecimal issueNumber = new BigDecimal("42.00");
            issue.setIssueNumber(issueNumber);

            // When
            String result = issue.toString();

            // Then
            String expected = VALID_SERIES + " #42";
            assertEquals(expected, result, "toString should remove trailing zeros from issue number");
        }

        @Test
        @DisplayName("Given issue with null issue number when toString called then empty string is used for number")
        void givenIssueWithNullIssueNumber_whenToStringCalled_thenEmptyStringIsUsedForNumber() {
            // Given
            issue.setIssueNumber(null);

            // When
            String result = issue.toString();

            // Then
            String expected = VALID_SERIES + " #";
            assertEquals(expected, result, "toString should use empty string for null issue number");
        }
    }
}
