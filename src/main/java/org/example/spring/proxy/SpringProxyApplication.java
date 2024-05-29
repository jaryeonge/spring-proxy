package org.example.spring.proxy;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SpringProxyApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringProxyApplication.class).run(args);
    }
}
