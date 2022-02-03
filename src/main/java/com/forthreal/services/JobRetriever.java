package com.forthreal.services;

import com.forthreal.dto.RuleInsertionJob;
import com.forthreal.repository.IRuleRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.jobrunr.scheduling.BackgroundJobRequest;
import org.jobrunr.scheduling.JobScheduler;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.lang.Thread.currentThread;

@AllArgsConstructor()
public class JobRetriever {
    private IRuleRepository ruleRepository;
    private JobScheduler jobScheduler;

    public long enqueueFromDb()
    {
        var dayNumber = LocalDate.now().getDayOfMonth();
        var logger = LogManager.getLogger(JobRetriever.class);

        return ruleRepository
                .findAllByDayNumber(dayNumber)
                .stream()
                .map( rule -> {
                    var time = rule.getLaunchTime();
                    var hours = time.getHours();
                    var minutes = time.getMinutes();
                    var localDateTime = LocalDateTime.now().withHour(hours).withMinute(minutes);
                    var ruleInsertionJob = new RuleInsertionJob(rule.getDescription(),localDateTime);
                    return ruleInsertionJob;
                } )
                .map( BackgroundJobRequest::enqueue )
                .map( id -> {
                    logger.warn("{} jobId {}", currentThread().getStackTrace()[1].getMethodName(), id);
                    return 1;
                })
                .reduce( Integer::sum )
                .orElse(0);
    }
}
