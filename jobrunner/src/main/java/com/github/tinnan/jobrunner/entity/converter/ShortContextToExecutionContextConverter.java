package com.github.tinnan.jobrunner.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.repository.dao.DefaultExecutionContextSerializer;
import org.springframework.batch.item.ExecutionContext;

@Converter(autoApply = false)
public class ShortContextToExecutionContextConverter extends DefaultExecutionContextSerializer implements
    AttributeConverter<ExecutionContext, String> {

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(ExecutionContext attribute) {
        if (attribute == null) {
            return null;
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            serialize(attribute.toMap(), outputStream);
            return outputStream.toString();
        }
    }

    @SneakyThrows
    @Override
    public ExecutionContext convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return null;
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(dbData.getBytes())) {
            return new ExecutionContext(deserialize(inputStream));
        }
    }
}
