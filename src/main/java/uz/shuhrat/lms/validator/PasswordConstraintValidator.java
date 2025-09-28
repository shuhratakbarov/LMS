package uz.shuhrat.lms.validator;

import org.passay.*;
import uz.shuhrat.lms.annotation.ValidPassword;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword arg0) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8, 30),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 3, false),
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 3, false),
                new IllegalSequenceRule(EnglishSequenceData.USQwerty, 3, false),
                new WhitespaceRule()
        ));

        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        List<String> messages = validator.getMessages(result);
        String joinedMessages = String.join(", ", messages);

        context.buildConstraintViolationWithTemplate(joinedMessages)
                .addConstraintViolation();
        return false;
    }
}