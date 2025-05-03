package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.model.ComicCharacter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterServiceTest {

    private final CharacterService service = new CharacterService();

    @Nested
    @DisplayName("validateEntity method tests")
    class ValidateEntityTests {

        @Test
        @DisplayName("Given valid character when validateEntity called then no exception is thrown")
        void givenValidCharacter_whenValidateEntityCalled_thenNoExceptionThrown() {
            ComicCharacter character = new ComicCharacter("Batman");
            assertDoesNotThrow(() -> service.validateEntity(character));
        }

        @Test
        @DisplayName("Given null character when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenNullCharacter_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(null));
            assertEquals("Character cannot be null.", exception.getMessage());
        }

        @Test
        @DisplayName("Given null name when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenNullName_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            ComicCharacter character = new ComicCharacter();
            character.setName(null);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(character));
            assertEquals("Character name cannot be empty.", exception.getMessage());
        }

        @Test
        @DisplayName("Given blank name when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenBlankName_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            ComicCharacter character = new ComicCharacter();
            character.setName("   ");
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(character));
            assertEquals("Character name cannot be empty.", exception.getMessage());
        }

        @Test
        @DisplayName("Given name over 255 characters when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenLongName_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            String longName = "a".repeat(256);
            ComicCharacter character = new ComicCharacter();
            character.setName(longName);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(character));
            assertEquals("Character name must be 255 characters or fewer.", exception.getMessage());
        }

        @Test
        @DisplayName("Given alias over 255 characters when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenLongAlias_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            ComicCharacter character = new ComicCharacter("Superman");
            character.setAlias("a".repeat(256));
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(character));
            assertEquals("Character alias must be 255 characters or fewer.", exception.getMessage());
        }

        @Test
        @DisplayName("Given overview over 3000 characters when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenLongOverview_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            ComicCharacter character = new ComicCharacter("Wonder Woman");
            character.setOverview("a".repeat(3001));
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(character));
            assertEquals("Character overview must be 3000 characters or fewer.", exception.getMessage());
        }

        @Test
        @DisplayName("Given imageUrl over 2083 characters when validateEntity called then IllegalArgumentException with correct message is thrown")
        void givenLongImageUrl_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            ComicCharacter character = new ComicCharacter("Flash");
            character.setImageUrl("a".repeat(2084));
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateEntity(character));
            assertEquals("Character image URL must be 2083 characters or fewer.", exception.getMessage());
        }

        @Test
        @DisplayName("Given name with leading/trailing whitespace when validateEntity called then name is trimmed")
        void givenWhitespaceName_whenValidateEntityCalled_thenNameIsTrimmed() {
            ComicCharacter character = new ComicCharacter("   Green Lantern   ");
            service.validateEntity(character);
            assertEquals("Green Lantern", character.getName());
        }
    }
}
