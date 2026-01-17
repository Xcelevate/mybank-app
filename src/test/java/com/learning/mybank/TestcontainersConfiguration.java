package com.learning.mybank;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;
import java.util.List;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {
    private static final Network NETWORK = Network.newNetwork();

    @Bean("postgres")
    @ServiceConnection
    PostgreSQLContainer postgres() {
        PostgreSQLContainer postgres =
                new PostgreSQLContainer("postgres:17")
                        .withDatabaseName("mybank")
                        .withUsername("sa")
                        .withPassword("sa")
                        .withNetwork(NETWORK)
                        .withInitScript("db/init-db.sql");
        postgres.setPortBindings(List.of("5432:5432"));
        postgres.start(); // ensure started

        System.out.println("=== PostgreSQL Testcontainer ===");
        System.out.println("JDBC URL : " + postgres.getJdbcUrl());
        System.out.println("Host     : " + postgres.getHost());
        System.out.println("Port     : " + postgres.getMappedPort(5432));
        System.out.println("User     : " + postgres.getUsername());
        System.out.println("Password : " + postgres.getPassword());

        return postgres;
    }

    @Bean
    @DependsOn("postgres")
    GenericContainer<?> pgadmin(PostgreSQLContainer postgres) {
        GenericContainer<?> pgadmin =
                new GenericContainer<>("dpage/pgadmin4:9.11")
                        .withEnv("PGADMIN_DEFAULT_EMAIL", "admin@mybank.com")
                        .withEnv("PGADMIN_DEFAULT_PASSWORD", "admin123")
                        .withEnv("PGADMIN_CONFIG_SERVER_MODE", "False")
                        .withEnv("PGADMIN_CONFIG_MASTER_PASSWORD_REQUIRED", "False")
                        .withStartupTimeout(Duration.ofMinutes(2))
                        .withExposedPorts(80)
                        .withNetwork(NETWORK)
                        .withCopyFileToContainer(
                                MountableFile.forClasspathResource("db/servers.json"),
                                "/pgadmin4/servers.json")
                        .waitingFor(Wait.forHttp("/").forStatusCode(200))
                        .withStartupTimeout(Duration.ofMinutes(2))
                        .dependsOn(postgres);

        pgadmin.setPortBindings(List.of("5050:80"));
        pgadmin.start();


        Integer pgadminPort = pgadmin.getMappedPort(80);
        String pgadminUrl = "http://localhost:" + pgadminPort + "/";

        System.out.println();
        System.out.println("=== PgAdmin Testcontainer ===");
        System.out.println("PgAdmin URL : " + pgadminUrl);
        System.out.println("Login email : admin@mybank.local");
        System.out.println("Login pass  : admin123");

        return pgadmin;
    }




}
