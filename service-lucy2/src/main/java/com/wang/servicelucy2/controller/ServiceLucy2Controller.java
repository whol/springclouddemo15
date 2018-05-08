package com.wang.servicelucy2.controller;

import com.wang.servicelucy2.client.ServiceLucy1Client;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceLucy2Controller {
    private static final Logger LOG = Logger.getLogger(ServiceLucy2Controller.class.getName());

    @Autowired
    private ServiceLucy1Client serviceLucy1Client;

    @Value("${server.port}")
    String port;

    @RequestMapping("/localhi")
    public String localhi(@RequestParam String name){
        LOG.log(Level.INFO, "Lucy2 localhi is being called");
        return "hi i'm Lucy2! "+name+",i am from port:" +port;
    }

    @RequestMapping("/remotehi")
    public String remotehi(@RequestParam String name){
        LOG.log(Level.INFO, "Lucy2 remotehi is being called");
        return serviceLucy1Client.sayHiFromClientOne(name);
    }
}
