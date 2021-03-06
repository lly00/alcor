package com.futurewei.alcor.subnet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class SubnetApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubnetApplication.class, args);
    }

}
