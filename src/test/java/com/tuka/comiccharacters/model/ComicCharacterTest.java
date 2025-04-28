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

class ComicCharacterTest {

    private static final String VALID_NAME = "Spider-Man";
    private static final String VALID_ALIAS = "Peter Parker";
    private static final String VALID_OVERVIEW = "Bitten by a radioactive spider, Peter Parker gained superhuman abilities.";
    private static final String VALID_IMAGE_URL = "https://example.com/spider-man.jpg";
    private static final Publisher VALID_PUBLISHER = new Publisher("Marvel Comics");
    private ComicCharacter character;

    @BeforeEach
    void setUp() {
        character = new ComicCharacter(VALID_NAME, VALID_ALIAS, VALID_PUBLISHER, VALID_OVERVIEW, VALID_IMAGE_URL);
    }

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("Given valid name, alias, publisher and overview when constructor called then character is created with those properties")
        void givenValidNameAliasPublisherAndOverview_whenConstructorCalled_thenCharacterIsCreatedWithThoseProperties() {
            // Given
            String name = "Batman";
            String alias = "Bruce Wayne";
            Publisher publisher = new Publisher("DC Comics");
            String overview = "Billionaire vigilante fighting crime in Gotham City";

            // When
            ComicCharacter character = new ComicCharacter(name, alias, publisher, overview);

            // Then
            assertEquals(name, character.getName(), "Character name should match provided name");
            assertEquals(alias, character.getAlias(), "Character alias should match provided alias");
            assertEquals(publisher, character.getPublisher(), "Character publisher should match provided publisher");
            assertEquals(overview, character.getOverview(), "Character overview should match provided overview");
            assertNull(character.getImageUrl(), "Character image URL should be null");
            assertNotNull(character.getCreators(), "Character creators should be initialized");
            assertTrue(character.getCreators().isEmpty(), "Character creators should be empty");
            assertNotNull(character.getIssues(), "Character issues should be initialized");
            assertTrue(character.getIssues().isEmpty(), "Character issues should be empty");
            assertNull(character.getFirstAppearance(), "Character first appearance should be null");
        }

        @Test
        @DisplayName("Given all parameters when full constructor called then character is created with all properties")
        void givenAllParameters_whenFullConstructorCalled_thenCharacterIsCreatedWithAllProperties() {
            // Given
            String name = "Superman";
            String alias = "Clark Kent";
            Publisher publisher = new Publisher("DC Comics");
            String overview = "Last son of Krypton with superhuman abilities";
            String imageUrl = "https://example.com/superman.jpg";

            // When
            ComicCharacter character = new ComicCharacter(name, alias, publisher, overview, imageUrl);

            // Then
            assertEquals(name, character.getName(), "Character name should match provided name");
            assertEquals(alias, character.getAlias(), "Character alias should match provided alias");
            assertEquals(publisher, character.getPublisher(), "Character publisher should match provided publisher");
            assertEquals(overview, character.getOverview(), "Character overview should match provided overview");
            assertEquals(imageUrl, character.getImageUrl(), "Character image URL should match provided image URL");
            assertNotNull(character.getCreators(), "Character creators should be initialized");
            assertTrue(character.getCreators().isEmpty(), "Character creators should be empty");
            assertNotNull(character.getIssues(), "Character issues should be initialized");
            assertTrue(character.getIssues().isEmpty(), "Character issues should be empty");
            assertNull(character.getFirstAppearance(), "Character first appearance should be null");
        }

