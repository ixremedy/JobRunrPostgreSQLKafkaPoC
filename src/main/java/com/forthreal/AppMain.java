package com.forthreal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppMain {
    public static void main(String[] args)
    {
        var app = new SpringApplication(AppMain.class);

        app.run(args);
    }

}
