package com.bangtechnologies.qrscanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication(scanBasePackages = {"com.bangtechnologies.qrscanner"})
@ComponentScan(basePackages = {"com.bangtechnologies.qrscanner.controller",
        "com.bangtechnologies.qrscanner.service",
        "com.bangtechnologies.qrscanner.dao"})
public class QrScannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(QrScannerApplication.class, args);
    }
}
