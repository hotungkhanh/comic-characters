package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.model.Creator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreatorServiceTest {

    private final CreatorService service = new CreatorService();

    @Nested
    @DisplayName("validateEntity method tests")
    class ValidateEntityTests {

        @Test
        @DisplayName("Given valid creator when validateEntity called then no exception is thrown")
        void givenValidCreator_whenValidateEntityCalled_thenNoExceptionThrown() {
            // Given
            Creator validCreator = new Creator("Alan Moore", "Famous comic writer", "http://example.com/image.jpg");

            // When/Then
            assertDoesNotThrow(() -> service.validateEntity(validCreator));
        }

        @Test
        @DisplayName("Given null creator when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenNullCreator_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            // When
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(null));

            // Then
            assertEquals("Creator cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Given null name when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenNullName_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            // Given
            Creator creator = new Creator();
            creator.setName(null);

            // When
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(creator));

            // Then
            assertEquals("Creator name cannot be null or blank", exception.getMessage());
        }

        @Test
        @DisplayName("Given blank name when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenBlankName_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            // Given
            Creator creator = new Creator();
            creator.setName("   ");

            // When
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(creator));

            // Then
            assertEquals("Creator name cannot be null or blank", exception.getMessage());
        }

        @Test
        @DisplayName("Given name over 255 characters when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenLongName_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            // Given
            String longName = "a".repeat(256);
            Creator creator = new Creator();
            creator.setName(longName);

            // When
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(creator));

            // Then
            assertEquals("Creator name must be 255 characters or fewer", exception.getMessage());
        }

        @Test
        @DisplayName("Given overview over 3000 characters when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenLongOverview_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            // Given
            Creator creator = new Creator();
            creator.setName("Valid Name");
            creator.setOverview("a".repeat(3001));

            // When
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(creator));

            // Then
            assertEquals("Overview must be 3000 characters or fewer", exception.getMessage());
        }

        @Test
        @DisplayName("Given imageUrl over 2083 characters when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenLongImageUrl_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            // Given
            Creator creator = new Creator();
            creator.setName("Valid Name");
            creator.setImageUrl("http://example.com/" + "a".repeat(2083 - 18 + 1)); // total length > 2083

            // When
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(creator));

            // Then
            assertEquals("Image URL must be 2083 characters or fewer", exception.getMessage());
        }

        @Test
        @DisplayName("Given negative ID when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenNegativeId_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            // Given
            Creator creator = new Creator("Valid Name");
            creator.setId(-5L);

            // When
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(creator));

            // Then
            assertEquals("Invalid creator ID", exception.getMessage());
        }

        @Test
        @DisplayName("Given name with extra whitespace when validateEntity called then name is trimmed")
        void givenWhitespaceName_whenValidateEntityCalled_thenNameIsTrimmed() {
            // Given
            Creator creator = new Creator("  Alan Moore  ");

            // When
            service.validateEntity(creator);

            // Then
            assertEquals("Alan Moore", creator.getName());
        }
    }
}
