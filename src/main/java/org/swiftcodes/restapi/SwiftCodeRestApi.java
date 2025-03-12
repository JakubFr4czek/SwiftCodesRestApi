package org.swiftcodes.restapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EntityScan(basePackages = "org.swiftcodes.database")
@EnableJpaRepositories(basePackages = "org.swiftcodes.database.repositories")  // Update with your correct package
public class SwiftCodeRestApi {
    public static void main(String[] args) {
        SpringApplication.run(SwiftCodeRestApi.class, args);
    }
}