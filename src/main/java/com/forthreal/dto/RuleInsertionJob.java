package com.forthreal.dto;

import com.forthreal.handlers.RuleInsertionJobHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jobrunr.jobs.lambdas.JobRequest;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RuleInsertionJob implements JobRequest {
    private String description;
    private LocalDateTime executionTime;

    @Override
    public Class<RuleInsertionJobHandler> getJobRequestHandler() {
        return RuleInsertionJobHandler.class;
    }
}
