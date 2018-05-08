package com.wang.servicelucy1.controller;

import com.wang.servicelucy1.client.ServiceLucy2Client;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceLucy1Controller {
    private static final Logger LOG = Logger.getLogger(ServiceLucy1Controller.class.getName());

    @Autowired
    private ServiceLucy2Client serviceLucy2Client;

    @Value("${server.port}")
    String port;

    @RequestMapping("/localhi")
    public String localhi(@RequestParam String name){
        LOG.log(Level.INFO, "Lucy1 localhi is being called");
        return "hi i'm Lucy1! "+name+",i am from port:" +port;
    }

    @RequestMapping("/remotehi")
    public String remotehi(@RequestParam String name){
        LOG.log(Level.INFO, "Lucy1 remotehi is being called");
        return serviceLucy2Client.sayHiFromClientOne(name);
    }
}
