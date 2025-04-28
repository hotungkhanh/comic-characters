package com.tuka.comiccharacters.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CreatorTest {

    private static final String VALID_NAME = "Stan Lee";
    private static final String VALID_OVERVIEW = "Comic book writer, editor, publisher, and producer";
    private static final String VALID_IMAGE_URL = "https://example.com/stan-lee.jpg";
    private Creator creator;

    @BeforeEach
    void setUp() {
        creator = new Creator(VALID_NAME, VALID_OVERVIEW, VALID_IMAGE_URL);
    }

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("Given valid name when constructor called with name only then creator is created with name only")
        void givenValidName_whenConstructorCalledWithNameOnly_thenCreatorIsCreatedWithNameOnly() {
            // Given
            String name = "Jack Kirby";

            // When
            Creator creator = new Creator(name);

            // Then
            assertEquals(name, creator.getName(), "Creator name should match provided name");
            assertNull(creator.getOverview(), "Overview should be null");
            assertNull(creator.getImageUrl(), "Image URL should be null");
            assertTrue(creator.getIssueCreators().isEmpty(), "Issue creators should be empty");
            assertTrue(creator.getCreditedCharacters().isEmpty(), "Credited characters should be empty");
        }

        @Test
        @DisplayName("Given valid name and overview when constructor called then creator is created with name and overview")
        void givenValidNameAndOverview_whenConstructorCalled_thenCreatorIsCreatedWithNameAndOverview() {
            // Given
            String name = "Frank Miller";
            String overview = "Comic book writer, penciller and inker";

            // When
            Creator creator = new Creator(name, overview);

            // Then
            assertEquals(name, creator.getName(), "Creator name should match provided name");
            assertEquals(overview, creator.getOverview(), "Creator overview should match provided overview");
            assertNull(creator.getImageUrl(), "Image URL should be null");
            assertTrue(creator.getIssueCreators().isEmpty(), "Issue creators should be empty");
            assertTrue(creator.getCreditedCharacters().isEmpty(), "Credited characters should be empty");
        }

        @Test
        @DisplayName("Given valid name, overview and imageUrl when constructor called then creator is created with all properties")
        void givenValidNameOverviewAndImageUrl_whenConstructorCalled_thenCreatorIsCreatedWithAllProperties() {
            // Given
            String name = "Alan Moore";
            String overview = "Comic book writer known for Watchmen and V for Vendetta";
            String imageUrl = "https://example.com/alan-moore.jpg";

            // When
            Creator creator = new Creator(name, overview, imageUrl);

            // Then
            assertEquals(name, creator.getName(), "Creator name should match provided name");
            assertEquals(overview, creator.getOverview(), "Creator overview should match provided overview");
            assertEquals(imageUrl, creator.getImageUrl(), "Creator image URL should match provided image URL");
            assertTrue(creator.getIssueCreators().isEmpty(), "Issue creators should be empty");
            assertTrue(creator.getCreditedCharacters().isEmpty(), "Credited characters should be empty");
        }

        @Test
        @DisplayName("Given no parameters when default constructor called then creator is created with null properties")
        void givenNoParameters_whenDefaultConstructorCalled_thenCreatorIsCreatedWithNullProperties() {
            // When
            Creator creator = new Creator();

            // Then
            assertNull(creator.getName(), "Creator name should be null");
            assertNull(creator.getOverview(), "Creator overview should be null");
            assertNull(creator.getImageUrl(), "Creator image URL should be null");
            assertNotNull(creator.getIssueCreators(), "Issue creators should be initialized");
            assertNotNull(creator.getCreditedCharacters(), "Credited characters should be initialized");
            assertTrue(creator.getIssueCreators().isEmpty(), "Issue creators should be empty");
            assertTrue(creator.getCreditedCharacters().isEmpty(), "Credited characters should be empty");
        }
    }

    @Nested
    @DisplayName("Getter and setter tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Given creator with ID set when getId called then correct ID is returned")
        void givenCreatorWithIdSet_whenGetIdCalled_thenCorrectIdIsReturned() {
            // Given
            Long expectedId = 42L;
            creator.setId(expectedId);

            // When
            Long actualId = creator.getId();

            // Then
            assertEquals(expectedId, actualId, "getId should return the set ID");
        }

        @Test
        @DisplayName("Given creator with name set when getName called then correct name is returned")
        void givenCreatorWithNameSet_whenGetNameCalled_thenCorrectNameIsReturned() {
            // Given - creator already has name set in setUp

            // When
            String actualName = creator.getName();

            // Then
            assertEquals(VALID_NAME, actualName, "getName should return the set name");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Jim Lee", "Todd McFarlane", "Neil Gaiman"})
        @DisplayName("Given different names when setName called then name is updated correctly")
        void givenDifferentNames_whenSetNameCalled_thenNameIsUpdatedCorrectly(String newName) {
            // When
            creator.setName(newName);

            // Then
            assertEquals(newName, creator.getName(), "setName should update the name property");
        }

        @Test
        @DisplayName("Given creator with overview set when getOverview called then correct overview is returned")
        void givenCreatorWithOverviewSet_whenGetOverviewCalled_thenCorrectOverviewIsReturned() {
            // Given - creator already has overview set in setUp

            // When
            String actualOverview = creator.getOverview();

            // Then
            assertEquals(VALID_OVERVIEW, actualOverview, "getOverview should return the set overview");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Updated overview text", "A longer biography with more details", ""})
        @DisplayName("Given different overviews when setOverview called then overview is updated correctly")
        void givenDifferentOverviews_whenSetOverviewCalled_thenOverviewIsUpdatedCorrectly(String newOverview) {
            // When
            creator.setOverview(newOverview);

            // Then
            assertEquals(newOverview, creator.getOverview(), "setOverview should update the overview property");
        }

        @Test
        @DisplayName("Given creator with imageUrl set when getImageUrl called then correct imageUrl is returned")
        void givenCreatorWithImageUrlSet_whenGetImageUrlCalled_thenCorrectImageUrlIsReturned() {
            // Given - creator already has imageUrl set in setUp

            // When
            String actualImageUrl = creator.getImageUrl();

            // Then
            assertEquals(VALID_IMAGE_URL, actualImageUrl, "getImageUrl should return the set imageUrl");
        }

        @ParameterizedTest
        @ValueSource(strings = {"https://example.com/new-image.jpg", "https://cdn.comics.com/creators/12345.png", ""})
        @DisplayName("Given different imageUrls when setImageUrl called then imageUrl is updated correctly")
        void givenDifferentImageUrls_whenSetImageUrlCalled_thenImageUrlIsUpdatedCorrectly(String newImageUrl) {
            // When
            creator.setImageUrl(newImageUrl);

            // Then
            assertEquals(newImageUrl, creator.getImageUrl(), "setImageUrl should update the imageUrl property");
        }
    }

    @Nested
    @DisplayName("toString method tests")
    class ToStringTests {

        @Test
        @DisplayName("Given creator with name when toString called then name is returned")
        void givenCreatorWithName_whenToStringCalled_thenNameIsReturned() {
            // Given - creator with name already set up

            // When
            String result = creator.toString();

            // Then
            assertEquals(VALID_NAME, result, "toString should return the creator's name");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Chris Claremont", "Brian Michael Bendis", "Grant Morrison"})
        @DisplayName("Given creator with different names when toString called then correct name is returned")
        void givenCreatorWithDifferentNames_whenToStringCalled_thenCorrectNameIsReturned(String name) {
            // Given
            creator.setName(name);

            // When
            String result = creator.toString();

            // Then
            assertEquals(name, result, "toString should return the updated creator name");
        }
    }
}
