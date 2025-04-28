package com.tuka.comiccharacters.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IssueCreatorTest {

    private static final Role SAMPLE_ROLE = Role.WRITER;

    private IssueCreator issueCreator;
    private Issue issue;
    private Creator creator;

    @BeforeEach
    void setUp() {
        issueCreator = new IssueCreator();
        issue = new Issue();
        creator = new Creator("Test Creator");
        issueCreator.setIssue(issue);
        issueCreator.setCreator(creator);
        issueCreator.setRoles(EnumSet.of(SAMPLE_ROLE));
    }

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("Given no parameters when default constructor called then IssueCreator is created with null properties and empty roles")
        void givenNoParameters_whenDefaultConstructorCalled_thenIssueCreatorIsCreatedWithNullPropertiesAndEmptyRoles() {
            // When
            IssueCreator newIssueCreator = new IssueCreator();

            // Then
            assertNull(newIssueCreator.getId(), "Id should be null");
            assertNull(newIssueCreator.getIssue(), "Issue should be null");
            assertNull(newIssueCreator.getCreator(), "Creator should be null");
            assertNotNull(newIssueCreator.getRoles(), "Roles should be initialized");
            assertTrue(newIssueCreator.getRoles().isEmpty(), "Roles should be empty");
        }
    }

    @Nested
    @DisplayName("Getter and setter tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Given IssueCreator with Issue set when getIssue called then correct Issue is returned")
        void givenIssueCreatorWithIssueSet_whenGetIssueCalled_thenCorrectIssueIsReturned() {
            // Given - issue already set in setUp

            // When
            Issue actualIssue = issueCreator.getIssue();

            // Then
            assertEquals(issue, actualIssue, "getIssue should return the set Issue");
        }

        @Test
        @DisplayName("Given IssueCreator with Creator set when getCreator called then correct Creator is returned")
        void givenIssueCreatorWithCreatorSet_whenGetCreatorCalled_thenCorrectCreatorIsReturned() {
            // Given - creator already set in setUp

            // When
            Creator actualCreator = issueCreator.getCreator();

            // Then
            assertEquals(creator, actualCreator, "getCreator should return the set Creator");
        }

        @Test
        @DisplayName("Given IssueCreator with Roles set when getRoles called then correct Roles are returned")
        void givenIssueCreatorWithRolesSet_whenGetRolesCalled_thenCorrectRolesAreReturned() {
            // Given - roles already set in setUp

            // When
            Set<Role> actualRoles = issueCreator.getRoles();

            // Then
            assertTrue(actualRoles.contains(SAMPLE_ROLE), "Roles should contain the assigned role");
            assertEquals(1, actualRoles.size(), "Roles size should be 1");
        }

        @ParameterizedTest
        @EnumSource(Role.class)
        @DisplayName("Given different Roles when setRoles called then roles are updated correctly")
        void givenDifferentRoles_whenSetRolesCalled_thenRolesAreUpdatedCorrectly(Role role) {
            // Given
            Set<Role> newRoles = EnumSet.of(role);

            // When
            issueCreator.setRoles(newRoles);

            // Then
            assertEquals(newRoles, issueCreator.getRoles(), "setRoles should update the roles property");
        }
    }
}
