package com.forthreal.services;

import org.springframework.stereotype.Service;

@Service
public class SchedulingService {
    public void doServiceTask(String name)
    {
        System.out.println("Hello world: " + name);
    }
}