        @Test
        @DisplayName("Given no parameters when default constructor called then character is created with null properties")
        void givenNoParameters_whenDefaultConstructorCalled_thenCharacterIsCreatedWithNullProperties() {
            // When
            ComicCharacter character = new ComicCharacter();

            // Then
            assertNull(character.getName(), "Character name should be null");
            assertNull(character.getAlias(), "Character alias should be null");
            assertNull(character.getPublisher(), "Character publisher should be null");
            assertNull(character.getOverview(), "Character overview should be null");
            assertNull(character.getImageUrl(), "Character image URL should be null");
            assertNotNull(character.getCreators(), "Character creators should be initialized");
            assertTrue(character.getCreators().isEmpty(), "Character creators should be empty");
            assertNotNull(character.getIssues(), "Character issues should be initialized");
            assertTrue(character.getIssues().isEmpty(), "Character issues should be empty");
            assertNull(character.getFirstAppearance(), "Character first appearance should be null");
        }
    }

    @Nested
    @DisplayName("Getter and setter tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Given character with ID set implicitly when getId called then correct ID is returned")
        void givenCharacterWithIdSet_whenGetIdCalled_thenCorrectIdIsReturned() {
            // Given
            Long expectedId = 42L;
            try {
                java.lang.reflect.Field idField = ComicCharacter.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(character, expectedId);
            } catch (Exception e) {
                fail("Failed to set up test: " + e.getMessage());
            }

            // When
            Long actualId = character.getId();

            // Then
            assertEquals(expectedId, actualId, "getId should return the set ID");
        }

        @Test
        @DisplayName("Given character with name set when getName called then correct name is returned")
        void givenCharacterWithNameSet_whenGetNameCalled_thenCorrectNameIsReturned() {
            // Given - character already has name set in setUp

            // When
            String actualName = character.getName();

            // Then
            assertEquals(VALID_NAME, actualName, "getName should return the set name");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Iron Man", "Captain America", "Hulk"})
        @DisplayName("Given different names when setName called then name is updated correctly")
        void givenDifferentNames_whenSetNameCalled_thenNameIsUpdatedCorrectly(String newName) {
            // When
            character.setName(newName);

            // Then
            assertEquals(newName, character.getName(), "setName should update the name property");
        }

        @Test
        @DisplayName("Given character with alias set when getAlias called then correct alias is returned")
        void givenCharacterWithAliasSet_whenGetAliasCalled_thenCorrectAliasIsReturned() {
            // Given - character already has alias set in setUp

            // When
            String actualAlias = character.getAlias();

            // Then
            assertEquals(VALID_ALIAS, actualAlias, "getAlias should return the set alias");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Tony Stark", "Steve Rogers", "Bruce Banner"})
        @DisplayName("Given different aliases when setAlias called then alias is updated correctly")
        void givenDifferentAliases_whenSetAliasCalled_thenAliasIsUpdatedCorrectly(String newAlias) {
            // When
            character.setAlias(newAlias);

            // Then
            assertEquals(newAlias, character.getAlias(), "setAlias should update the alias property");
        }

        @Test
        @DisplayName("Given character with publisher set when getPublisher called then correct publisher is returned")
        void givenCharacterWithPublisherSet_whenGetPublisherCalled_thenCorrectPublisherIsReturned() {
            // Given - character already has publisher set in setUp

            // When
            Publisher actualPublisher = character.getPublisher();

            // Then
            assertEquals(VALID_PUBLISHER, actualPublisher, "getPublisher should return the set publisher");
        }

        @Test
        @DisplayName("Given different publisher when setPublisher called then publisher is updated correctly")
        void givenDifferentPublisher_whenSetPublisherCalled_thenPublisherIsUpdatedCorrectly() {
            // Given
            Publisher newPublisher = new Publisher("DC Comics");

            // When
            character.setPublisher(newPublisher);

            // Then
            assertEquals(newPublisher, character.getPublisher(), "setPublisher should update the publisher property");
        }

        @Test
        @DisplayName("Given character with overview set when getOverview called then correct overview is returned")
        void givenCharacterWithOverviewSet_whenGetOverviewCalled_thenCorrectOverviewIsReturned() {
            // Given - character already has overview set in setUp

            // When
            String actualOverview = character.getOverview();

            // Then
            assertEquals(VALID_OVERVIEW, actualOverview, "getOverview should return the set overview");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Updated character backstory", "A longer description of the character", ""})
        @DisplayName("Given different overviews when setOverview called then overview is updated correctly")
        void givenDifferentOverviews_whenSetOverviewCalled_thenOverviewIsUpdatedCorrectly(String newOverview) {
            // When
            character.setOverview(newOverview);

            // Then
            assertEquals(newOverview, character.getOverview(), "setOverview should update the overview property");
        }

        @Test
        @DisplayName("Given character with imageUrl set when getImageUrl called then correct imageUrl is returned")
        void givenCharacterWithImageUrlSet_whenGetImageUrlCalled_thenCorrectImageUrlIsReturned() {
            // Given - character already has imageUrl set in setUp

            // When
            String actualImageUrl = character.getImageUrl();

            // Then
            assertEquals(VALID_IMAGE_URL, actualImageUrl, "getImageUrl should return the set imageUrl");
        }

        @ParameterizedTest
        @ValueSource(strings = {"https://example.com/new-image.jpg", "https://cdn.comics.com/characters/12345.png", ""})
        @DisplayName("Given different imageUrls when setImageUrl called then imageUrl is updated correctly")
        void givenDifferentImageUrls_whenSetImageUrlCalled_thenImageUrlIsUpdatedCorrectly(String newImageUrl) {
            // When
            character.setImageUrl(newImageUrl);

            // Then
            assertEquals(newImageUrl, character.getImageUrl(), "setImageUrl should update the imageUrl property");
        }

        @Test
        @DisplayName("Given character with creators set when getCreators called then correct creators are returned")
        void givenCharacterWithCreatorsSet_whenGetCreatorsCalled_thenCorrectCreatorsAreReturned() {
            // Given
            Set<Creator> creators = new HashSet<>();
            Creator creator1 = new Creator("Stan Lee");
            Creator creator2 = new Creator("Steve Ditko");
            creators.add(creator1);
            creators.add(creator2);
            character.setCreators(creators);

            // When
            Set<Creator> actualCreators = character.getCreators();

            // Then
            assertEquals(creators, actualCreators, "getCreators should return the set creators");
            assertEquals(2, actualCreators.size(), "getCreators should return the correct number of creators");
            assertTrue(actualCreators.contains(creator1), "getCreators should contain creator1");
            assertTrue(actualCreators.contains(creator2), "getCreators should contain creator2");
        }

        @Test
        @DisplayName("Given character when getIssues called then initialized issues collection is returned")
        void givenCharacter_whenGetIssuesCalled_thenInitializedIssuesCollectionIsReturned() {
            // When
            Set<Issue> issues = character.getIssues();

            // Then
            assertNotNull(issues, "getIssues should not return null");
            assertTrue(issues.isEmpty(), "Initially, issues should be empty");
        }
    }

    @Nested
    @DisplayName("Collection management tests")
    class CollectionManagementTests {

        @Test
        @DisplayName("Given character with issue when relationship is established then it's reflected in collections")
        void givenCharacterWithIssue_whenRelationshipEstablished_thenReflectedInCollections() {
            // Given
            ComicCharacter character = new ComicCharacter("Wolverine", "Logan", VALID_PUBLISHER, "Mutant with healing factor");
            Issue issue = new Issue();
            Series series = new Series("X-Men", 1963);
            issue.setSeries(series);

            // Establish the bidirectional relationship
            issue.addCharacter(character);

            // When
            Set<Issue> characterIssues = character.getIssues();

            // Then
            assertFalse(characterIssues.isEmpty(), "character issues should not be empty after relationship established");
            assertEquals(1, characterIssues.size(), "character issues should contain exactly one issue");
            assertTrue(characterIssues.contains(issue), "character issues should contain the added issue");
        }

        @Test
        @DisplayName("Given character with creators when relationship is established then it's reflected in collections")
        void givenCharacterWithCreators_whenRelationshipEstablished_thenReflectedInCollections() {
            // Given
            ComicCharacter character = new ComicCharacter("Daredevil", "Matt Murdock", VALID_PUBLISHER, "Blind vigilante");
            Set<Creator> creators = new HashSet<>();
            Creator creator1 = new Creator("Frank Miller");
            Creator creator2 = new Creator("Brian Michael Bendis");
            creators.add(creator1);
            creators.add(creator2);

            // When
            character.setCreators(creators);
            Set<Creator> characterCreators = character.getCreators();

            // Then
            assertFalse(characterCreators.isEmpty(), "character creators should not be empty after relationship established");
            assertEquals(2, characterCreators.size(), "character creators should contain exactly two creators");
            assertTrue(characterCreators.contains(creator1), "character creators should contain creator1");
            assertTrue(characterCreators.contains(creator2), "character creators should contain creator2");
        }
    }

    @Nested
    @DisplayName("toString method tests")
    class ToStringTests {

        @Test
        @DisplayName("Given character with name and alias when toString called then name and alias are returned")
        void givenCharacterWithNameAndAlias_whenToStringCalled_thenNameAndAliasAreReturned() {
            // Given - character with name and alias already set up

            // When
            String result = character.toString();

            // Then
            String expected = VALID_NAME + " (" + VALID_ALIAS + ")";
            assertEquals(expected, result, "toString should return name and alias in parentheses");
        }

        @Test
        @DisplayName("Given character with name but no alias when toString called then only name is returned")
        void givenCharacterWithNameButNoAlias_whenToStringCalled_thenOnlyNameIsReturned() {
            // Given
            character.setAlias(null);

            // When
            String result = character.toString();

            // Then
            assertEquals(VALID_NAME, result, "toString should return only the name when alias is null");
        }

        @Test
        @DisplayName("Given character with name and empty alias when toString called then only name is returned")
        void givenCharacterWithNameAndEmptyAlias_whenToStringCalled_thenOnlyNameIsReturned() {
            // Given
            character.setAlias("");

            // When
            String result = character.toString();

            // Then
            assertEquals(VALID_NAME, result, "toString should return only the name when alias is empty");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Thor", "Black Widow", "Hawkeye"})
        @DisplayName("Given character with different names when toString called then correct name is returned")
        void givenCharacterWithDifferentNames_whenToStringCalled_thenCorrectNameIsReturned(String name) {
            // Given
            character.setName(name);
            character.setAlias(null);  // Set alias to null to only test name

            // When
            String result = character.toString();

            // Then
            assertEquals(name, result, "toString should return the updated character name");
        }
    }
}
