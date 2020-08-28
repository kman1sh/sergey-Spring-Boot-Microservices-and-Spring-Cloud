package com.manish.photoapp.photoappaccountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class PhotoappAccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoappAccountServiceApplication.class, args);
    }

}
