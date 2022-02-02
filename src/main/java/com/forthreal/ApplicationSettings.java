package com.forthreal;

import com.forthreal.repository.IRuleRepository;
import com.forthreal.services.JobRetriever;
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.StorageProviderUtils;
import org.jobrunr.storage.sql.common.DefaultSqlStorageProvider;
import org.jobrunr.storage.sql.common.db.dialect.AnsiDialect;
import org.jobrunr.utils.mapper.jackson.JacksonJsonMapper;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import javax.sql.DataSource;

@Configuration
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
        dataSource.setUrl("jdbc:postgresql://127.0.0.1/testdatabase");
        dataSource.setUser("testuser");
        dataSource.setPassword("simplemagic");
        return dataSource;
    }

    @Bean
    @DependsOn("dataSource")
    public StorageProvider storageProvider(JobMapper jobMapper, DataSource dataSource) {
        return new DefaultSqlStorageProvider(dataSource, new AnsiDialect(), StorageProviderUtils.DatabaseOptions.CREATE);
    }

    @Bean(name = "JobScheduler")
    @DependsOn("storageProvider")
    public JobScheduler initJobScheduler(StorageProvider provider)
    {
        return JobRunr
                .configure()
                .useStorageProvider(provider)
                .useDashboard(8080)
                .useBackgroundJobServer()
                .initialize()
                .getJobScheduler();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource)
    {
        var entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan("com.forthreal");
        entityManagerFactoryBean.setPersistenceUnitName("Postgresql");

        var hibernateVendorAdapter = new HibernateJpaVendorAdapter();
        /* make sure to automatically create tables */
        hibernateVendorAdapter.setGenerateDdl(true);
        entityManagerFactoryBean.setJpaVendorAdapter(hibernateVendorAdapter);

        return entityManagerFactoryBean;
    }

    @Lazy
    @Bean
    public JobRetriever jobRetriever(IRuleRepository ruleRepository, JobScheduler jobScheduler)
    {
        return new JobRetriever(ruleRepository, jobScheduler);
    }
}
