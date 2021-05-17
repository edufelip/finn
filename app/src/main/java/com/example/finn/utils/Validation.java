package com.example.finn.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class Validation {
    public static boolean isEmailValid(CharSequence target) {
            return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
