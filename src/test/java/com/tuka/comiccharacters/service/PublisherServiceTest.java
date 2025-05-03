package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.model.Publisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PublisherServiceTest {

    private final PublisherService service = new PublisherService();

    @Nested
    @DisplayName("validateEntity method tests")
    class ValidateEntityTests {

        @Test
        @DisplayName("Given valid publisher when validateEntity called then no exception is thrown")
        void givenValidPublisher_whenValidateEntityCalled_thenNoExceptionThrown() {
            // Given
            Publisher publisher = new Publisher("Marvel Comics");

            // When/Then
            assertDoesNotThrow(() -> service.validateEntity(publisher));
        }

        @Test
        @DisplayName("Given null publisher when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenNullPublisher_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            // When
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(null));

            // Then
            assertEquals("Publisher cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Given null name when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenNullName_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            // Given
            Publisher publisher = new Publisher();
            publisher.setName(null);

            // When
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(publisher));

            // Then
            assertEquals("Publisher name cannot be null or blank", exception.getMessage());
        }

        @Test
        @DisplayName("Given blank name when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenBlankName_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            // Given
            Publisher publisher = new Publisher();
            publisher.setName("   ");

            // When
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(publisher));

            // Then
            assertEquals("Publisher name cannot be null or blank", exception.getMessage());
        }

        @Test
        @DisplayName("Given name over 255 characters when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenLongName_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            // Given
            String longName = "a".repeat(256);
            Publisher publisher = new Publisher();
            publisher.setName(longName);

            // When
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(publisher));

            // Then
            assertEquals("Publisher name must be 255 characters or fewer", exception.getMessage());
        }

        @Test
        @DisplayName("Given name with extra whitespace when validateEntity called then name is trimmed")
        void givenWhitespaceName_whenValidateEntityCalled_thenNameIsTrimmed() {
            // Given
            Publisher publisher = new Publisher("   DC Comics   ");

            // When
            service.validateEntity(publisher);

            // Then
            assertEquals("DC Comics", publisher.getName());
        }
    }
}
