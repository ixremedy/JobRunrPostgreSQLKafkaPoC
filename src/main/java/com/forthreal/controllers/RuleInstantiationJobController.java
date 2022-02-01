package com.forthreal.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Controller
public class RuleInstantiationJobController {
    private final Logger logger = LogManager.getLogger(RuleInstantiationJobController.class);

    @Lazy @Autowired
    private JobScheduler jobScheduler;

    /*
    @PostConstruct
    @Job(name = "firstJob")
    public void executeFirstJob()
    {
        var futureTime = LocalDateTime.now().plus(5, ChronoUnit.SECONDS).atZone(ZoneId.of("Europe/Kiev"));
        jobScheduler.schedule(futureTime, () -> System.out.println("Pryvit"));
    }
     */

}
