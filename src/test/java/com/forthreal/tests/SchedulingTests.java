package com.forthreal.tests;

import com.forthreal.ApplicationSettings;
import com.forthreal.repository.IRuleRepository;
import com.forthreal.services.JobRetriever;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ApplicationSettings.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableJpaRepositories(basePackages = "com.forthreal.repository")
public class SchedulingTests {
    @Lazy @Autowired
    private JobRetriever jobRetriever;
    @Lazy @Autowired
    private IRuleRepository ruleRepository;

    @Test
    @Order(0)
    @DisplayName("check if we can insert new rules when they don't exist")
    public void ruleInsertionTest()
    {
        assertTrue(ruleRepository.findAll().isEmpty());
    }
}
