package org.swiftcodes.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "org.swiftcodes.database")
public class SwiftCodeRestApi {
    public static void main(String[] args) {
        SpringApplication.run(SwiftCodeRestApi.class, args);
    }
}