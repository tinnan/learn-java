package org.example.security.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JwtPayload {
    @JsonAlias("staff_id")
    private String staffId;
    private List<String> permissions;
}
