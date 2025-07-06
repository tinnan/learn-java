package com.github.tinnan.jobrunner.entity.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tinnan.jobrunner.entity.JobParam;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

@Converter(autoApply = false)
public class JsonToJobParamConverter implements AttributeConverter<JobParam, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(JobParam attribute) {
        if (attribute == null) {
            return null;
        }
        return MAPPER.writeValueAsString(attribute);
    }

    @SneakyThrows
    @Override
    public JobParam convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return null;
        }
        return MAPPER.readValue(dbData, JobParam.class);
    }
}
