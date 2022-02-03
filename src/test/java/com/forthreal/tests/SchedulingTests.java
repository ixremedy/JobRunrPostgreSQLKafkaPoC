package com.forthreal.tests;

import com.forthreal.ApplicationSettings;
import com.forthreal.entities.Rule;
import com.forthreal.repository.IRuleRepository;
import com.forthreal.services.JobRetriever;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
    @DisplayName("check if we can insert new rules")
    public void ruleInsertionTest()
    {
        var dayNumber = LocalDate.now().getDayOfMonth();
        var time1 = Time.valueOf(LocalTime.now().plus(1, ChronoUnit.MINUTES));

        var rule1 = new Rule(time1, dayNumber, "First rule");
        var rule2 = new Rule(time1, dayNumber, "Second rule");
        var rule3 = new Rule(time1, dayNumber, "Third rule");
        var rule4 = new Rule(time1, dayNumber, "Fourth rule");
        var rule5 = new Rule(time1, dayNumber, "Fifth rule");

        assertTrue(ruleRepository.saveAndFlush(rule1) != null
                        && ruleRepository.saveAndFlush(rule2) != null
                        && ruleRepository.saveAndFlush(rule3) != null
                        && ruleRepository.saveAndFlush(rule4) != null
                        && ruleRepository.saveAndFlush(rule5) != null
                      );
    }

    @Test
    @Order(1)
    @DisplayName("check if we can can enqueue the new rule related jobs")
    public void enqueueRuleJobsTest()
    {
        assertEquals(5, jobRetriever.enqueueFromDb());
    }

}
