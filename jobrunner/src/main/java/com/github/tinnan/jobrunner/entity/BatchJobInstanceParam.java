package com.github.tinnan.jobrunner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = BatchJobInstanceParam.TABLE_NAME)
@Entity
public class BatchJobInstanceParam {

    public static final String TABLE_NAME = "BATCH_JOB_INSTANCE_PARAM";

    public static final String COLUMN_JOB_INSTANCE_ID = "JOB_INSTANCE_ID";
    public static final String COLUMN_SERIALIZED_PARAM = "SERIALIZED_PARAM";

    @Id
    @Column(name = COLUMN_JOB_INSTANCE_ID)
    private Long jobInstanceId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = COLUMN_SERIALIZED_PARAM)
    private StartJobParam serializedParam;
}
