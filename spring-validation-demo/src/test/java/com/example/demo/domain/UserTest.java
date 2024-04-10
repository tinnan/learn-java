package com.example.demo.domain;

import com.example.demo.validation.marker.AdminUser;
import com.example.demo.validation.marker.AllUser;
import com.example.demo.validation.marker.RegularUser;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringJUnitConfig
class UserTest {
    private static Validator validator;

    @BeforeAll
    public static void beforeAll() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    public void givenAdminLevelUser_whenInvalidInput_thenExpectErrors() {
        User user = new User();
        user.setName("");
        user.setEmail("john.d");
        user.setAge(24);
        user.setAgreement(false);

        Set<ConstraintViolation<User>> violations = validator.validate(user, AdminUser.class, AllUser.class);
        assertEquals(4, violations.size());

        assertViolation(violations, "name", "{validation.user.notBlank}");
        assertViolation(violations, "agreement", "{validation.agreement.assertTrue}");
        assertViolation(violations, "email", "{validation.email.email}");
        assertViolation(violations, "age", "{validation.age.between.admin}");
    }

    @Test
    public void givenRegularUser_whenInvalidInput_thenExpectErrors() {
        User user = new User();
        user.setName("");
        user.setEmail("john.d");
        user.setAge(9);
        user.setAgreement(false);

        Set<ConstraintViolation<User>> violations = validator.validate(user, RegularUser.class, AllUser.class);
        assertEquals(3, violations.size());

        assertViolation(violations, "name", "{validation.user.notBlank}");
        assertViolation(violations, "email", "{validation.email.email}");
        assertViolation(violations, "age", "{validation.age.between}");
    }

    private void assertViolation(Set<ConstraintViolation<User>> violations, String expectedField,
                                 String expectedMessage) {
        for (ConstraintViolation<User> action : violations) {
            if (expectedField.equals(action.getPropertyPath()
                    .toString())) {
                assertEquals(expectedMessage, action.getMessage());
                return;
            }
        }
        fail("Expected field [" + expectedField + "] to be in violation list.");
    }
}