package com.example.consumer;

import com.example.api.GreetingService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class ConsumerApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ConsumerApplication.class);

    @DubboReference
    private GreetingService greetingService;

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String result = greetingService.sayHello("World");
        log.info(">>> Consumer received: {}", result);
        // 调用完成后退出容器，方便观察结果
        System.exit(0);
    }
}
