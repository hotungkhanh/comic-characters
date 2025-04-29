package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Publisher;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterDaoImplTest {

    private final Long VALID_ID = 1L;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<ComicCharacter> typedQuery;

    private CharacterDaoImpl characterDao;
    private ComicCharacter testCharacter;

    @BeforeEach
    void setUp() {
        // Create a testable DAO that overrides getEntityManager
        characterDao = new CharacterDaoImpl() {
            @Override
            protected EntityManager getEntityManager() {
                return entityManager;
            }
        };

        // Set up test data
        Publisher publisher = new Publisher("Marvel Comics");
        testCharacter = new ComicCharacter("Spider-Man", "Peter Parker", publisher, "Friendly neighborhood Spider-Man");

        try {
            java.lang.reflect.Field idField = ComicCharacter.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testCharacter, VALID_ID);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }

    @Nested
    @DisplayName("findByIdWithDetails method tests")
    class FindByIdWithDetailsMethodTests {

        @Test
        @DisplayName("Given valid ID when findByIdWithDetails called then query is executed with proper parameters")
        void givenValidId_whenFindByIdWithDetailsCalled_thenQueryIsExecutedWithProperParameters() {
            // Given
            String expectedQuery = "SELECT c FROM ComicCharacter c LEFT JOIN FETCH c.creators LEFT JOIN FETCH c.issues LEFT JOIN FETCH c.firstAppearance LEFT JOIN FETCH c.publisher WHERE c.id = :id";
            when(entityManager.createQuery(expectedQuery, ComicCharacter.class)).thenReturn(typedQuery);
            when(typedQuery.setParameter("id", VALID_ID)).thenReturn(typedQuery);
            when(typedQuery.getResultStream()).thenReturn(Stream.of(testCharacter));

            // When
            ComicCharacter result = characterDao.findByIdWithDetails(VALID_ID);

            // Then
            assertNotNull(result, "findByIdWithDetails should return a character");
            assertEquals(testCharacter, result, "findByIdWithDetails should return the correct character");
            verify(entityManager).createQuery(expectedQuery, ComicCharacter.class);
            verify(typedQuery).setParameter("id", VALID_ID);
            verify(typedQuery).getResultStream();
        }

        @Test
        @DisplayName("Given non-existent ID when findByIdWithDetails called then null is returned")
        void givenNonExistentId_whenFindByIdWithDetailsCalled_thenNullIsReturned() {
            // Given
            Long nonExistentId = 999L;
            String expectedQuery = "SELECT c FROM ComicCharacter c LEFT JOIN FETCH c.creators LEFT JOIN FETCH c.issues LEFT JOIN FETCH c.firstAppearance LEFT JOIN FETCH c.publisher WHERE c.id = :id";
            when(entityManager.createQuery(expectedQuery, ComicCharacter.class)).thenReturn(typedQuery);
            when(typedQuery.setParameter("id", nonExistentId)).thenReturn(typedQuery);
            when(typedQuery.getResultStream()).thenReturn(Stream.empty());

            // When
            ComicCharacter result = characterDao.findByIdWithDetails(nonExistentId);

            // Then
            assertNull(result, "findByIdWithDetails should return null for non-existent ID");
            verify(entityManager).createQuery(expectedQuery, ComicCharacter.class);
            verify(typedQuery).setParameter("id", nonExistentId);
            verify(typedQuery).getResultStream();
        }

        @Test
        @DisplayName("Given exception during query when findByIdWithDetails called then null is returned")
        void givenExceptionDuringQuery_whenFindByIdWithDetailsCalled_thenNullIsReturned() {
            // Given
            String expectedQuery = "SELECT c FROM ComicCharacter c LEFT JOIN FETCH c.creators LEFT JOIN FETCH c.issues LEFT JOIN FETCH c.firstAppearance LEFT JOIN FETCH c.publisher WHERE c.id = :id";
            when(entityManager.createQuery(expectedQuery, ComicCharacter.class)).thenThrow(new RuntimeException("Database error"));

            // When
            ComicCharacter result = characterDao.findByIdWithDetails(VALID_ID);

            // Then
            assertNull(result, "findByIdWithDetails should return null when exception occurs");
            verify(entityManager).createQuery(expectedQuery, ComicCharacter.class);
        }
    }

    @Nested
    @DisplayName("Inherited methods tests")
    class InheritedMethodsTests {

        @Test
        @DisplayName("Given valid character when save called then character is merged in a transaction")
        void givenValidCharacter_whenSaveCalled_thenCharacterIsMergedInTransaction() {
            // Given
            EntityTransaction transaction = mock(EntityTransaction.class);
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.merge(testCharacter)).thenReturn(testCharacter);

            // When
            characterDao.save(testCharacter);

            // Then
            verify(transaction).begin();
            verify(entityManager).merge(testCharacter);
            verify(transaction).commit();
        }

        @Test
        @DisplayName("Given valid ID when findById called then character is retrieved from EntityManager")
        void givenValidId_whenFindByIdCalled_thenCharacterIsRetrievedFromEntityManager() {
            // Given
            when(entityManager.find(ComicCharacter.class, VALID_ID)).thenReturn(testCharacter);

            // When
            ComicCharacter result = characterDao.findById(VALID_ID);

            // Then
            assertNotNull(result, "findById should return a character");
            assertEquals(testCharacter, result, "findById should return the correct character");
            verify(entityManager).find(ComicCharacter.class, VALID_ID);
        }

        @Test
        @DisplayName("When findAll called then all characters are returned")
        void whenFindAllCalled_thenAllCharactersAreReturned() {
            // Given
            Publisher marvel = new Publisher("Marvel Comics");
            Publisher dc = new Publisher("DC Comics");

            ComicCharacter character1 = new ComicCharacter("Spider-Man", "Peter Parker", marvel, "Friendly neighborhood Spider-Man");
            ComicCharacter character2 = new ComicCharacter("Batman", "Bruce Wayne", dc, "The Dark Knight");

            // Set IDs for the characters
            try {
                java.lang.reflect.Field idField = ComicCharacter.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(character1, 1L);
                idField.set(character2, 2L);
            } catch (Exception e) {
                fail("Failed to set up test: " + e.getMessage());
            }

            // Use a List instead of a Set to avoid hashCode issues with mocked objects
            List<ComicCharacter> characterList = Arrays.asList(character1, character2);

            when(entityManager.createQuery("FROM ComicCharacter", ComicCharacter.class)).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(characterList);

            // When
            Set<ComicCharacter> result = characterDao.findAll();

            // Then
            assertNotNull(result, "findAll should return a non-null set");
            assertEquals(2, result.size(), "findAll should return all characters");

            // Verify expected behavior without relying on equals/hashCode
            verify(entityManager).createQuery("FROM ComicCharacter", ComicCharacter.class);
            verify(typedQuery).getResultList();
            assertTrue(result.contains(character1), "Result should contain character1");
            assertTrue(result.contains(character2), "Result should contain character2");
        }

        @Test
        @DisplayName("Given valid character when delete called then character is removed in a transaction")
        void givenValidCharacter_whenDeleteCalled_thenCharacterIsRemovedInTransaction() {
            // Given
            EntityTransaction transaction = mock(EntityTransaction.class);
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.merge(testCharacter)).thenReturn(testCharacter);

            // When
            characterDao.delete(testCharacter);

            // Then
            verify(transaction).begin();
            verify(entityManager).merge(testCharacter);
            verify(entityManager).remove(testCharacter);
            verify(transaction).commit();
        }
    }
}
