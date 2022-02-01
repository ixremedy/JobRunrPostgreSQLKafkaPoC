package com.forthreal.dto;

import com.forthreal.handlers.RuleInsertionJobHandler;
import lombok.Getter;
import lombok.Setter;
import org.jobrunr.jobs.lambdas.JobRequest;
import java.util.UUID;

@Getter
@Setter
public class RuleInsertionJob implements JobRequest {

    public RuleInsertionJob()
    {
        description = "Rule: " + UUID.randomUUID();
    }

    private String description;

    @Override
    public Class<RuleInsertionJobHandler> getJobRequestHandler() {
        return RuleInsertionJobHandler.class;
    }
}
