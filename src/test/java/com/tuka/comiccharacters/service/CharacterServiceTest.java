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
        @DisplayName("Given null character when validateEntity called then IllegalArgumentException is thrown")
        void givenNullCharacter_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(null));
        }

        @Test
        @DisplayName("Given null name when validateEntity called then IllegalArgumentException is thrown")
        void givenNullName_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            ComicCharacter character = new ComicCharacter();
            character.setName(null);
            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(character));
        }

        @Test
        @DisplayName("Given blank name when validateEntity called then IllegalArgumentException is thrown")
        void givenBlankName_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            ComicCharacter character = new ComicCharacter();
            character.setName("   ");
            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(character));
        }

        @Test
        @DisplayName("Given name over 255 characters when validateEntity called then IllegalArgumentException is thrown")
        void givenLongName_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            String longName = "a".repeat(256);
            ComicCharacter character = new ComicCharacter();
            character.setName(longName);
            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(character));
        }

        @Test
        @DisplayName("Given alias over 255 characters when validateEntity called then IllegalArgumentException is thrown")
        void givenLongAlias_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            ComicCharacter character = new ComicCharacter("Superman");
            character.setAlias("a".repeat(256));
            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(character));
        }

        @Test
        @DisplayName("Given overview over 1000 characters when validateEntity called then IllegalArgumentException is thrown")
        void givenLongOverview_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            ComicCharacter character = new ComicCharacter("Wonder Woman");
            character.setOverview("a".repeat(1001));
            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(character));
        }

        @Test
        @DisplayName("Given imageUrl over 2083 characters when validateEntity called then IllegalArgumentException is thrown")
        void givenLongImageUrl_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            ComicCharacter character = new ComicCharacter("Flash");
            character.setImageUrl("a".repeat(2084));
            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(character));
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
