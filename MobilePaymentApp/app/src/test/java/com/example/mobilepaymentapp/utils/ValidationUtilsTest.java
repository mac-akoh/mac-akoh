package com.example.mobilepaymentapp.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValidationUtilsTest {

    // --- Tests for isValidEmail ---

    @Test
    public void isValidEmail_correctEmailSimple_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmail("name@example.com"));
    }

    @Test
    public void isValidEmail_correctEmailWithSubdomain_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmail("name@sub.example.com"));
    }

    @Test
    public void isValidEmail_correctEmailWithPlus_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmail("name+tag@example.com"));
    }

    @Test
    public void isValidEmail_correctEmailWithDotInName_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmail("first.last@example.com"));
    }

    @Test
    public void isValidEmail_noAtSign_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail("nameexample.com"));
    }

    @Test
    public void isValidEmail_noDomain_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail("name@"));
    }

    @Test
    public void isValidEmail_noLocalPart_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail("@example.com"));
    }

    @Test
    public void isValidEmail_emptyString_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail(""));
    }

    @Test
    public void isValidEmail_nullInput_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail(null));
    }

    @Test
    public void isValidEmail_domainStartsWithHyphen_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail("name@-example.com"));
    }

    @Test
    public void isValidEmail_domainEndsWithHyphen_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail("name@example-.com"));
    }

    @Test
    public void isValidEmail_domainTldMissing_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail("name@example"));
    }


    // --- Tests for isValidPassword ---

    @Test
    public void isValidPassword_correctPassword_returnsTrue() {
        assertTrue(ValidationUtils.isValidPassword("password123"));
    }

    @Test
    public void isValidPassword_minLengthPassword_returnsTrue() {
        assertTrue(ValidationUtils.isValidPassword("123456"));
    }

    @Test
    public void isValidPassword_tooShort_returnsFalse() {
        assertFalse(ValidationUtils.isValidPassword("12345"));
    }

    @Test
    public void isValidPassword_emptyString_returnsFalse() {
        assertFalse(ValidationUtils.isValidPassword(""));
    }

    @Test
    public void isValidPassword_nullInput_returnsFalse() {
        assertFalse(ValidationUtils.isValidPassword(null));
    }

    // Add more tests if password complexity rules change (e.g., uppercase, numbers, symbols)
}
