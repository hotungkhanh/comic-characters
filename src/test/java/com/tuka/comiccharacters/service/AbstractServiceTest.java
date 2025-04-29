package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.Dao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractServiceTest {

    @Mock
    private Dao<TestEntity> dao;
    private TestService service;
    private TestEntity testEntity;
    private Long testId;

    @BeforeEach
    void setUp() {
        service = new TestService(dao);
        testId = 1L;
        testEntity = new TestEntity(testId, "Test Entity");
    }

    // Test entity class
    static class TestEntity {
        private Long id;
        private final String name;

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    // Test implementation of AbstractService
    static class TestService extends AbstractService<TestEntity> {
        public TestService(Dao<TestEntity> dao) {
            super(dao);
        }

        @Override
        protected void validateEntity(TestEntity entity) {
            if (entity == null || entity.getId() == null) {
                throw new IllegalArgumentException("Entity is invalid");
            }
        }
    }

    @Nested
    @DisplayName("getAllEntities method tests")
    class GetAllEntitiesMethodTests {
        @Test
        @DisplayName("When getAllEntities called then dao.findAll is called and its result is returned")
        void whenGetAllEntitiesCalled_thenDaoFindAllIsCalledAndItsResultIsReturned() {
            // Given
            Set<TestEntity> expectedEntities = new HashSet<>();
            expectedEntities.add(testEntity);
            when(dao.findAll()).thenReturn(expectedEntities);

            // When
            Set<TestEntity> actualEntities = service.getAllEntities();

            // Then
            assertSame(expectedEntities, actualEntities);
            verify(dao).findAll();
        }
    }

    @Nested
    @DisplayName("getById method tests")
    class GetByIdMethodTests {
        @Test
        @DisplayName("Given valid ID when getById called then dao.findById is called and its result is returned")
        void givenValidId_whenGetByIdCalled_thenDaoFindByIdIsCalledAndItsResultIsReturned() {
            // Given
            when(dao.findById(testId)).thenReturn(testEntity);

            // When
            TestEntity actualEntity = service.getById(testId);

            // Then
            assertSame(testEntity, actualEntity);
            verify(dao).findById(testId);
        }

        @Test
        @DisplayName("Given invalid ID when getById called then IllegalArgumentException is thrown")
        void givenInvalidId_whenGetByIdCalled_thenIllegalArgumentExceptionIsThrown() {
            // Given
            Long invalidId = -1L;

            // When/Then
            assertThrows(IllegalArgumentException.class, () -> service.getById(invalidId));
        }
    }

    @Nested
    @DisplayName("getByIdWithDetails method tests")
    class GetByIdWithDetailsMethodTests {
        @Test
        @DisplayName("Given valid ID when getByIdWithDetails called then dao.findByIdWithDetails is called")
        void givenValidId_whenGetByIdWithDetailsCalled_thenDaoFindByIdWithDetailsIsCalled() {
            // Given
            when(dao.findByIdWithDetails(testId)).thenReturn(testEntity);

            // When
            TestEntity result = service.getByIdWithDetails(testId);

            // Then
            assertSame(testEntity, result);
            verify(dao).findByIdWithDetails(testId);
        }

        @Test
        @DisplayName("Given invalid ID when getByIdWithDetails called then IllegalArgumentException is thrown")
        void givenInvalidId_whenGetByIdWithDetailsCalled_thenIllegalArgumentExceptionIsThrown() {
            // Given
            Long invalidId = -1L;

            // When/Then
            assertThrows(IllegalArgumentException.class, () -> service.getByIdWithDetails(invalidId));
        }
    }

    @Nested
    @DisplayName("save method tests")
    class SaveMethodTests {
        @Test
        @DisplayName("Given valid entity when save called then dao.save is called")
        void givenValidEntity_whenSaveCalled_thenDaoSaveIsCalled() {
            // When
            service.save(testEntity);

            // Then
            verify(dao).save(testEntity);
        }

        @Test
        @DisplayName("Given invalid entity when save called then IllegalArgumentException is thrown")
        void givenInvalidEntity_whenSaveCalled_thenIllegalArgumentExceptionIsThrown() {
            // Given
            TestEntity invalidEntity = new TestEntity(null, "Invalid");

            // When/Then
            assertThrows(IllegalArgumentException.class, () -> service.save(invalidEntity));
        }
    }

    @Nested
    @DisplayName("delete method tests")
    class DeleteMethodTests {
        @Test
        @DisplayName("Given valid ID when delete called then dao.delete is called with entity returned by dao.findById")
        void givenValidId_whenDeleteCalled_thenDaoDeleteIsCalledWithEntityReturnedByDaoFindById() {
            // Given
            when(dao.findById(testId)).thenReturn(testEntity);

            // When
            service.delete(testId);

            // Then
            verify(dao).findById(testId);
            verify(dao).delete(testEntity);
        }

        @Test
        @DisplayName("Given invalid ID when delete called then IllegalArgumentException is thrown")
        void givenInvalidId_whenDeleteCalled_thenIllegalArgumentExceptionIsThrown() {
            // Given
            Long invalidId = -1L;

            // When/Then
            assertThrows(IllegalArgumentException.class, () -> service.delete(invalidId));
        }

        @Test
        @DisplayName("Given valid ID when delete called and dao.findById returns null, dao.delete is not called")
        void givenValidId_whenDeleteCalledAndDaoFindByIdReturnsNull_daoDeleteIsNotCalled() {
            // Given
            when(dao.findById(testId)).thenReturn(null);

            // When
            service.delete(testId);

            // Then
            verify(dao).findById(testId);
            verify(dao, never()).delete(any());
        }
    }
}
