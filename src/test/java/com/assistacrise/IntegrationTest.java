package com.assistacrise;

import com.assistacrise.config.AsyncSyncConfiguration;
import com.assistacrise.config.DatabaseTestcontainer;
import com.assistacrise.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        AssistaCriseApp.class,
        JacksonConfiguration.class,
        AsyncSyncConfiguration.class,
        com.assistacrise.config.JacksonHibernateConfiguration.class,
    }
)
@ImportTestcontainers(DatabaseTestcontainer.class)
public @interface IntegrationTest {}
