package com.tuka.comiccharacters.dao;

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

import java.util.Arrays;
import java.util.List;
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
    private TypedQuery<Creator> typedQuery;
    private CreatorDaoImpl creatorDao;
    private Creator testCreator;

    @BeforeEach
    void setUp() {
        // Create a testable DAO that overrides getEntityManager
        creatorDao = new CreatorDaoImpl() {
            @Override
            protected EntityManager getEntityManager() {
                return entityManager;
            }
        };

        // Set up test data
        testCreator = new Creator("Stan Lee");
        try {
            java.lang.reflect.Field idField = Creator.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testCreator, VALID_ID);
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
            String expectedQuery = "SELECT c FROM Creator c LEFT JOIN FETCH c.creditedCharacters LEFT JOIN FETCH c.issueCreators ic LEFT JOIN FETCH ic.issue WHERE c.id = :id";
            when(entityManager.createQuery(expectedQuery, Creator.class)).thenReturn(typedQuery);
            when(typedQuery.setParameter("id", VALID_ID)).thenReturn(typedQuery);
            when(typedQuery.getResultStream()).thenReturn(Stream.of(testCreator));

            // When
            Creator result = creatorDao.findByIdWithDetails(VALID_ID);

            // Then
            assertNotNull(result, "findByIdWithDetails should return a creator");
            assertEquals(testCreator, result, "findByIdWithDetails should return the correct creator");
            verify(entityManager).createQuery(expectedQuery, Creator.class);
            verify(typedQuery).setParameter("id", VALID_ID);
            verify(typedQuery).getResultStream();
        }

        @Test
        @DisplayName("Given non-existent ID when findByIdWithDetails called then null is returned")
        void givenNonExistentId_whenFindByIdWithDetailsCalled_thenNullIsReturned() {
            // Given
            Long nonExistentId = 999L;
            String expectedQuery = "SELECT c FROM Creator c LEFT JOIN FETCH c.creditedCharacters LEFT JOIN FETCH c.issueCreators ic LEFT JOIN FETCH ic.issue WHERE c.id = :id";
            when(entityManager.createQuery(expectedQuery, Creator.class)).thenReturn(typedQuery);
            when(typedQuery.setParameter("id", nonExistentId)).thenReturn(typedQuery);
            when(typedQuery.getResultStream()).thenReturn(Stream.empty());

            // When
            Creator result = creatorDao.findByIdWithDetails(nonExistentId);

            // Then
            assertNull(result, "findByIdWithDetails should return null for non-existent ID");
            verify(entityManager).createQuery(expectedQuery, Creator.class);
            verify(typedQuery).setParameter("id", nonExistentId);
            verify(typedQuery).getResultStream();
        }
    }

    @Nested
    @DisplayName("Inherited methods tests")
    class InheritedMethodsTests {

        @Test
        @DisplayName("Given valid creator when save called then creator is merged in a transaction")
        void givenValidCreator_whenSaveCalled_thenCreatorIsMergedInTransaction() {
            // Given
            EntityTransaction transaction = mock(EntityTransaction.class);
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.merge(testCreator)).thenReturn(testCreator);

            // When
            creatorDao.save(testCreator);

            // Then
            verify(transaction).begin();
            verify(entityManager).merge(testCreator);
            verify(transaction).commit();
        }

        @Test
        @DisplayName("Given valid ID when findById called then creator is retrieved from EntityManager")
        void givenValidId_whenFindByIdCalled_thenCreatorIsRetrievedFromEntityManager() {
            // Given
            when(entityManager.find(Creator.class, VALID_ID)).thenReturn(testCreator);

            // When
            Creator result = creatorDao.findById(VALID_ID);

            // Then
            assertNotNull(result, "findById should return a creator");
            assertEquals(testCreator, result, "findById should return the correct creator");
            verify(entityManager).find(Creator.class, VALID_ID);
        }

        @Test
        @DisplayName("When findAll called then all creators are returned")
        void whenFindAllCalled_thenAllCreatorsAreReturned() {
            // Given
            Creator creator1 = new Creator("Stan Lee");
            Creator creator2 = new Creator("Jack Kirby");

            // Set IDs for the creators
            try {
                java.lang.reflect.Field idField = Creator.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(creator1, 1L);
                idField.set(creator2, 2L);
            } catch (Exception e) {
                fail("Failed to set up test: " + e.getMessage());
            }

            // Use a List instead of a Set to avoid hashCode issues with mocked objects
            List<Creator> creatorList = Arrays.asList(creator1, creator2);

            when(entityManager.createQuery("FROM Creator", Creator.class)).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(creatorList);

            // When
            Set<Creator> result = creatorDao.findAll();

            // Then
            assertNotNull(result, "findAll should return a non-null set");
            assertEquals(2, result.size(), "findAll should return all creators");

            // Verify expected behaviour without relying on equals/hashCode
            verify(entityManager).createQuery("FROM Creator", Creator.class);
            verify(typedQuery).getResultList();
            assertTrue(result.contains(creator1), "Result should contain creator1");
            assertTrue(result.contains(creator2), "Result should contain creator2");
        }

        @Test
        @DisplayName("Given valid creator when delete called then creator is removed in a transaction")
        void givenValidCreator_whenDeleteCalled_thenCreatorIsRemovedInTransaction() {
            // Given
            EntityTransaction transaction = mock(EntityTransaction.class);
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.merge(testCreator)).thenReturn(testCreator);

            // When
            creatorDao.delete(testCreator);

            // Then
            verify(transaction).begin();
            verify(entityManager).merge(testCreator);
            verify(entityManager).remove(testCreator);
            verify(transaction).commit();
        }
    }
}
