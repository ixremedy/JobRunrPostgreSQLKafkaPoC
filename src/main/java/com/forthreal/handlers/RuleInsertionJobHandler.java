package com.forthreal.handlers;

import com.forthreal.dto.RuleInsertionJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.springframework.stereotype.Component;
import static java.lang.Thread.currentThread;

@Component
public class RuleInsertionJobHandler implements JobRequestHandler<RuleInsertionJob> {
    private Logger logger = LogManager.getLogger(RuleInsertionJobHandler.class);

    @Override
    public void run(RuleInsertionJob jobRequest) throws Exception {
        logger.info("{} Caught a job request {}",
                    currentThread().getStackTrace()[1].getMethodName(),
                    jobRequest.getDescription()
                );
    }

    @Override
    public JobContext jobContext() {
        return JobRequestHandler.super.jobContext();
    }
}
