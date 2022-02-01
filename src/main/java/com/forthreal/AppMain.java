package com.forthreal;

import com.forthreal.services.JobRetriever;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class AppMain {
    public static void main(String[] args)
    {
        var appFactory = new AnnotationConfigApplicationContext(ApplicationSettings.class);
        var app = new SpringApplication(AppMain.class);

        app.run(args);

        var jobRetriever = appFactory.getBean(JobRetriever.class);

        jobRetriever.enqueueFromDb();
    }

}
