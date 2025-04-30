package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.*;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueDaoImplTest {

    private final Long VALID_ID = 1L;

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction transaction;

    @Mock
    private TypedQuery<Issue> typedQuery;

    private IssueDaoImpl issueDao;
    private Issue testIssue;
    private ComicCharacter character1;
    private ComicCharacter character2;
    private Series series;

    @BeforeEach
    void setUp() {
        issueDao = new IssueDaoImpl() {
            @Override
            protected EntityManager getEntityManager() {
                return entityManager;
            }
        };

        character1 = new ComicCharacter();
        character2 = new ComicCharacter();
        series = new Series();
        series.setIssues(new HashSet<>());

        testIssue = new Issue();
        testIssue.setCharacters(new HashSet<>(Set.of(character1, character2)));
        testIssue.setSeries(series);

        try {
            java.lang.reflect.Field idField = Issue.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testIssue, VALID_ID);

            java.lang.reflect.Field idFieldChar1 = ComicCharacter.class.getDeclaredField("id");
            idFieldChar1.setAccessible(true);
            idFieldChar1.set(character1, 100L);

            java.lang.reflect.Field idFieldChar2 = ComicCharacter.class.getDeclaredField("id");
            idFieldChar2.setAccessible(true);
            idFieldChar2.set(character2, 101L);

            java.lang.reflect.Field idFieldSeries = Series.class.getDeclaredField("id");
            idFieldSeries.setAccessible(true);
            idFieldSeries.set(series, 200L);

        } catch (Exception e) {
            fail("Failed to set up test ID: " + e.getMessage());
        }
    }

    @Nested
    @DisplayName("findByIdWithDetails tests")
    class FindByIdWithDetailsTests {

        @Test
        @DisplayName("Returns issue when found")
        void whenFound_returnsIssue() {
            String query = "SELECT i FROM Issue i " +
                    "LEFT JOIN FETCH i.issueCreators ic " +
                    "LEFT JOIN FETCH ic.creator " +
                    "LEFT JOIN FETCH ic.roles " +
                    "LEFT JOIN FETCH i.characters " +
                    "LEFT JOIN FETCH i.series s " +
                    "LEFT JOIN FETCH s.issues " +
                    "LEFT JOIN FETCH s.publisher " +
                    "WHERE i.id = :id";

            when(entityManager.createQuery(query, Issue.class)).thenReturn(typedQuery);
            when(typedQuery.setParameter("id", VALID_ID)).thenReturn(typedQuery);
            when(typedQuery.getSingleResult()).thenReturn(testIssue);

            Issue result = issueDao.findByIdWithDetails(VALID_ID);

            assertNotNull(result);
            assertEquals(testIssue, result);
        }

        @Test
        @DisplayName("Returns null if exception occurs")
        void whenExceptionOccurs_returnsNull() {
            String query = "SELECT i FROM Issue i " +
                    "LEFT JOIN FETCH i.issueCreators ic " +
                    "LEFT JOIN FETCH ic.creator " +
                    "LEFT JOIN FETCH ic.roles " +
                    "LEFT JOIN FETCH i.characters " +
                    "LEFT JOIN FETCH i.series s " +
                    "LEFT JOIN FETCH s.issues " +
                    "LEFT JOIN FETCH s.publisher " +
                    "WHERE i.id = :id";

            when(entityManager.createQuery(query, Issue.class)).thenThrow(new RuntimeException("DB error"));

            Issue result = issueDao.findByIdWithDetails(VALID_ID);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("save method tests")
    class SaveMethodTests {

        @Test
        @DisplayName("Saves issue with no creator successfully")
        void savesIssueSuccessfully() {
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.merge(testIssue)).thenReturn(testIssue);

            issueDao.save(testIssue);

            verify(transaction).begin();
            verify(entityManager).merge(testIssue);
            verify(transaction).commit();
        }

        @Test
        @DisplayName("Saves issue with creators successfully")
        void savesIssueWithCreatorsSuccessfully() {
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.merge(testIssue)).thenReturn(testIssue);

            IssueCreator issueCreator = new IssueCreator(new Creator(), Set.of(Role.WRITER));
            issueCreator.setIssue(testIssue);

            Set<IssueCreator> issueCreators = new HashSet<>();
            issueCreators.add(issueCreator);
            testIssue.setIssueCreators(issueCreators);

            issueDao.save(testIssue);

            verify(transaction).begin();
            verify(entityManager).merge(testIssue);
            verify(entityManager).persist(issueCreator);
            verify(transaction).commit();

            assertEquals(testIssue, issueCreator.getIssue());
            assertTrue(testIssue.getIssueCreators().contains(issueCreator));
        }

        @Test
        @DisplayName("Rolls back transaction if exception occurs")
        void rollsBackOnException() {
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.merge(testIssue)).thenThrow(new RuntimeException("Error during merge"));
            when(transaction.isActive()).thenReturn(true);

            doNothing().when(transaction).rollback();

            RuntimeException thrown = assertThrows(RuntimeException.class, () -> issueDao.save(testIssue));
            assertTrue(thrown.getMessage().contains("Error saving issue"));

            verify(transaction).begin();
            verify(transaction).rollback();
        }
    }

    @Nested
    @DisplayName("delete method tests")
    class DeleteMethodTests {

        @Test
        @DisplayName("Deletes issue and clears associations")
        void deletesIssueAndRemovesReferences() {
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.find(Issue.class, VALID_ID)).thenReturn(testIssue);
            when(entityManager.find(Series.class, 200L)).thenReturn(series);
            when(entityManager.find(ComicCharacter.class, 100L)).thenReturn(character1);
            when(entityManager.find(ComicCharacter.class, 101L)).thenReturn(character2);

            series.getIssues().add(testIssue);
            character1.getIssues().add(testIssue);
            character2.getIssues().add(testIssue);

            issueDao.delete(testIssue);

            verify(transaction).begin();
            verify(entityManager).remove(testIssue);
            verify(transaction).commit();

            assertFalse(series.getIssues().contains(testIssue));
            assertFalse(character1.getIssues().contains(testIssue));
            assertFalse(character2.getIssues().contains(testIssue));
            assertTrue(testIssue.getCharacters().isEmpty());
        }

        @Test
        @DisplayName("Rolls back transaction if exception during delete")
        void rollsBackDeleteOnException() {
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.find(Issue.class, VALID_ID)).thenThrow(new RuntimeException("DB error"));
            when(transaction.isActive()).thenReturn(true);

            doNothing().when(transaction).rollback();

            RuntimeException thrown = assertThrows(RuntimeException.class, () -> issueDao.delete(testIssue));
            assertTrue(thrown.getMessage().contains("Error deleting issue"));

            verify(transaction).begin();
            verify(transaction).rollback();
        }
    }
}
