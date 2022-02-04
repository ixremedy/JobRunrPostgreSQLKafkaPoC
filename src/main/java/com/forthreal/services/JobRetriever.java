package com.forthreal.services;

import com.forthreal.repository.IRuleRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.StorageProvider;
import org.springframework.kafka.core.KafkaTemplate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.lang.Thread.currentThread;

@AllArgsConstructor()
public class JobRetriever {
    private IRuleRepository ruleRepository;
    private JobScheduler jobScheduler;
    private KafkaTemplate<String,String> kafkaTemplate;
    private StorageProvider storageProvider;

    public boolean rescheduleSampleRuleWithCustomId(UUID taskId, String newTaskName)
    {
        var logger = LogManager.getLogger(JobRetriever.class);

        var localDateTime = LocalDateTime.now().plusMinutes(5);

        logger.warn("{} re-scheduling job {}", currentThread().getStackTrace()[1].getMethodName(), taskId);

        jobScheduler.delete(taskId); // mark the job DELETED
        storageProvider.deletePermanently(taskId); // otherwise after the prev step, it will not reassign the same number

        return jobScheduler.schedule(
                          taskId,
                          localDateTime,
                          () -> kafkaTemplate.send("incomingRule", "message", newTaskName)
                        )
                        .asUUID()
                        .compareTo(taskId) == 0;
    }

    public boolean enqueueSampleRuleWithCustomId(UUID taskId)
    {
        var logger = LogManager.getLogger(JobRetriever.class);

        var localDateTime = LocalDateTime.now().plusMinutes(1);

        logger.warn("{} scheduling job {}", currentThread().getStackTrace()[1].getMethodName(), taskId);

        return jobScheduler.schedule(
                                taskId,
                                localDateTime,
                                () -> kafkaTemplate.send("incomingRule", "message", "custom ID task " + taskId)
                              )
                           .asUUID()
                           .compareTo(taskId) == 0;
    }

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
                    return jobScheduler.schedule(localDateTime, () -> kafkaTemplate.send("incomingRule", "message", rule.getDescription()));
                } )
                .map( id -> {
                    logger.warn("{} jobId {}", currentThread().getStackTrace()[1].getMethodName(), id);
                    return 1;
                })
                .reduce( Integer::sum )
                .orElse(0);
    }
}
