package com.forthreal;

import com.forthreal.services.JobRetriever;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class AppMain {

    public static void main(String[] args)
    {
        var app = new SpringApplication(AppMain.class);

        var applicationContext = app.run(args);

        var jobRetriever = applicationContext.getBean(JobRetriever.class);
        jobRetriever.enqueueFromDb();
    }

}
