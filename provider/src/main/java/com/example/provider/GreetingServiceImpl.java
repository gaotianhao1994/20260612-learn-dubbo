package com.example.provider;

import com.example.api.GreetingService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class GreetingServiceImpl implements GreetingService {
    @Override
    public String sayHello(String name) {
        return "[Dubbo] Hello, " + name + "!";
    }
}
