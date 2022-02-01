package com.forthreal.services;

import com.forthreal.repository.IRuleRepository;
import lombok.AllArgsConstructor;
import org.jobrunr.scheduling.JobScheduler;

@AllArgsConstructor
public class JobRetriever {
    private IRuleRepository ruleRepository;
    private JobScheduler jobScheduler;

    public void enqueueFromDb()
    {
        ruleRepository.findAll();
    }
}
