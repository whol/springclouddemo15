package com.wang.servicelucy1.hystrix;

import com.wang.servicelucy1.client.ServiceLucy2Client;
import org.springframework.stereotype.Component;

@Component
public class ServiceLucy2Hystrix implements ServiceLucy2Client {
    @Override
    public String sayHiFromClientOne(String name) {
        return "sorry "+name;
    }
}
