package org.sharedhealth.mci.web.config;

import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

@Configuration
@EnableCaching
@Import({MCISecurityConfig.class, MCICassandraConfig.class, MCIWebConfig.class, ActuatorConfig.class})
@ComponentScan(basePackages = {"org.sharedhealth.mci.web.config",
        "org.sharedhealth.mci.web.controller",
        "org.sharedhealth.mci.web.exception",
        "org.sharedhealth.mci.web.infrastructure",
        "org.sharedhealth.mci.web.mapper",
        "org.sharedhealth.mci.web.model",
        "org.sharedhealth.mci.web.service",
        "org.sharedhealth.mci.utils",
        "org.sharedhealth.mci.web.handler",
        "org.sharedhealth.mci.validation",
        "org.sharedhealth.mci.web.tasks"})
public class MCIConfig {

    public static final int CACHE_TTL_IN_MINUTES = 15;
    public static final int MASTER_DATA_CACHE_TTL_IN_DAYS = 1;

    public static final String PROVIDER_CACHE = "PROVIDER_CACHE";
    public static final String SETTINGS_CACHE = "SETTINGS_CACHE";
    public static final String APPROVAL_FIELDS_CACHE = "APPROVAL_FIELDS_CACHE";
    public static final String MASTER_DATA_CACHE = "MASTER_DATA_CACHE";
    public static final String PROFILE_DEFAULT = "default";

    @Autowired
    private MCIProperties mciProperties;

    @Bean(name = "MCIRestTemplate")
    public AsyncRestTemplate mciRestTemplate() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();
        executor.setCorePoolSize(mciProperties.getRestPoolSize());
        return new AsyncRestTemplate(executor);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        cacheManager.setCaches(Arrays.asList(
                createConcurrentMapCache(SETTINGS_CACHE, CACHE_TTL_IN_MINUTES, MINUTES, 10),
                createConcurrentMapCache(APPROVAL_FIELDS_CACHE, CACHE_TTL_IN_MINUTES, MINUTES, 50),
                createConcurrentMapCache(PROVIDER_CACHE, 15, DAYS, 500),
                createConcurrentMapCache(MASTER_DATA_CACHE, MASTER_DATA_CACHE_TTL_IN_DAYS, DAYS, 500)
        ));

        return cacheManager;
    }

    private ConcurrentMapCache createConcurrentMapCache(String name, int duration, TimeUnit unit, int maxSize) {
        return new ConcurrentMapCache(name,
                CacheBuilder
                        .newBuilder()
                        .expireAfterWrite(duration, unit)
                        .maximumSize(maxSize).build().asMap(),
                true
        );
    }

    public static List<String> getSupportedRequestUris(String apiVersion, boolean isLatestApiVersion) {
        List<String> mappings = getSupportedServletMappings(apiVersion, isLatestApiVersion);
        List<String> uris = new ArrayList<>();

        for (String mapping : mappings) {
            uris.add(substringBeforeLast(mapping, "/*"));
        }
        return uris;
    }

    public static List<String> getSupportedServletMappings(String apiVersion, boolean isLatestApiVersion) {
        List<String> mappings = new ArrayList<>();

        mappings.add(format("/api/%s/%s/*", apiVersion, PROFILE_DEFAULT));
        mappings.add(format("/api/%s/*", apiVersion));

        if (isLatestApiVersion) {
            mappings.add(format("/api/%s/*", PROFILE_DEFAULT));
            mappings.add("/api/*");
        }

        return mappings;
    }
}
