package com.tuka.comiccharacters.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PublisherTest {

    private static final String VALID_NAME = "Marvel Comics";
    private Publisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new Publisher(VALID_NAME);
    }

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("Given valid name when constructor called with name only then publisher is created with name only")
        void givenValidName_whenConstructorCalledWithNameOnly_thenPublisherIsCreatedWithNameOnly() {
            // Given
            String name = "DC Comics";

            // When
            Publisher publisher = new Publisher(name);

            // Then
            assertEquals(name, publisher.getName(), "Publisher name should match provided name");
            assertNotNull(publisher.getPublisherSeries(), "Publisher series should be initialized");
            assertTrue(publisher.getPublisherSeries().isEmpty(), "Publisher series should be empty");
            assertNotNull(publisher.getPublisherCharacters(), "Publisher characters should be initialized");
            assertTrue(publisher.getPublisherCharacters().isEmpty(), "Publisher characters should be empty");
        }

        @Test
        @DisplayName("Given no parameters when default constructor called then publisher is created with null properties")
        void givenNoParameters_whenDefaultConstructorCalled_thenPublisherIsCreatedWithNullProperties() {
            // When
            Publisher publisher = new Publisher();

            // Then
            assertNull(publisher.getName(), "Publisher name should be null");
            assertNotNull(publisher.getPublisherSeries(), "Publisher series should be initialized");
            assertTrue(publisher.getPublisherSeries().isEmpty(), "Publisher series should be empty");
            assertNotNull(publisher.getPublisherCharacters(), "Publisher characters should be initialized");
            assertTrue(publisher.getPublisherCharacters().isEmpty(), "Publisher characters should be empty");
        }
    }

    @Nested
    @DisplayName("Getter and setter tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Given publisher with ID set implicitly when getId called then correct ID is returned")
        void givenPublisherWithIdSet_whenGetIdCalled_thenCorrectIdIsReturned() {
            // Note: In a real application, the ID would be set by the JPA provider
            // For testing purposes, we're using reflection to set a value

            // Given
            Long expectedId = 42L;
            try {
                java.lang.reflect.Field idField = Publisher.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(publisher, expectedId);
            } catch (Exception e) {
                fail("Failed to set up test: " + e.getMessage());
            }

            // When
            Long actualId = publisher.getId();

            // Then
            assertEquals(expectedId, actualId, "getId should return the set ID");
        }

        @Test
        @DisplayName("Given publisher with name set when getName called then correct name is returned")
        void givenPublisherWithNameSet_whenGetNameCalled_thenCorrectNameIsReturned() {
            // Given - publisher already has name set in setUp

            // When
            String actualName = publisher.getName();

            // Then
            assertEquals(VALID_NAME, actualName, "getName should return the set name");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Dark Horse Comics", "IDW Publishing", "Vertigo"})
        @DisplayName("Given different names when setName called then name is updated correctly")
        void givenDifferentNames_whenSetNameCalled_thenNameIsUpdatedCorrectly(String newName) {
            // When
            publisher.setName(newName);

            // Then
            assertEquals(newName, publisher.getName(), "setName should update the name property");
        }

        @Test
        @DisplayName("Given publisher when getPublisherSeries called then initialized series collection is returned")
        void givenPublisher_whenGetPublisherSeriesCalled_thenInitializedSeriesCollectionIsReturned() {
            // When
            Set<Series> publisherSeries = publisher.getPublisherSeries();

            // Then
            assertNotNull(publisherSeries, "getPublisherSeries should not return null");
            assertTrue(publisherSeries.isEmpty(), "Initially, publisher series should be empty");
        }

        @Test
        @DisplayName("Given publisher when getPublisherCharacters called then initialized characters collection is returned")
        void givenPublisher_whenGetPublisherCharactersCalled_thenInitializedCharactersCollectionIsReturned() {
            // When
            Set<ComicCharacter> publisherCharacters = publisher.getPublisherCharacters();

            // Then
            assertNotNull(publisherCharacters, "getPublisherCharacters should not return null");
            assertTrue(publisherCharacters.isEmpty(), "Initially, publisher characters should be empty");
        }
    }

    @Nested
    @DisplayName("Collection management tests")
    class CollectionManagementTests {

        @Test
        @DisplayName("Given publisher with series when relationship is established then it's reflected in collections")
        void givenPublisherWithSeries_whenRelationshipEstablished_thenReflectedInCollections() {
            // Given
            Publisher publisher = new Publisher("Marvel");
            Series series = new Series("X-Men", 1963);
            series.setPublisher(publisher);

            // This would normally be done by the JPA provider
            try {
                java.lang.reflect.Field field = Publisher.class.getDeclaredField("publisherSeries");
                field.setAccessible(true);
                @SuppressWarnings("unchecked") Set<Series> series_set = (Set<Series>) field.get(publisher);
                series_set.add(series);
            } catch (Exception e) {
                fail("Failed to set up test relationship: " + e.getMessage());
            }

            // When
            Set<Series> publisherSeries = publisher.getPublisherSeries();

            // Then
            assertFalse(publisherSeries.isEmpty(), "publisherSeries should not be empty after relationship established");
            assertEquals(1, publisherSeries.size(), "publisherSeries should contain exactly one series");
            assertTrue(publisherSeries.contains(series), "publisherSeries should contain the added series");
        }

        @Test
        @DisplayName("Given publisher with character when relationship is established then it's reflected in collections")
        void givenPublisherWithCharacter_whenRelationshipEstablished_thenReflectedInCollections() {
            // Given
            Publisher publisher = new Publisher("DC Comics");
            ComicCharacter character = new ComicCharacter("Superman", "Clark Kent", publisher, "The Man of Steel");

            // This would normally be done by the JPA provider
            try {
                java.lang.reflect.Field field = Publisher.class.getDeclaredField("publisherCharacters");
                field.setAccessible(true);
                @SuppressWarnings("unchecked") Set<ComicCharacter> character_set = (Set<ComicCharacter>) field.get(publisher);
                character_set.add(character);
            } catch (Exception e) {
                fail("Failed to set up test relationship: " + e.getMessage());
            }

            // When
            Set<ComicCharacter> publisherCharacters = publisher.getPublisherCharacters();

            // Then
            assertFalse(publisherCharacters.isEmpty(), "publisherCharacters should not be empty after relationship established");
            assertEquals(1, publisherCharacters.size(), "publisherCharacters should contain exactly one character");
            assertTrue(publisherCharacters.contains(character), "publisherCharacters should contain the added character");
        }
    }

    @Nested
    @DisplayName("toString method tests")
    class ToStringTests {

        @Test
        @DisplayName("Given publisher with name when toString called then name is returned")
        void givenPublisherWithName_whenToStringCalled_thenNameIsReturned() {
            // Given - publisher with name already set up

            // When
            String result = publisher.toString();

            // Then
            assertEquals(VALID_NAME, result, "toString should return the publisher's name");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Boom! Studios", "Valiant Comics", "Top Cow"})
        @DisplayName("Given publisher with different names when toString called then correct name is returned")
        void givenPublisherWithDifferentNames_whenToStringCalled_thenCorrectNameIsReturned(String name) {
            // Given
            publisher.setName(name);

            // When
            String result = publisher.toString();

            // Then
            assertEquals(name, result, "toString should return the updated publisher name");
        }
    }

    @Nested
    @DisplayName("equals and hashCode tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Given two publishers with same ID when equals called then true is returned")
        void givenTwoPublishersWithSameId_whenEqualsCalled_thenTrueIsReturned() {
            // Given
            Publisher publisher1 = new Publisher("Marvel");
            Publisher publisher2 = new Publisher("Marvel Clone");

            // Set same ID on both publishers using reflection
            try {
                java.lang.reflect.Field idField = Publisher.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(publisher1, 1L);
                idField.set(publisher2, 1L);
            } catch (Exception e) {
                fail("Failed to set up test: " + e.getMessage());
            }

            // When & Then
            assertEquals(publisher1, publisher2, "Publishers with same ID should be equal");
            assertEquals(publisher1.hashCode(), publisher2.hashCode(), "Publishers with same ID should have same hash code");
        }

        @Test
        @DisplayName("Given two publishers with different IDs when equals called then false is returned")
        void givenTwoPublishersWithDifferentIds_whenEqualsCalled_thenFalseIsReturned() {
            // Given
            Publisher publisher1 = new Publisher("Marvel");
            Publisher publisher2 = new Publisher("DC");

            // Set different IDs on both publishers using reflection
            try {
                java.lang.reflect.Field idField = Publisher.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(publisher1, 1L);
                idField.set(publisher2, 2L);
            } catch (Exception e) {
                fail("Failed to set up test: " + e.getMessage());
            }

            // When & Then
            assertNotEquals(publisher1, publisher2, "Publishers with different IDs should not be equal");
            assertNotEquals(publisher1.hashCode(), publisher2.hashCode(), "Publishers with different IDs should have different hash codes");
        }

        @Test
        @DisplayName("Given publisher and null when equals called then false is returned")
        void givenPublisherAndNull_whenEqualsCalled_thenFalseIsReturned() {
            // When & Then
            assertNotEquals(null, publisher, "Publisher should not be equal to null");
        }

        @Test
        @DisplayName("Given publisher compared to itself when equals called then true is returned")
        void givenPublisherComparedToItself_whenEqualsCalled_thenTrueIsReturned() {
            // When & Then
            assertEquals(publisher, publisher, "Publisher should be equal to itself");
        }
    }
}