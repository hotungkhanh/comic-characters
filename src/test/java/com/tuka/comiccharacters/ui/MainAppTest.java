package com.tuka.comiccharacters.ui;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MainAppTest {

    @Mock
    private JTextField mockTextField;

    private MockedStatic<JOptionPane> mockedStaticJOptionPane;

    @BeforeEach
    void setUp() {
        // Store original UI defaults for restoration later
        // This prevents tests from interfering with each other
        UIManager.put("originalTestFonts", UIManager.getDefaults().clone());
    }

    @AfterEach
    void tearDown() {
        // Restore original UI defaults
        if (UIManager.get("originalTestFonts") != null) {
            UIDefaults originalDefaults = (UIDefaults) UIManager.get("originalTestFonts");
            UIDefaults currentDefaults = UIManager.getDefaults();

            // Restore all the original values
            for (Object key : originalDefaults.keySet()) {
                currentDefaults.put(key, originalDefaults.get(key));
            }

            UIManager.put("originalTestFonts", null);
        }
    }

    @Test
    @DisplayName("Given null font when setGlobalFont is called then IllegalArgumentException should be thrown")
    void givenNullFont_whenSetGlobalFont_thenThrowIllegalArgumentException() throws Exception {
        // Given
        Method setGlobalFontMethod = MainApp.class.getDeclaredMethod("setGlobalFont", Font.class);
        setGlobalFontMethod.setAccessible(true);

        // When & Then
        assertThrows(InvocationTargetException.class, () -> setGlobalFontMethod.invoke(null, (Font) null), "setGlobalFont should throw exception when passed null font");
    }


    @Nested
    @DisplayName("Tests for addPlaceholderText method")
    class AddPlaceholderTextTests {

        private FocusListener captureAddedFocusListener() {
            // Capture the FocusListener that was added to the text field
            ArgumentCaptor<FocusListener> focusListenerCaptor = ArgumentCaptor.forClass(FocusListener.class);
            verify(mockTextField).addFocusListener(focusListenerCaptor.capture());
            return focusListenerCaptor.getValue();
        }

        @Test
        @DisplayName("Given text field and placeholder when addPlaceholderText is called then text field properties should be set correctly")
        void givenTextFieldAndPlaceholder_whenAddPlaceholderText_thenTextFieldPropertiesSetCorrectly() {
            // Given
            String placeholderText = "Enter text here...";

            // When
            MainApp.addPlaceholderText(mockTextField, placeholderText);

            // Then
            verify(mockTextField).setForeground(Color.GRAY);
            verify(mockTextField).setText(placeholderText);
            verify(mockTextField).addFocusListener(any(FocusListener.class));
        }

        @Test
        @DisplayName("Given text field with placeholder when focus gained and text matches placeholder then text cleared and color changed")
        void givenTextFieldWithPlaceholder_whenFocusGainedAndTextMatchesPlaceholder_thenTextClearedAndColorChanged() {
            // Given
            String placeholderText = "Enter text here...";
            MainApp.addPlaceholderText(mockTextField, placeholderText);
            FocusListener listener = captureAddedFocusListener();

            // Mock behavior
            when(mockTextField.getText()).thenReturn(placeholderText);
            FocusEvent mockEvent = mock(FocusEvent.class);

            // When
            listener.focusGained(mockEvent);

            // Then
            verify(mockTextField).setText("");
            verify(mockTextField).setForeground(Color.BLACK);
        }

        @Test
        @DisplayName("Given text field with placeholder when focus gained and text does not match placeholder then no changes made")
        void givenTextFieldWithPlaceholder_whenFocusGainedAndTextDoesNotMatchPlaceholder_thenNoChangesMade() {
            // Given
            String placeholderText = "Enter text here...";
            String userText = "User entered text";
            MainApp.addPlaceholderText(mockTextField, placeholderText);
            FocusListener listener = captureAddedFocusListener();

            // Mock behavior
            when(mockTextField.getText()).thenReturn(userText);
            FocusEvent mockEvent = mock(FocusEvent.class);

            // When
            listener.focusGained(mockEvent);

            // Then
            verify(mockTextField, never()).setText("");
            verify(mockTextField, times(1)).setForeground(Color.GRAY); // Only from initial setup
        }

        @Test
        @DisplayName("Given text field with placeholder when focus lost and text is empty then placeholder restored and color changed")
        void givenTextFieldWithPlaceholder_whenFocusLostAndTextIsEmpty_thenPlaceholderRestoredAndColorChanged() {
            // Given
            String placeholderText = "Enter text here...";
            MainApp.addPlaceholderText(mockTextField, placeholderText);
            FocusListener listener = captureAddedFocusListener();

            // Reset the mock to clear the initial setup interactions
            reset(mockTextField);

            // Mock behavior
            when(mockTextField.getText()).thenReturn("");
            FocusEvent mockEvent = mock(FocusEvent.class);

            // When
            listener.focusLost(mockEvent);

            // Then
            verify(mockTextField).setText(placeholderText);
            verify(mockTextField).setForeground(Color.GRAY);
        }

        @Test
        @DisplayName("Given text field with placeholder when focus lost and text is not empty then no changes made")
        void givenTextFieldWithPlaceholder_whenFocusLostAndTextIsNotEmpty_thenNoChangesMade() {
            // Given
            String placeholderText = "Enter text here...";
            String userText = "User entered text";
            MainApp.addPlaceholderText(mockTextField, placeholderText);
            FocusListener listener = captureAddedFocusListener();

            // Reset the mock to clear the initial setup interactions
            reset(mockTextField);

            // Mock behaviour for the focus test
            when(mockTextField.getText()).thenReturn(userText);
            FocusEvent mockEvent = mock(FocusEvent.class);

            // When
            listener.focusLost(mockEvent);

            // Then
            verify(mockTextField, never()).setText(placeholderText);
            verify(mockTextField, never()).setForeground(any(Color.class)); // No colour changes should happen
        }
    }

    @Nested
    @DisplayName("Tests for dialog methods")
    class DialogMethodsTests {

        @BeforeEach
        void setUp() {
            mockedStaticJOptionPane = mockStatic(JOptionPane.class);
        }

        @AfterEach
        void tearDown() {
            mockedStaticJOptionPane.close();
        }

        @Test
        @DisplayName("Given error message when showError called then JOptionPane.showMessageDialog called with correct parameters")
        void givenErrorMessage_whenShowError_thenJOptionPaneShowMessageDialogCalledWithCorrectParameters() {
            // Given
            String errorMessage = "This is an error message";

            // When
            MainApp.showError(errorMessage);

            // Then
            mockedStaticJOptionPane.verify(() -> JOptionPane.showMessageDialog(isNull(),                 // parent component
                    eq(errorMessage),         // message
                    eq("Input Error"),        // title
                    eq(JOptionPane.ERROR_MESSAGE)  // message type
            ));
        }

        @ParameterizedTest
        @ValueSource(strings = {"Success message", "Operation completed", ""})
        @DisplayName("Given success message when showSuccess called then JOptionPane.showMessageDialog called with correct parameters")
        void givenSuccessMessage_whenShowSuccess_thenJOptionPaneShowMessageDialogCalledWithCorrectParameters(String successMessage) {
            // Given - message from parameter

            // When
            MainApp.showSuccess(successMessage);

            // Then
            mockedStaticJOptionPane.verify(() -> JOptionPane.showMessageDialog(isNull(), eq(successMessage), eq("Success"), eq(JOptionPane.INFORMATION_MESSAGE)));
        }
    }
}
