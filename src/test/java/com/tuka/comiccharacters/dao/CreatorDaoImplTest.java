package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Creator;
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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatorDaoImplTest {

    private final Long VALID_ID = 1L;

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction transaction;

    @Mock
    private TypedQuery<Creator> typedQuery;

    private CreatorDaoImpl creatorDao;
    private Creator testCreator;
    private ComicCharacter character1;
    private ComicCharacter character2;

    @BeforeEach
    void setUp() {
        creatorDao = new CreatorDaoImpl() {
            @Override
            protected EntityManager getEntityManager() {
                return entityManager;
            }
        };

        character1 = new ComicCharacter();
        character2 = new ComicCharacter();
        testCreator = new Creator();
        testCreator.addCreditedCharacter(character1);
        testCreator.addCreditedCharacter(character2);

        try {
            java.lang.reflect.Field idField = Creator.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testCreator, VALID_ID);
        } catch (Exception e) {
            fail("Failed to set up test ID: " + e.getMessage());
        }
    }

    @Nested
    @DisplayName("findByIdWithDetails tests")
    class FindByIdWithDetailsTests {

        @Test
        @DisplayName("Returns creator when found")
        void whenFound_returnsCreator() {
            String query = "SELECT c FROM Creator c LEFT JOIN FETCH c.creditedCharacters LEFT JOIN FETCH c.issueCreators ic LEFT JOIN FETCH ic.issue WHERE c.id = :id";

            when(entityManager.createQuery(query, Creator.class)).thenReturn(typedQuery);
            when(typedQuery.setParameter("id", VALID_ID)).thenReturn(typedQuery);
            when(typedQuery.getResultStream()).thenReturn(Stream.of(testCreator));

            Creator result = creatorDao.findByIdWithDetails(VALID_ID);

            assertNotNull(result);
            assertEquals(testCreator, result);
            verify(entityManager).createQuery(query, Creator.class);
        }

        @Test
        @DisplayName("Returns null if not found")
        void whenNotFound_returnsNull() {
            String query = "SELECT c FROM Creator c LEFT JOIN FETCH c.creditedCharacters LEFT JOIN FETCH c.issueCreators ic LEFT JOIN FETCH ic.issue WHERE c.id = :id";

            when(entityManager.createQuery(query, Creator.class)).thenReturn(typedQuery);
            when(typedQuery.setParameter("id", VALID_ID)).thenReturn(typedQuery);
            when(typedQuery.getResultStream()).thenReturn(Stream.empty());

            Creator result = creatorDao.findByIdWithDetails(VALID_ID);

            assertNull(result);
        }

        @Test
        @DisplayName("Returns null if exception occurs")
        void whenExceptionOccurs_returnsNull() {
            String query = "SELECT c FROM Creator c LEFT JOIN FETCH c.creditedCharacters LEFT JOIN FETCH c.issueCreators ic LEFT JOIN FETCH ic.issue WHERE c.id = :id";

            when(entityManager.createQuery(query, Creator.class)).thenThrow(new RuntimeException("DB error"));

            Creator result = creatorDao.findByIdWithDetails(VALID_ID);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("delete method tests")
    class DeleteMethodTests {

        @Test
        @DisplayName("Removes creator and clears associations")
        void removesCreatorAndClearsAssociations() {
            // Given
            character1.setCreators(new HashSet<>(Set.of(testCreator)));
            character2.setCreators(new HashSet<>(Set.of(testCreator)));

            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.find(Creator.class, VALID_ID)).thenReturn(testCreator);

            // When
            creatorDao.delete(testCreator);

            // Then
            verify(transaction).begin();
            verify(entityManager).remove(testCreator);
            verify(transaction).commit();

            assertTrue(character1.getCreators().isEmpty(), "Creator should be removed from character1");
            assertTrue(character2.getCreators().isEmpty(), "Creator should be removed from character2");
            assertTrue(testCreator.getCreditedCharacters().isEmpty(), "Credited characters should be cleared");
        }

        @Test
        @DisplayName("Does nothing if creator not found")
        void doesNothingIfNotFound() {
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.find(Creator.class, VALID_ID)).thenReturn(null);

            creatorDao.delete(testCreator);

            verify(transaction).begin();
            verify(entityManager, never()).remove(any());
            verify(transaction).commit();
        }

        @Test
        @DisplayName("Rolls back transaction on exception")
        void rollsBackOnException() {
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.find(Creator.class, VALID_ID)).thenThrow(new RuntimeException("Find failed"));
            when(transaction.isActive()).thenReturn(true);

            assertThrows(RuntimeException.class, () -> creatorDao.delete(testCreator));

            verify(transaction).begin();
            verify(transaction).rollback();
        }
    }
}
