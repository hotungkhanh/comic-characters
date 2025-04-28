package com.tuka.comiccharacters.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DaoTest {

    @Mock
    private Dao<TestEntity> dao;

    private TestEntity entity;
    private final Long VALID_ID = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        entity = new TestEntity(VALID_ID);
    }

    @Nested
    @DisplayName("save method tests")
    class SaveMethodTests {

        @Test
        @DisplayName("Given valid entity when save called then entity is saved")
        void givenValidEntity_whenSaveCalled_thenEntityIsSaved() {
            // Given
            doNothing().when(dao).save(entity);

            // When
            dao.save(entity);

            // Then
            verify(dao, times(1)).save(entity);
        }
    }

    @Nested
    @DisplayName("findById method tests")
    class FindByIdMethodTests {

        @Test
        @DisplayName("Given valid ID when findById called then correct entity is returned")
        void givenValidId_whenFindByIdCalled_thenCorrectEntityIsReturned() {
            // Given
            when(dao.findById(VALID_ID)).thenReturn(entity);

            // When
            TestEntity result = dao.findById(VALID_ID);

            // Then
            assertNotNull(result, "findById should return an entity");
            assertEquals(entity, result, "findById should return the correct entity");
            verify(dao, times(1)).findById(VALID_ID);
        }

        @Test
        @DisplayName("Given non-existent ID when findById called then null is returned")
        void givenNonExistentId_whenFindByIdCalled_thenNullIsReturned() {
            // Given
            Long nonExistentId = 999L;
            when(dao.findById(nonExistentId)).thenReturn(null);

            // When
            TestEntity result = dao.findById(nonExistentId);

            // Then
            assertNull(result, "findById should return null for non-existent ID");
            verify(dao, times(1)).findById(nonExistentId);
        }
    }

    @Nested
    @DisplayName("findByIdWithDetails method tests")
    class FindByIdWithDetailsMethodTests {

        @Test
        @DisplayName("Given valid ID when findByIdWithDetails called then entity with details is returned")
        void givenValidId_whenFindByIdWithDetailsCalled_thenEntityWithDetailsIsReturned() {
            // Given
            TestEntity entityWithDetails = new TestEntity(VALID_ID);
            entityWithDetails.addDetail("Detail 1");
            when(dao.findByIdWithDetails(VALID_ID)).thenReturn(entityWithDetails);

            // When
            TestEntity result = dao.findByIdWithDetails(VALID_ID);

            // Then
            assertNotNull(result, "findByIdWithDetails should return an entity");
            assertEquals(entityWithDetails, result, "findByIdWithDetails should return entity with details");
            verify(dao, times(1)).findByIdWithDetails(VALID_ID);
        }
    }

    @Nested
    @DisplayName("findAll method tests")
    class FindAllMethodTests {

        @Test
        @DisplayName("When findAll called then all entities are returned")
        void whenFindAllCalled_thenAllEntitiesAreReturned() {
            // Given
            Set<TestEntity> entities = new HashSet<>();
            entities.add(new TestEntity(1L));
            entities.add(new TestEntity(2L));
            when(dao.findAll()).thenReturn(entities);

            // When
            Set<TestEntity> result = dao.findAll();

            // Then
            assertNotNull(result, "findAll should return a non-null set");
            assertEquals(2, result.size(), "findAll should return all entities");
            verify(dao, times(1)).findAll();
        }

        @Test
        @DisplayName("When findAll called with empty database then empty set is returned")
        void whenFindAllCalledWithEmptyDatabase_thenEmptySetIsReturned() {
            // Given
            when(dao.findAll()).thenReturn(new HashSet<>());

            // When
            Set<TestEntity> result = dao.findAll();

            // Then
            assertNotNull(result, "findAll should return a non-null set");
            assertTrue(result.isEmpty(), "findAll should return an empty set for empty database");
            verify(dao, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("delete method tests")
    class DeleteMethodTests {

        @Test
        @DisplayName("Given valid entity when delete called then entity is deleted")
        void givenValidEntity_whenDeleteCalled_thenEntityIsDeleted() {
            // Given
            doNothing().when(dao).delete(entity);

            // When
            dao.delete(entity);

            // Then
            verify(dao, times(1)).delete(entity);
        }
    }

    // Test entity inner class for testing the DAO
    private static class TestEntity {
        private final Long id;
        private final Set<String> details = new HashSet<>();

        public TestEntity(Long id) {
            this.id = id;
        }

        public void addDetail(String detail) {
            details.add(detail);
        }

        public Set<String> getDetails() {
            return details;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestEntity that = (TestEntity) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }
}
