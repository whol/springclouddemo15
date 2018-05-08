package com.wang.servicelucy1.client;

import com.wang.servicelucy1.hystrix.ServiceLucy2Hystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "service-lucy2", fallback = ServiceLucy2Hystrix.class)
public interface ServiceLucy2Client {
    @RequestMapping(value = "/localhi",method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);
}
