package com.example.mobilepaymentapp.utils;

import android.text.TextUtils;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
        "\\@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"
    );

    public static boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        // Add more complex password rules as needed
        // For example, minimum length, uppercase, lowercase, numbers, special characters
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }
}
