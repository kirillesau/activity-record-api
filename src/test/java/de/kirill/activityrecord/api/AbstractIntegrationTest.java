package de.kirill.activityrecord.api;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractIntegrationTest {
    private static final PostgreSQLContainer<?> POSTGRES_DB;

    static {
        POSTGRES_DB = new PostgreSQLContainer<>("postgres:17")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        POSTGRES_DB.start();
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_DB::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_DB::getUsername);
        registry.add("spring.datasource.password", POSTGRES_DB::getPassword);
    }
}
