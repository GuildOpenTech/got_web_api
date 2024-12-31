package org.got.erp.security.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
    @Value("${security.password.min-length:8}")
    private int minLength;

    @Value("${security.password.max-length:32}")
    private int maxLength;

    @Value("${security.password.require-special:true}")
    private boolean requireSpecial;

    @Value("${security.password.require-digit:true}")
    private boolean requireDigit;

    @Value("${security.password.require-uppercase:true}")
    private boolean requireUppercase;

    @Value("${security.password.require-lowercase:true}")
    private boolean requireLowercase;

    private static final Pattern SPECIAL_CHARS = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        boolean isValid = password.length() >= minLength && 
                         password.length() <= maxLength;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                String.format("Password must be between %d and %d characters", minLength, maxLength))
                .addConstraintViolation();
            return false;
        }

        StringBuilder violations = new StringBuilder();

        if (requireSpecial && !SPECIAL_CHARS.matcher(password).find()) {
            violations.append("special character, ");
        }
        if (requireDigit && !DIGIT.matcher(password).find()) {
            violations.append("digit, ");
        }
        if (requireUppercase && !UPPERCASE.matcher(password).find()) {
            violations.append("uppercase letter, ");
        }
        if (requireLowercase && !LOWERCASE.matcher(password).find()) {
            violations.append("lowercase letter, ");
        }

        if (violations.length() > 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Password must contain: " + violations.substring(0, violations.length() - 2))
                .addConstraintViolation();
            return false;
        }

        return true;
    }
}
