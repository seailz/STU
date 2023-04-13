package com.seailz.stu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.seailz.stu.ws")
public class ServiceTeamUnion {

    public static void main(String[] args) {
        SpringApplication.run(ServiceTeamUnion.class, args);
    }

    public static boolean validateToken(String token) {
        // TODO: validate token
        return true;
    }

}
