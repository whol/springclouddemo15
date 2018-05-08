package com.wang.servicelucy2.hystrix;

import com.wang.servicelucy2.client.ServiceLucy1Client;
import org.springframework.stereotype.Component;

@Component
public class ServiceLucy1Hystrix implements ServiceLucy1Client {
    @Override
    public String sayHiFromClientOne(String name) {
        return "sorry "+name;
    }

}
