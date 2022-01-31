package com.forthreal;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.StorageProviderUtils;
import org.jobrunr.storage.sql.common.DefaultSqlStorageProvider;
import org.jobrunr.storage.sql.common.db.dialect.AnsiDialect;
import org.jobrunr.utils.mapper.jackson.JacksonJsonMapper;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import javax.sql.DataSource;

@SpringBootConfiguration
public class ApplicationSettings {
    @Bean
    public JobMapper jobMapper()
    {
        return new JobMapper(new JacksonJsonMapper());
    }

    @Bean
    public DataSource dataSource() {
        var builder = DataSourceBuilder.create();
        builder.driverClassName("org.postgresql.Driver");
        builder.url("jdbc:postgresql://192.168.31.52/testdatabase");
        builder.username("testuser");
        builder.password("simplemagic");
        return builder.build();
    }

    @Bean
    @DependsOn("jobMapper")
    public StorageProvider storageProvider(JobMapper jobMapper, DataSource dataSource) {
        return new DefaultSqlStorageProvider(dataSource, new AnsiDialect(), StorageProviderUtils.DatabaseOptions.CREATE);
    }

    @Bean
    @DependsOn("storageProvider")
    public JobScheduler initJobScheduler(StorageProvider provider, ApplicationContext applicationContext)
    {
        return JobRunr
                .configure()
                .useJobActivator(applicationContext::getBean)
                .useStorageProvider(provider)
                .useDashboard()
                .useBackgroundJobServer()
                .initialize()
                .getJobScheduler();
    }
}
