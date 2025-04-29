package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Publisher;
import com.tuka.comiccharacters.model.Series;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublisherDaoImplTest {

    private final Long VALID_ID = 1L;
    @Mock
    private EntityManager entityManager;
    @Mock
    private TypedQuery<Publisher> typedQuery;
    private PublisherDaoImpl publisherDao;
    private Publisher testPublisher;

    @BeforeEach
    void setUp() {
        // Create a testable DAO that overrides getEntityManager
        publisherDao = new PublisherDaoImpl() {
            @Override
            protected EntityManager getEntityManager() {
                return entityManager;
            }
        };

        // Set up test data
        testPublisher = new Publisher("Marvel Comics");
        try {
            java.lang.reflect.Field idField = Publisher.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testPublisher, VALID_ID);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Given valid publisher with associations when delete called then associations are cleared and publisher is removed")
    void givenValidPublisherWithAssociations_whenDeleteCalled_thenAssociationsClearedAndPublisherRemoved() {
        // Given
        EntityTransaction transaction = mock(EntityTransaction.class);
        when(entityManager.getTransaction()).thenReturn(transaction);

        // Create mock character and series
        ComicCharacter character1 = mock(ComicCharacter.class);
        ComicCharacter character2 = mock(ComicCharacter.class);
        Series series1 = mock(Series.class);
        Series series2 = mock(Series.class);

        // Use real sets for easier verification
        Set<ComicCharacter> characters = new HashSet<>(Arrays.asList(character1, character2));
        Set<Series> series = new HashSet<>(Arrays.asList(series1, series2));

        // Create a managed copy of the publisher
        Publisher managedPublisher = spy(new Publisher("Marvel Comics"));
        try {
            java.lang.reflect.Field idField = Publisher.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(managedPublisher, VALID_ID);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }

        // Set associations
        managedPublisher.getPublisherCharacters().addAll(characters);
        managedPublisher.getPublisherSeries().addAll(series);

        // When entityManager.find(...) is called, return the managedPublisher
        when(entityManager.find(Publisher.class, VALID_ID)).thenReturn(managedPublisher);

        // When
        publisherDao.delete(testPublisher);

        // Then
        verify(transaction).begin();

        // Characters should be unlinked from publisher
        for (ComicCharacter character : characters) {
            verify(character).setPublisher(null);
        }

        assertTrue(managedPublisher.getPublisherCharacters().isEmpty(), "Publisher characters should be cleared");

        // Series should be unlinked from publisher
        for (Series s : series) {
            verify(s).setPublisher(null);
        }

        assertTrue(managedPublisher.getPublisherSeries().isEmpty(), "Publisher series should be cleared");

        verify(entityManager).remove(managedPublisher);
        verify(transaction).commit();
    }

    @Nested
    @DisplayName("findByIdWithDetails method tests")
    class FindByIdWithDetailsMethodTests {

        @Test
        @DisplayName("Given valid ID when findByIdWithDetails called then query is executed with proper parameters")
        void givenValidId_whenFindByIdWithDetailsCalled_thenQueryIsExecutedWithProperParameters() {
            // Given
            when(entityManager.createQuery(
                    "SELECT p FROM Publisher p LEFT JOIN FETCH p.publisherSeries LEFT JOIN FETCH p.publisherCharacters WHERE p.id = :id",
                    Publisher.class)).thenReturn(typedQuery);
            when(typedQuery.setParameter("id", VALID_ID)).thenReturn(typedQuery);
            when(typedQuery.getResultStream()).thenReturn(Stream.of(testPublisher));

            // When
            Publisher result = publisherDao.findByIdWithDetails(VALID_ID);

            // Then
            assertNotNull(result, "findByIdWithDetails should return a publisher");
            assertEquals(testPublisher, result, "findByIdWithDetails should return the correct publisher");
            verify(entityManager).createQuery(
                    "SELECT p FROM Publisher p LEFT JOIN FETCH p.publisherSeries LEFT JOIN FETCH p.publisherCharacters WHERE p.id = :id",
                    Publisher.class);
            verify(typedQuery).setParameter("id", VALID_ID);
            verify(typedQuery).getResultStream();
        }

        @Test
        @DisplayName("Given non-existent ID when findByIdWithDetails called then null is returned")
        void givenNonExistentId_whenFindByIdWithDetailsCalled_thenNullIsReturned() {
            // Given
            Long nonExistentId = 999L;
            when(entityManager.createQuery(
                    "SELECT p FROM Publisher p LEFT JOIN FETCH p.publisherSeries LEFT JOIN FETCH p.publisherCharacters WHERE p.id = :id",
                    Publisher.class)).thenReturn(typedQuery);
            when(typedQuery.setParameter("id", nonExistentId)).thenReturn(typedQuery);
            when(typedQuery.getResultStream()).thenReturn(Stream.empty());

            // When
            Publisher result = publisherDao.findByIdWithDetails(nonExistentId);

            // Then
            assertNull(result, "findByIdWithDetails should return null for non-existent ID");
            verify(entityManager).createQuery(
                    "SELECT p FROM Publisher p LEFT JOIN FETCH p.publisherSeries LEFT JOIN FETCH p.publisherCharacters WHERE p.id = :id",
                    Publisher.class);
            verify(typedQuery).setParameter("id", nonExistentId);
            verify(typedQuery).getResultStream();
        }
    }

    @Nested
    @DisplayName("Inherited methods tests")
    class InheritedMethodsTests {

        @Test
        @DisplayName("Given valid publisher when save called then publisher is merged in a transaction")
        void givenValidPublisher_whenSaveCalled_thenPublisherIsMergedInTransaction() {
            // Given
            EntityTransaction transaction = mock(EntityTransaction.class);
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.merge(testPublisher)).thenReturn(testPublisher);

            // When
            publisherDao.save(testPublisher);

            // Then
            verify(transaction).begin();
            verify(entityManager).merge(testPublisher);
            verify(transaction).commit();
        }

        @Test
        @DisplayName("Given valid ID when findById called then publisher is retrieved from EntityManager")
        void givenValidId_whenFindByIdCalled_thenPublisherIsRetrievedFromEntityManager() {
            // Given
            when(entityManager.find(Publisher.class, VALID_ID)).thenReturn(testPublisher);

            // When
            Publisher result = publisherDao.findById(VALID_ID);

            // Then
            assertNotNull(result, "findById should return a publisher");
            assertEquals(testPublisher, result, "findById should return the correct publisher");
            verify(entityManager).find(Publisher.class, VALID_ID);
        }

        @Test
        @DisplayName("When findAll called then all publishers are returned")
        void whenFindAllCalled_thenAllPublishersAreReturned() {
            // Given
            Publisher publisher1 = new Publisher("Marvel Comics");
            Publisher publisher2 = new Publisher("DC Comics");

            // Set IDs for the publishers
            try {
                java.lang.reflect.Field idField = Publisher.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(publisher1, 1L);
                idField.set(publisher2, 2L);
            } catch (Exception e) {
                fail("Failed to set up test: " + e.getMessage());
            }

            // Use a List instead of a Set to avoid hashCode issues with mocked objects
            List<Publisher> publisherList = Arrays.asList(publisher1, publisher2);

            when(entityManager.createQuery("FROM Publisher", Publisher.class)).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(publisherList);

            // When
            Set<Publisher> result = publisherDao.findAll();

            // Then
            assertNotNull(result, "findAll should return a non-null set");
            assertEquals(2, result.size(), "findAll should return all publishers");

            // Verify expected behaviour without relying on equals/hashCode
            verify(entityManager).createQuery("FROM Publisher", Publisher.class);
            verify(typedQuery).getResultList();
            assertTrue(result.contains(publisher1), "Result should contain publisher1");
            assertTrue(result.contains(publisher2), "Result should contain publisher2");
        }
    }
}
