package com.fareye.sphere.d.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Component
@ApplicationScope
public class PasswordUtils {
    private static final int REQUIRED_DIGITS =2;
    private static final int REQUIRED_SPECIAL_CHARACTERS =2;
    private static final int REQUIRED_PASSWORD_LENGTH =8;
    public boolean validatePassword(String password){
        if (password==null || password.length()< REQUIRED_PASSWORD_LENGTH) return false;
        int digits=0;
        int specialCharacters=0;
        for(char c:password.toCharArray()){
            if (Character.isDigit(c)) digits++;
            else if (!Character.isLetterOrDigit(c)) specialCharacters++;
            if (digits>= REQUIRED_DIGITS && specialCharacters>= REQUIRED_SPECIAL_CHARACTERS) return true;
        }
        return false;
    }
}