package com.forthreal;

import org.springframework.boot.SpringApplication;

public class AppMain {
    public static void main(String[] args)
    {
        var app = new SpringApplication(AppMain.class);

        app.run(args);
    }

}
