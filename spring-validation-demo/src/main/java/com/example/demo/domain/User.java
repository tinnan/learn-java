package com.example.demo.domain;

import com.example.demo.validation.constraints.Between;
import com.example.demo.validation.marker.AdminUser;
import com.example.demo.validation.marker.AllUser;
import com.example.demo.validation.marker.RegularUser;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "{validation.user.notBlank}", groups = AllUser.class)
    private String name;

    @NotBlank(message = "{validation.email.notBlank}", groups = AllUser.class)
    @Email(message = "{validation.email.email}", groups = AllUser.class)
    private String email;

    @Between.List({
            @Between(min = 10, max = 99, message = "{validation.age.between}", groups = RegularUser.class),
            @Between(min = 25, max = 50, message = "{validation.age.between.admin}", groups = AdminUser.class),
    })
    private Integer age;

    @AssertTrue(message = "{validation.agreement.assertTrue}", groups = AdminUser.class)
    private boolean agreement;
}
