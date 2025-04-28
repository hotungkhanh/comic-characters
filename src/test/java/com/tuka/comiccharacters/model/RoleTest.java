package com.tuka.comiccharacters.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RoleTest {

    @Nested
    @DisplayName("Enum values tests")
    class EnumValuesTests {

        @Test
        @DisplayName("Role enum should have expected number of values")
        void roleShouldHaveExpectedNumberOfValues() {
            // Given/When
            Role[] roles = Role.values();

            // Then
            assertEquals(8, roles.length, "Role enum should have 8 values");
        }

        @Test
        @DisplayName("Role enum should contain all expected values")
        void roleShouldContainAllExpectedValues() {
            // Given/When/Then
            assertNotNull(Role.valueOf("WRITER"), "WRITER should be a valid Role");
            assertNotNull(Role.valueOf("ARTIST"), "ARTIST should be a valid Role");
            assertNotNull(Role.valueOf("PENCILLER"), "PENCILLER should be a valid Role");
            assertNotNull(Role.valueOf("INKER"), "INKER should be a valid Role");
            assertNotNull(Role.valueOf("COLORIST"), "COLORIST should be a valid Role");
            assertNotNull(Role.valueOf("LETTERER"), "LETTERER should be a valid Role");
            assertNotNull(Role.valueOf("COVER_ARTIST"), "COVER_ARTIST should be a valid Role");
            assertNotNull(Role.valueOf("EDITOR"), "EDITOR should be a valid Role");
        }

        @ParameterizedTest
        @EnumSource(Role.class)
        @DisplayName("All enum values should be retrievable by name")
        void allEnumValuesShouldBeRetrievableByName(Role role) {
            // Given
            String roleName = role.name();

            // When
            Role retrievedRole = Role.valueOf(roleName);

            // Then
            assertEquals(role, retrievedRole, "Role retrieved by name should match original role");
        }
    }
}
