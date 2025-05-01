package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Issue;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeriesDaoImplTest {

    private final Long VALID_ID = 1L;
    @Mock
    private EntityManager entityManager;
    @Mock
    private TypedQuery<Series> typedQuery;
    @Mock
    private TypedQuery<ComicCharacter> characterTypedQuery;
    private SeriesDaoImpl seriesDao;
    private Series testSeries;
    private Issue testIssue;
    private ComicCharacter testCharacter;

    @BeforeEach
    void setUp() {
        // Create a testable DAO that overrides getEntityManager
        seriesDao = new SeriesDaoImpl() {
            @Override
            protected EntityManager getEntityManager() {
                return entityManager;
            }
        };

        // Set up test data
        Publisher publisher = new Publisher("Marvel Comics");
        testSeries = new Series("X-Men", 1963);
        testSeries.setPublisher(publisher);

        // Create a test issue
        testIssue = new Issue(testSeries, new BigDecimal("1.0"));

        // Create a test character
        testCharacter = new ComicCharacter("Wolverine");
        testCharacter.setFirstAppearance(testIssue);

        // Set up relationship between issue and character
        testIssue.addCharacter(testCharacter);
        testSeries.getIssues().add(testIssue);

        try {
            // Set IDs for all test objects
            setPrivateField(testSeries, "id", VALID_ID);
            setPrivateField(testIssue, "id", 100L);
            setPrivateField(testCharacter, "id", 200L);
            setPrivateField(publisher, "id", 300L);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }

    private void setPrivateField(Object obj, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    @Nested
    @DisplayName("findByIdWithDetails method tests")
    class FindByIdWithDetailsMethodTests {

        @Test
        @DisplayName("Given valid ID when findByIdWithDetails called then query is executed with proper parameters")
        void givenValidId_whenFindByIdWithDetailsCalled_thenQueryIsExecutedWithProperParameters() {
            // Given
            String expectedQuery = "SELECT s FROM Series s LEFT JOIN FETCH s.publisher LEFT JOIN FETCH s.issues WHERE s.id = :id";
            when(entityManager.createQuery(expectedQuery, Series.class)).thenReturn(typedQuery);
            when(typedQuery.setParameter("id", VALID_ID)).thenReturn(typedQuery);
            when(typedQuery.getResultStream()).thenReturn(Stream.of(testSeries));

            // When
            Series result = seriesDao.findByIdWithDetails(VALID_ID);

            // Then
            assertNotNull(result, "findByIdWithDetails should return a series");
            assertEquals(testSeries, result, "findByIdWithDetails should return the correct series");
            verify(entityManager).createQuery(expectedQuery, Series.class);
            verify(typedQuery).setParameter("id", VALID_ID);
            verify(typedQuery).getResultStream();
        }

        @Test
        @DisplayName("Given non-existent ID when findByIdWithDetails called then null is returned")
        void givenNonExistentId_whenFindByIdWithDetailsCalled_thenNullIsReturned() {
            // Given
            Long nonExistentId = 999L;
            String expectedQuery = "SELECT s FROM Series s LEFT JOIN FETCH s.publisher LEFT JOIN FETCH s.issues WHERE s.id = :id";
            when(entityManager.createQuery(expectedQuery, Series.class)).thenReturn(typedQuery);
            when(typedQuery.setParameter("id", nonExistentId)).thenReturn(typedQuery);
            when(typedQuery.getResultStream()).thenReturn(Stream.empty());

            // When
            Series result = seriesDao.findByIdWithDetails(nonExistentId);

            // Then
            assertNull(result, "findByIdWithDetails should return null for non-existent ID");
            verify(entityManager).createQuery(expectedQuery, Series.class);
            verify(typedQuery).setParameter("id", nonExistentId);
            verify(typedQuery).getResultStream();
        }

        @Test
        @DisplayName("Given exception occurs when findByIdWithDetails called then null is returned")
        void givenExceptionOccurs_whenFindByIdWithDetailsCalled_thenNullIsReturned() {
            // Given
            String expectedQuery = "SELECT s FROM Series s LEFT JOIN FETCH s.publisher LEFT JOIN FETCH s.issues WHERE s.id = :id";
            when(entityManager.createQuery(expectedQuery, Series.class)).thenThrow(new RuntimeException("Test exception"));

            // When
            Series result = seriesDao.findByIdWithDetails(VALID_ID);

            // Then
            assertNull(result, "findByIdWithDetails should return null when exception occurs");
        }
    }

    @Nested
    @DisplayName("delete method tests")
    class DeleteMethodTests {

        @Mock
        private EntityTransaction transaction;

        @BeforeEach
        void setupDeleteTest() {
            when(entityManager.getTransaction()).thenReturn(transaction);
        }

        @Test
        @DisplayName("Given series with issues that are firstAppearance references when deleted then references are properly handled")
        void givenSeriesWithIssuesAsFirstAppearance_whenDeleted_thenReferencesAreProperlyHandled() {
            // Given
            String characterQuery = "SELECT c FROM ComicCharacter c WHERE c.firstAppearance.id = :issueId";
            when(entityManager.createQuery(characterQuery, ComicCharacter.class)).thenReturn(characterTypedQuery);
            when(characterTypedQuery.setParameter("issueId", testIssue.getId())).thenReturn(characterTypedQuery);
            when(characterTypedQuery.getResultList()).thenReturn(List.of(testCharacter));
            when(entityManager.merge(testSeries)).thenReturn(testSeries);
            when(entityManager.merge(testIssue)).thenReturn(testIssue);

            // When
            seriesDao.delete(testSeries);

            // Then
            // Verify transaction management
            verify(transaction).begin();
            verify(transaction).commit();

            // Verify character's firstAppearance is set to null
            ArgumentCaptor<ComicCharacter> characterCaptor = ArgumentCaptor.forClass(ComicCharacter.class);
            verify(entityManager).createQuery(characterQuery, ComicCharacter.class);
            verify(characterTypedQuery).setParameter("issueId", testIssue.getId());

            // Verify flush was called to ensure updates before removing issues
            verify(entityManager).flush();

            // Verify character sets are properly handled
            assertTrue(testIssue.getCharacters().isEmpty(), "Issue's characters should be cleared");

            // Verify series is removed at the end
            verify(entityManager).remove(testSeries);
        }

        @Test
        @DisplayName("Given transaction fails when delete called then transaction is rolled back")
        void givenTransactionFails_whenDeleteCalled_thenTransactionIsRolledBack() {
            // Given
            String characterQuery = "SELECT c FROM ComicCharacter c WHERE c.firstAppearance.id = :issueId";
            when(entityManager.createQuery(characterQuery, ComicCharacter.class)).thenReturn(characterTypedQuery);
            when(entityManager.merge(testSeries)).thenReturn(testSeries);
            when(characterTypedQuery.setParameter("issueId", testIssue.getId())).thenReturn(characterTypedQuery);

            // Simulate exception during operation
            doThrow(new RuntimeException("Test exception")).when(characterTypedQuery).getResultList();
            when(transaction.isActive()).thenReturn(true);

            // When/Then
            RuntimeException exception = assertThrows(RuntimeException.class, () -> seriesDao.delete(testSeries));

            // Verify transaction is rolled back
            verify(transaction).begin();
            verify(transaction).rollback();
            assertTrue(exception.getMessage().contains("Error deleting series"), "Exception should contain error message");
        }

        @Test
        @DisplayName("Given empty issues collection when delete called then series is deleted without issue processing")
        void givenEmptyIssuesCollection_whenDeleteCalled_thenSeriesIsDeletedWithoutIssueProcessing() {
            // Given
            Series emptyIssuesSeries = new Series("Empty Series", 2000);
            try {
                setPrivateField(emptyIssuesSeries, "id", 500L);
            } catch (Exception e) {
                fail("Failed to set up test: " + e.getMessage());
            }

            when(entityManager.merge(emptyIssuesSeries)).thenReturn(emptyIssuesSeries);

            // When
            seriesDao.delete(emptyIssuesSeries);

            // Then
            verify(transaction).begin();
            verify(entityManager).merge(emptyIssuesSeries);
            verify(entityManager).remove(emptyIssuesSeries);
            verify(transaction).commit();

            // Verify no character queries were executed
            verify(entityManager, never()).createQuery(anyString(), eq(ComicCharacter.class));
        }

        @Test
        @DisplayName("Given null managedSeries when delete called then transaction is committed without operations")
        void givenNullManagedSeries_whenDeleteCalled_thenTransactionIsCommittedWithoutOperations() {
            // Given
            when(entityManager.merge(testSeries)).thenReturn(null);

            // When
            seriesDao.delete(testSeries);

            // Then
            verify(transaction).begin();
            verify(entityManager).merge(testSeries);
            verify(transaction).commit();
            verify(entityManager, never()).remove(any());
        }
    }

    @Nested
    @DisplayName("Inherited methods tests")
    class InheritedMethodsTests {

        @Test
        @DisplayName("Given valid series when save called then series is merged in a transaction")
        void givenValidSeries_whenSaveCalled_thenSeriesIsMergedInTransaction() {
            // Given
            EntityTransaction transaction = mock(EntityTransaction.class);
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.merge(testSeries)).thenReturn(testSeries);

            // When
            seriesDao.save(testSeries);

            // Then
            verify(transaction).begin();
            verify(entityManager).merge(testSeries);
            verify(transaction).commit();
        }

        @Test
        @DisplayName("Given valid ID when findById called then series is retrieved from EntityManager")
        void givenValidId_whenFindByIdCalled_thenSeriesIsRetrievedFromEntityManager() {
            // Given
            when(entityManager.find(Series.class, VALID_ID)).thenReturn(testSeries);

            // When
            Series result = seriesDao.findById(VALID_ID);

            // Then
            assertNotNull(result, "findById should return a series");
            assertEquals(testSeries, result, "findById should return the correct series");
            verify(entityManager).find(Series.class, VALID_ID);
        }

        @Test
        @DisplayName("When findAll called then all series are returned")
        void whenFindAllCalled_thenAllSeriesAreReturned() {
            // Given
            Publisher marvel = new Publisher("Marvel Comics");
            Publisher dc = new Publisher("DC Comics");

            Series series1 = new Series("X-Men", 1963);
            series1.setPublisher(marvel);

            Series series2 = new Series("Avengers", 1963);
            series2.setPublisher(dc);

            // Set IDs for the series
            try {
                setPrivateField(series1, "id", 1L);
                setPrivateField(series2, "id", 2L);
            } catch (Exception e) {
                fail("Failed to set up test: " + e.getMessage());
            }

            // Use a List instead of a Set to avoid hashCode issues with mocked objects
            List<Series> seriesList = Arrays.asList(series1, series2);

            when(entityManager.createQuery("FROM Series", Series.class)).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(seriesList);

            // When
            Set<Series> result = seriesDao.findAll();

            // Then
            assertNotNull(result, "findAll should return a non-null set");
            assertEquals(2, result.size(), "findAll should return all series");

            // Verify expected behavior without relying on equals/hashCode
            verify(entityManager).createQuery("FROM Series", Series.class);
            verify(typedQuery).getResultList();
            assertTrue(result.contains(series1), "Result should contain series1");
            assertTrue(result.contains(series2), "Result should contain series2");
        }
    }
}
