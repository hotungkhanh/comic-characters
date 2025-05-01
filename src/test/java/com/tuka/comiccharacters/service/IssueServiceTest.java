package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.model.Issue;
import com.tuka.comiccharacters.model.Series;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IssueServiceTest {

    private final IssueService service = new IssueService();

    @Nested
    @DisplayName("validateEntity method tests")
    class ValidateEntityTests {

        @Test
        @DisplayName("Given valid issue when validateEntity called then no exception is thrown")
        void givenValidIssue_whenValidateEntityCalled_thenNoExceptionThrown() {
            Issue issue = new Issue();
            issue.setSeries(new Series());
            issue.setIssueNumber(new BigDecimal("1.00"));
            issue.setPriceUsd(new BigDecimal("5.99"));
            issue.setAnnual(false);

            assertDoesNotThrow(() -> service.validateEntity(issue));
        }

        @Test
        @DisplayName("Given null issue when validateEntity called then IllegalArgumentException is thrown")
        void givenNullIssue_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(null));
        }

        @Test
        @DisplayName("Given null series when validateEntity called then IllegalArgumentException is thrown")
        void givenNullSeries_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            Issue issue = new Issue();
            issue.setSeries(null);
            issue.setIssueNumber(BigDecimal.ONE);
            issue.setAnnual(true);

            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(issue));
        }

        @Test
        @DisplayName("Given null issueNumber when validateEntity called then IllegalArgumentException is thrown")
        void givenNullIssueNumber_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            Issue issue = new Issue();
            issue.setSeries(new Series());
            issue.setIssueNumber(null);
            issue.setAnnual(true);

            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(issue));
        }

        @Test
        @DisplayName("Given issueNumber over 999999.99 when validateEntity called then IllegalArgumentException is thrown")
        void givenLargeIssueNumber_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            Issue issue = new Issue();
            issue.setSeries(new Series());
            issue.setIssueNumber(new BigDecimal("1000000.00"));
            issue.setAnnual(true);

            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(issue));
        }

        @Test
        @DisplayName("Given negative issueNumber within limit when validateEntity called then no exception is thrown")
        void givenNegativeIssueNumberWithinLimit_whenValidateEntityCalled_thenNoExceptionThrown() {
            Issue issue = new Issue();
            issue.setSeries(new Series());
            issue.setIssueNumber(new BigDecimal("-999999.99"));
            issue.setAnnual(false);

            assertDoesNotThrow(() -> service.validateEntity(issue));
        }

        @Test
        @DisplayName("Given price over 9999.99 when validateEntity called then IllegalArgumentException is thrown")
        void givenLargePrice_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            Issue issue = new Issue();
            issue.setSeries(new Series());
            issue.setIssueNumber(BigDecimal.ONE);
            issue.setPriceUsd(new BigDecimal("10000.00"));
            issue.setAnnual(true);

            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(issue));
        }

        @Test
        @DisplayName("Given null annual flag when validateEntity called then IllegalArgumentException is thrown")
        void givenNullAnnualFlag_whenValidateEntityCalled_thenIllegalArgumentExceptionThrown() {
            Issue issue = new Issue();
            issue.setSeries(new Series());
            issue.setIssueNumber(BigDecimal.ONE);
            issue.setAnnual(null);

            assertThrows(IllegalArgumentException.class, () -> service.validateEntity(issue));
        }

        @Test
        @DisplayName("Given null price when validateEntity called then no exception is thrown")
        void givenNullPrice_whenValidateEntityCalled_thenNoExceptionThrown() {
            Issue issue = new Issue();
            issue.setSeries(new Series());
            issue.setIssueNumber(new BigDecimal("3.00"));
            issue.setPriceUsd(null);
            issue.setAnnual(false);

            assertDoesNotThrow(() -> service.validateEntity(issue));
        }
    }
}
