package com.walkmates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * WalkMates application entry point.
 *
 * <p>Run with {@code mvn spring-boot:run} and open {@code http://localhost:8080}. Sample data is
 * loaded at startup by {@link com.walkmates.web.DataLoader} so there is something to see and
 * book immediately.</p>
 */
@SpringBootApplication
public class WalkMatesApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalkMatesApplication.class, args);
    }
}
