package com.wang.servicefeign.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.wang.servicefeign.client.SchedualServiceHi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HiController {
    @Autowired
    SchedualServiceHi schedualServiceHi;

    @RequestMapping(value = "/hi")
    @HystrixCommand(fallbackMethod = "hiError")
    public String hi(@RequestParam String name){
        return schedualServiceHi.sayHiFromClientOne(name);
    }

    public String hiError(String name) {
        return "hi,"+name+",sorry,error!";
    }
}
