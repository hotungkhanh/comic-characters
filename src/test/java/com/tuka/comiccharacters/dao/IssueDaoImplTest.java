package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Issue;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueDaoImplTest {

    private final Long VALID_ID = 1L;
    @Mock
    private EntityManager entityManager;
    @Mock
    private TypedQuery<Issue> typedQuery;
    private IssueDaoImpl issueDao;
    private Issue testIssue;

    @BeforeEach
    void setUp() {
        // Create a testable DAO that overrides getEntityManager
        issueDao = new IssueDaoImpl() {
            @Override
            protected EntityManager getEntityManager() {
                return entityManager;
            }
        };

        // Set up test data
        Series series = new Series("The Amazing Spider-Man", 1963);
        testIssue = new Issue(series, BigDecimal.valueOf(1), LocalDate.of(1963, 3, 1));
        try {
            java.lang.reflect.Field idField = Issue.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testIssue, VALID_ID);
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
            String expectedQuery = "SELECT i FROM Issue i " +
                    "LEFT JOIN FETCH i.issueCreators ic " +
                    "LEFT JOIN FETCH ic.creator " +
                    "LEFT JOIN FETCH ic.roles " +
                    "LEFT JOIN FETCH i.characters " +
                    "LEFT JOIN FETCH i.series s " +
                    "LEFT JOIN FETCH s.issues " +
                    "LEFT JOIN FETCH s.publisher " +
                    "WHERE i.id = :id";

            when(entityManager.createQuery(expectedQuery, Issue.class)).thenReturn(typedQuery);
            when(typedQuery.setParameter("id", VALID_ID)).thenReturn(typedQuery);
            when(typedQuery.getSingleResult()).thenReturn(testIssue);

            // When
            Issue result = issueDao.findByIdWithDetails(VALID_ID);

            // Then
            assertNotNull(result, "findByIdWithDetails should return an issue");
            assertEquals(testIssue, result, "findByIdWithDetails should return the correct issue");
            verify(entityManager).createQuery(expectedQuery, Issue.class);
            verify(typedQuery).setParameter("id", VALID_ID);
            verify(typedQuery).getSingleResult();
        }

        @Test
        @DisplayName("Given non-existent ID when findByIdWithDetails called then null is returned")
        void givenNonExistentId_whenFindByIdWithDetailsCalled_thenNullIsReturned() {
            // Given
            Long nonExistentId = 999L;
            String expectedQuery = "SELECT i FROM Issue i " +
                    "LEFT JOIN FETCH i.issueCreators ic " +
                    "LEFT JOIN FETCH ic.creator " +
                    "LEFT JOIN FETCH ic.roles " +
                    "LEFT JOIN FETCH i.characters " +
                    "LEFT JOIN FETCH i.series s " +
                    "LEFT JOIN FETCH s.issues " +
                    "LEFT JOIN FETCH s.publisher " +
                    "WHERE i.id = :id";

            when(entityManager.createQuery(expectedQuery, Issue.class)).thenReturn(typedQuery);
            when(typedQuery.setParameter("id", nonExistentId)).thenReturn(typedQuery);
            when(typedQuery.getSingleResult()).thenThrow(new jakarta.persistence.NoResultException("No entity found for query"));

            // When
            Issue result = issueDao.findByIdWithDetails(nonExistentId);

            // Then
            assertNull(result, "findByIdWithDetails should return null for non-existent ID");
            verify(entityManager).createQuery(expectedQuery, Issue.class);
            verify(typedQuery).setParameter("id", nonExistentId);
            verify(typedQuery).getSingleResult();
        }

        @Test
        @DisplayName("Given exception thrown when findByIdWithDetails called then null is returned")
        void givenExceptionThrown_whenFindByIdWithDetailsCalled_thenNullIsReturned() {
            // Given
            String expectedQuery = "SELECT i FROM Issue i " +
                    "LEFT JOIN FETCH i.issueCreators ic " +
                    "LEFT JOIN FETCH ic.creator " +
                    "LEFT JOIN FETCH ic.roles " +
                    "LEFT JOIN FETCH i.characters " +
                    "LEFT JOIN FETCH i.series s " +
                    "LEFT JOIN FETCH s.issues " +
                    "LEFT JOIN FETCH s.publisher " +
                    "WHERE i.id = :id";

            when(entityManager.createQuery(expectedQuery, Issue.class)).thenReturn(typedQuery);
            when(typedQuery.setParameter("id", VALID_ID)).thenReturn(typedQuery);
            when(typedQuery.getSingleResult()).thenThrow(new RuntimeException("Database connection error"));

            // When
            Issue result = issueDao.findByIdWithDetails(VALID_ID);

            // Then
            assertNull(result, "findByIdWithDetails should return null when an exception is thrown");
            verify(entityManager).createQuery(expectedQuery, Issue.class);
            verify(typedQuery).setParameter("id", VALID_ID);
            verify(typedQuery).getSingleResult();
        }
    }

    @Nested
    @DisplayName("Inherited methods tests")
    class InheritedMethodsTests {

        @Test
        @DisplayName("Given valid issue when save called then issue is merged in a transaction")
        void givenValidIssue_whenSaveCalled_thenIssueIsMergedInTransaction() {
            // Given
            EntityTransaction transaction = mock(EntityTransaction.class);
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.merge(testIssue)).thenReturn(testIssue);

            // When
            issueDao.save(testIssue);

            // Then
            verify(transaction).begin();
            verify(entityManager).merge(testIssue);
            verify(transaction).commit();
        }

        @Test
        @DisplayName("Given valid ID when findById called then issue is retrieved from EntityManager")
        void givenValidId_whenFindByIdCalled_thenIssueIsRetrievedFromEntityManager() {
            // Given
            when(entityManager.find(Issue.class, VALID_ID)).thenReturn(testIssue);

            // When
            Issue result = issueDao.findById(VALID_ID);

            // Then
            assertNotNull(result, "findById should return an issue");
            assertEquals(testIssue, result, "findById should return the correct issue");
            verify(entityManager).find(Issue.class, VALID_ID);
        }

        @Test
        @DisplayName("When findAll called then all issues are returned")
        void whenFindAllCalled_thenAllIssuesAreReturned() {
            // Given
            Series spiderManSeries = new Series("The Amazing Spider-Man", 1963);
            Series xMenSeries = new Series("Uncanny X-Men", 1963);

            Issue issue1 = new Issue(spiderManSeries, BigDecimal.valueOf(1), LocalDate.of(1963, 3, 1));
            Issue issue2 = new Issue(xMenSeries, BigDecimal.valueOf(1), LocalDate.of(1963, 9, 1));

            // Set IDs for the issues
            try {
                java.lang.reflect.Field idField = Issue.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(issue1, 1L);
                idField.set(issue2, 2L);
            } catch (Exception e) {
                fail("Failed to set up test: " + e.getMessage());
            }

            // Use a List instead of a Set to avoid hashCode issues with mocked objects
            List<Issue> issueList = Arrays.asList(issue1, issue2);

            when(entityManager.createQuery("FROM Issue", Issue.class)).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(issueList);

            // When
            Set<Issue> result = issueDao.findAll();

            // Then
            assertNotNull(result, "findAll should return a non-null set");
            assertEquals(2, result.size(), "findAll should return all issues");

            // Verify expected behaviour without relying on equals/hashCode
            verify(entityManager).createQuery("FROM Issue", Issue.class);
            verify(typedQuery).getResultList();
            assertTrue(result.contains(issue1), "Result should contain issue1");
            assertTrue(result.contains(issue2), "Result should contain issue2");
        }

        @Test
        @DisplayName("Given valid issue when delete called then issue is removed in a transaction")
        void givenValidIssue_whenDeleteCalled_thenIssueIsRemovedInTransaction() {
            // Given
            EntityTransaction transaction = mock(EntityTransaction.class);
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.merge(testIssue)).thenReturn(testIssue);

            // When
            issueDao.delete(testIssue);

            // Then
            verify(transaction).begin();
            verify(entityManager).merge(testIssue);
            verify(entityManager).remove(testIssue);
            verify(transaction).commit();
        }
    }
}
