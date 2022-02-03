package com.forthreal;

import com.forthreal.repository.IRuleRepository;
import com.forthreal.services.JobRetriever;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.StorageProviderUtils;
import org.jobrunr.storage.nosql.redis.JedisRedisStorageProvider;
import org.jobrunr.storage.sql.common.DefaultSqlStorageProvider;
import org.jobrunr.storage.sql.common.db.dialect.AnsiDialect;
import org.jobrunr.utils.mapper.jackson.JacksonJsonMapper;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import redis.clients.jedis.JedisPool;

import javax.sql.DataSource;

@Configuration
public class ApplicationSettings {

    @Bean
    public JedisConnectionFactory jedisConnectionFactory()
    {
        var redisStandaloneConfiguration =
                new RedisStandaloneConfiguration("localhost", 6379);
        redisStandaloneConfiguration.setPassword("sOmE_sEcUrE_pAsS");
        redisStandaloneConfiguration.setDatabase(0);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    @DependsOn("jedisConnectionFactory")
    public RedisTemplate<String,Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory)
    {
        var template = new RedisTemplate<String,Object>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.setEnableDefaultSerializer(false);
        template.setEnableTransactionSupport(false);
        return template;
    }

    @Bean
    public DataSource pgDataSource() {
        var dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://127.0.0.1/testdatabase");
        dataSource.setUser("testuser");
        dataSource.setPassword("simplemagic");
        return dataSource;
    }

    /*
    @Bean
    @DependsOn("dataSource")
    public StorageProvider storageProvider(DataSource dataSource) {
        return new DefaultSqlStorageProvider(dataSource, new AnsiDialect(), StorageProviderUtils.DatabaseOptions.CREATE);
    }*/

    @Bean
    public JedisPool jedisPool()
    {
        return new JedisPool("localhost", 6379, null, "sOmE_sEcUrE_pAsS" );
    }

    @Bean
    public StorageProvider storageProvider() {
        return new JedisRedisStorageProvider(jedisPool());
    }

    @Bean(name = "JobScheduler")
    @DependsOn("storageProvider")
    public JobScheduler initJobScheduler(StorageProvider provider, ApplicationContext applicationContext)
    {
        return JobRunr
                .configure()
                .useJsonMapper(new JacksonJsonMapper())
                .useJobActivator(applicationContext::getBean)
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

    @Bean
    @DependsOn("pgDataSource")
    public PlatformTransactionManager transactionManager()
    {
        var transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory(pgDataSource()).getObject());
        return transactionManager;
    }

    @Lazy
    @Bean
    public JobRetriever jobRetriever(IRuleRepository ruleRepository, JobScheduler jobScheduler)
    {
        return new JobRetriever(ruleRepository, jobScheduler);
    }
}
