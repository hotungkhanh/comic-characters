package com.tuka.comiccharacters.dao;

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
    private SeriesDaoImpl seriesDao;
    private Series testSeries;

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

        try {
            java.lang.reflect.Field idField = Series.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testSeries, VALID_ID);
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
            series2.setPublisher(marvel);

            // Set IDs for the series
            try {
                java.lang.reflect.Field idField = Series.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(series1, 1L);
                idField.set(series2, 2L);
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

        @Test
        @DisplayName("Given valid series when delete called then series is removed in a transaction")
        void givenValidSeries_whenDeleteCalled_thenSeriesIsRemovedInTransaction() {
            // Given
            EntityTransaction transaction = mock(EntityTransaction.class);
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.merge(testSeries)).thenReturn(testSeries);

            // When
            seriesDao.delete(testSeries);

            // Then
            verify(transaction).begin();
            verify(entityManager).merge(testSeries);
            verify(entityManager).remove(testSeries);
            verify(transaction).commit();
        }
    }
}
