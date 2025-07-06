package com.github.tinnan.jobrunner.entity;

import com.github.tinnan.jobrunner.entity.converter.JsonToJobParamConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = BatchJobInstanceParam.TABLE_NAME)
@Entity
public class BatchJobInstanceParam {

    public static final String TABLE_NAME = "BATCH_JOB_INSTANCE_PARAM";

    public static final String COLUMN_JOB_INSTANCE_ID = "JOB_INSTANCE_ID";
    public static final String COLUMN_SERIALIZED_PARAM = "SERIALIZED_PARAM";

    @Id
    @Column(name = COLUMN_JOB_INSTANCE_ID)
    private Long jobInstanceId;

    @Convert(converter = JsonToJobParamConverter.class)
    @Column(name = COLUMN_SERIALIZED_PARAM)
    private JobParam serializedParam;
}
