package com.example.demo.validation.marker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.groups.Default;
import java.util.Set;
import lombok.Data;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GroupValidationTest {
    private static Validator validator;

    @BeforeAll
    public static void beforeAll() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void givenAllUserGroup_whenValidate_shouldValidateFieldWithAnnotationWithGroupAllUser() {
        User user = new User();

        Set<ConstraintViolation<User>> violations = validator.validate(user, AllUser.class);
        assertEquals(1, violations.size());

        assertViolation(violations, "name", "must not be blank");
    }

    @Test
    void givenRegularUserGroup_whenValidate_shouldValidateFieldWithAnnotationWithGroupRegularUser() {
        User user = new User();

        Set<ConstraintViolation<User>> violations = validator.validate(user, RegularUser.class);
        assertEquals(2, violations.size());

        assertViolation(violations, "email", "must not be blank");
        assertViolation(violations, "username", "must not be blank");
    }

    @Test
    void givenRegularUserAndAdminUserGroup_whenValidate_shouldValidateFieldWithAnnotationWithGroupRegularUserAndAdminUser() {
        User user = new User();

        Set<ConstraintViolation<User>> violations = validator.validate(user, RegularUser.class, AdminUser.class);
        assertEquals(2, violations.size());

        assertViolation(violations, "username", "must not be blank");
    }

    @Test
    void givenOtherUserGroup_whenValidate_shouldNotValidateAnyField() {
        User user = new User();

        Set<ConstraintViolation<User>> violations = validator.validate(user, OtherUser.class);
        assertEquals(0, violations.size());
    }

    @Test
    void givenOtherUserAndRegularUserGroup_whenValidate_shouldValidateFieldWithAnnotationWithGroupRegularUser() {
        User user = new User();

        Set<ConstraintViolation<User>> violations = validator.validate(user, OtherUser.class, RegularUser.class);
        assertEquals(2, violations.size());

        assertViolation(violations, "email", "must not be blank");
        assertViolation(violations, "username", "must not be blank");
    }

    @Test
    void givenDefaultAndAllUserGroup_whenValidate_shouldValidateFieldWithAnnotationWithGroupAllUserAndWithoutAnyGroup() {
        User user = new User();

        Set<ConstraintViolation<User>> violations = validator.validate(user, Default.class, AllUser.class);
        assertEquals(2, violations.size());

        assertViolation(violations, "name", "must not be blank");
        assertViolation(violations, "alias", "must not be blank");
    }

    @Test
    void givenDefaultGroup_whenValidate_shouldValidateFieldWithAnnotationWithoutAnyGroup() {
        User user = new User();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());

        assertViolation(violations, "alias", "must not be blank");
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

    @Data
    private static class User {
        @NotBlank(groups = AllUser.class)
        private String name;

        @NotBlank(groups = RegularUser.class)
        private String email;

        @NotBlank(groups = {RegularUser.class, AdminUser.class})
        private String username;

        @NotBlank
        private String alias;
    }
}