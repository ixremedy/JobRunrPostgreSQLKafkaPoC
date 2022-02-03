package com.forthreal;

import com.forthreal.handlers.RuleInsertionJobHandler;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;

@Configuration
public class ApplicationSettings {

    @Bean
    public DataSource dataSource() {
        var dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://127.0.0.1/testdatabase");
        dataSource.setUser("testuser");
        dataSource.setPassword("simplemagic");
        return dataSource;
    }

    @Bean
    @DependsOn("dataSource")
    public StorageProvider storageProvider(DataSource dataSource) {
        return new DefaultSqlStorageProvider(dataSource, new AnsiDialect(), StorageProviderUtils.DatabaseOptions.CREATE);
    }

    @Bean
    public RuleInsertionJobHandler ruleInsertionJobHandler()
    {
        return new RuleInsertionJobHandler();
    }

    @Bean(name = "JobScheduler")
    @DependsOn("storageProvider")
    public JobScheduler initJobScheduler(StorageProvider provider, ApplicationContext applicationContext)
    {
        return JobRunr
                .configure()
                .useJobActivator(applicationContext::getBean)
                .useStorageProvider(provider)
                .useDashboard(8080)
                .useBackgroundJobServer()
                .useJsonMapper(new JacksonJsonMapper())
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

    @Bean
    public PlatformTransactionManager transactionManager()
    {
        var transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory(dataSource()).getObject());
        return transactionManager;
    }

    @Lazy
    @Bean
    public JobRetriever jobRetriever(IRuleRepository ruleRepository, JobScheduler jobScheduler)
    {
        return new JobRetriever(ruleRepository, jobScheduler);
    }
}
