package com.tuka.comiccharacters.dao;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractJpaDaoTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityTransaction transaction;
    @Mock
    private TypedQuery<TestEntity> typedQuery;
    private TestDao dao;
    private TestEntity testEntity;

    @BeforeEach
    void setUp() {
        // Initialise the DAO with mocked EntityManager provider
        dao = new TestDao(entityManager);

        // Set up the entity
        testEntity = new TestEntity(1L, "Test Entity");
    }

    // Test entity class that's accessible to all parts of the test
    static class TestEntity {
        private final Long id;
        private String name;

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    // Test implementation of AbstractJpaDao that overrides the getEntityManager method
    static class TestDao extends AbstractJpaDao<TestEntity> {
        private final EntityManager entityManager;

        public TestDao(EntityManager entityManager) {
            super(TestEntity.class);
            this.entityManager = entityManager;
        }

        @Override
        public TestEntity findByIdWithDetails(Long id) {
            return findById(id);
        }

        @Override
        protected EntityManager getEntityManager() {
            return entityManager;
        }
    }

    @Nested
    @DisplayName("save method tests")
    class SaveMethodTests {
        @BeforeEach
        void setUpTransaction() {
            when(entityManager.getTransaction()).thenReturn(transaction);
        }

        @Test
        @DisplayName("Given valid entity when save called then entity is merged in transaction")
        void givenValidEntity_whenSaveCalled_thenEntityIsMergedInTransaction() {
            // Given
            when(entityManager.merge(testEntity)).thenReturn(testEntity);

            // When
            dao.save(testEntity);

            // Then
            verify(transaction).begin();
            verify(entityManager).merge(testEntity);
            verify(transaction).commit();
        }

        @Test
        @DisplayName("Given exception during save when save called then transaction is rolled back")
        void givenExceptionDuringSave_whenSaveCalled_thenTransactionIsRolledBack() {
            // Given
            when(entityManager.merge(testEntity)).thenThrow(new RuntimeException("Test exception"));
            when(transaction.isActive()).thenReturn(true);

            // When/Then
            try {
                dao.save(testEntity);
            } catch (RuntimeException e) {
                // Expected exception
            }

            verify(transaction).begin();
            verify(transaction).rollback();
        }
    }

    @Nested
    @DisplayName("findById method tests")
    class FindByIdMethodTests {

        @Test
        @DisplayName("Given valid ID when findById called then entity is retrieved from EntityManager")
        void givenValidId_whenFindByIdCalled_thenEntityIsRetrievedFromEntityManager() {
            // Given
            when(entityManager.find(TestEntity.class, 1L)).thenReturn(testEntity);

            // When
            TestEntity result = dao.findById(1L);

            // Then
            assertSame(testEntity, result);
            verify(entityManager).find(TestEntity.class, 1L);
        }
    }

    @Nested
    @DisplayName("findAll method tests")
    class FindAllMethodTests {

        @Test
        @DisplayName("When findAll called then query is executed and results returned as set")
        void whenFindAllCalled_thenQueryIsExecutedAndResultsReturnedAsSet() {
            // Given
            TestEntity entity1 = new TestEntity(1L, "Entity 1");
            TestEntity entity2 = new TestEntity(2L, "Entity 2");
            List<TestEntity> resultList = Arrays.asList(entity1, entity2);

            when(entityManager.createQuery("FROM " + TestEntity.class.getSimpleName(), TestEntity.class))
                    .thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(resultList);

            // When
            Set<TestEntity> results = dao.findAll();

            // Then
            assertEquals(new HashSet<>(resultList), results);
            verify(entityManager).createQuery("FROM " + TestEntity.class.getSimpleName(), TestEntity.class);
            verify(typedQuery).getResultList();
        }
    }

    @Nested
    @DisplayName("delete method tests")
    class DeleteMethodTests {
        @BeforeEach
        void setUpTransaction() {
            when(entityManager.getTransaction()).thenReturn(transaction);
        }

        @Test
        @DisplayName("Given valid entity when delete called then merged entity is removed in transaction")
        void givenValidEntity_whenDeleteCalled_thenMergedEntityIsRemovedInTransaction() {
            // Given
            TestEntity managedEntity = new TestEntity(1L, "Managed Entity");
            when(entityManager.merge(testEntity)).thenReturn(managedEntity);

            // When
            dao.delete(testEntity);

            // Then
            verify(transaction).begin();
            verify(entityManager).merge(testEntity);
            verify(entityManager).remove(managedEntity);
            verify(transaction).commit();
        }

        @Test
        @DisplayName("Given exception during delete when delete called then transaction is rolled back")
        void givenExceptionDuringDelete_whenDeleteCalled_thenTransactionIsRolledBack() {
            // Given
            when(entityManager.merge(testEntity)).thenThrow(new RuntimeException("Test exception"));
            when(transaction.isActive()).thenReturn(true);

            // When/Then
            try {
                dao.delete(testEntity);
            } catch (RuntimeException e) {
                // Expected exception
            }

            verify(transaction).begin();
            verify(transaction).rollback();
        }
    }
}
