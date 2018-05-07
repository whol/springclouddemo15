package com.wang.servicemiya.controller;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ServiceMiyaController {

    private static final Logger LOG = Logger.getLogger(ServiceMiyaController.class.getName());


    @RequestMapping("/hi")
    public String home(){
        LOG.log(Level.INFO, "miya hi is being called");
        return "hi i'm miya!";
    }

    @RequestMapping("/miya")
    public String info(){
        LOG.log(Level.INFO, "miya info is being called");
        return restTemplate.getForObject("http://localhost:8762/info22",String.class);
    }

    @Autowired
    private RestTemplate restTemplate;

}
