package com.github.tinnan.jobrunner.model;

import java.util.List;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BatchJobDetail extends BatchJob {

    List<BatchTask> tasks;
}
