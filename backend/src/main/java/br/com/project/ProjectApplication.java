package br.com.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.util.Optional;

@SpringBootApplication
@EnableConfigurationProperties({LiquibaseProperties.class})
public class ProjectApplication {

    private static final Logger log = LoggerFactory.getLogger(ProjectApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ProjectApplication.class);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store")).map(key -> "https").orElse("http");
        String serverPort = env.getProperty("server.port");
        String hostAddress = "localhost";

        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }

        log.info("""
                        ----------------------------------------------------------
                        \tApplication '{}' is running! Access URLs:
                        \tLocal: \t\t{}://localhost:{}
                        \tExternal: \t{}://{}:{}
                        ----------------------------------------------------------
                        """,
                env.getProperty("spring.application.name"),
                protocol,
                serverPort,
                protocol,
                hostAddress,
                serverPort);
    }

}
