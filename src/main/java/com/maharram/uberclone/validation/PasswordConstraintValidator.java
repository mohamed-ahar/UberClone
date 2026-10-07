package com.maharram.uberclone.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    public final String SpecialChars = "!@#$%^&*()_+-=[]{}|;':\\\",./<>?";
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        if(password == null){
            return false;
        }
        boolean hasCapital = password.equals(password.toLowerCase());
        boolean hasLowerCase = password.equals(password.toUpperCase());
        boolean hasSpecial = false;
        for(char c : password.toCharArray()){
            if(SpecialChars.indexOf(c) >=0){
                hasSpecial = true;
                break;
            }
        }
        boolean perfectLength = password.length() >= 8 && password.length() <= 64;
        return !hasCapital && !hasLowerCase && hasSpecial && perfectLength;
    }
}
