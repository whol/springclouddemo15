package com.wang.servicelucy2.client;

import com.wang.servicelucy2.hystrix.ServiceLucy1Hystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "service-lucy1", fallback = ServiceLucy1Hystrix.class)
public interface ServiceLucy1Client {
    @RequestMapping(value = "/localhi",method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);
}