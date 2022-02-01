package com.forthreal;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.StorageProviderUtils;
import org.jobrunr.storage.sql.common.DefaultSqlStorageProvider;
import org.jobrunr.storage.sql.common.db.dialect.AnsiDialect;
import org.jobrunr.utils.mapper.jackson.JacksonJsonMapper;
import org.postgresql.ds.PGSimpleDataSource;
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
    @DependsOn("jobMapper")
    public DataSource dataSource() {
        var dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://192.168.31.52/testdatabase");
        dataSource.setUser("testuser");
        dataSource.setPassword("simplemagic");
        return dataSource;
    }

    @Bean
    @DependsOn("dataSource")
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
                .useDashboard(8080)
                .useBackgroundJobServer()
                .initialize()
                .getJobScheduler();
    }

}
