package com.example.jpademo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "special_type")
@Entity(name = "special_type")
public class SpecialTypeEntity {

    @Id
    @Column(name = "id")
    private Integer id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "json_column")
    private JsonColumn jsonColumn;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JsonColumn {

        private String header;

        @JsonProperty("description_list")
        private List<String> description;
    }
}
