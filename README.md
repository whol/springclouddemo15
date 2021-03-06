# springclouddemo15
spring cloud 1.5.12版本整合示例。

spring_cloud_demo_15

1、新建工程，springboot版本选择1.5.12，选择Web-web和Cloud Discovery-Eureka Server。
2、新建module，命名为eurekaserver。
2.1、在pom中指定parent：
<parent>
   <groupId>com.wang</groupId>
   <artifactId>springclouddemo15</artifactId>
   <version>0.0.1-SNAPSHOT</version>
   <relativePath/> <!-- lookup parent from repository -->
</parent>
2.2、配置文件application.yml内容：
server:
  port: 8761
eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
2.3、启动入口Application上面增加注解@EnableEurekaServer
2.4、右键运行EurekaServerApplication启动eurekaserver

3、新建module，命名为service-hi
3.1、在pom中指定parent：
<parent>
   <groupId>com.wang</groupId>
   <artifactId>springclouddemo15</artifactId>
   <version>0.0.1-SNAPSHOT</version>
   <relativePath/> <!-- lookup parent from repository -->
</parent>
3.2、配置文件application.yml内容：
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8762
spring:
  application:
    name: service-hi
3.3、启动入口Application上面增加注解@EnableEurekaClient
3.4、右键运行ServiceHiApplication启动service-hi
3.5、启动多个服务：edit Run Configurations-选择ServiceHiApplication，在最右边取消Single instance only的选中，重新右键运行ServiceHiApplication启动service-hi

4、负载均衡 服务消费者（rest+ribbon）
4.1、在pom中指定parent：
<parent>
   <groupId>com.wang</groupId>
   <artifactId>springclouddemo15</artifactId>
   <version>0.0.1-SNAPSHOT</version>
   <relativePath/> <!-- lookup parent from repository -->
</parent>
增加ribbon的dependency
   <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-ribbon</artifactId>
   </dependency>
4.2、配置文件application.yml内容：
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8764
spring:
  application:
    name: service-ribbon
4.3、启动入口Application上面增加注解@EnableDiscoveryClient
向程序的ioc注入一个bean: restTemplate;并通过@LoadBalanced注解表明这个restRemplate开启负载均衡的功能。
@Bean
@LoadBalanced
RestTemplate restTemplate() {
   return new RestTemplate();
}
4.4、新建HiService
@Service
public class HiService {
    @Autowired
    RestTemplate restTemplate;

    public String hiService(String name) {
        return restTemplate.getForObject("http://SERVICE-HI/hi?name="+name,String.class);
    }
}
4.5、新建HiController
@RestController
public class HiController {
    @Autowired
    HiService hiService;

    @RequestMapping(value = "/hi")
    public String hi(@RequestParam String name){
        return hiService.hiService(name);
    }
}
4.6、右键运行ServiceRibbonApplication启动service-ribbon

5、负载均衡 服务消费者（Feign）
5.1、在pom中指定parent：
<parent>
   <groupId>com.wang</groupId>
   <artifactId>springclouddemo15</artifactId>
   <version>0.0.1-SNAPSHOT</version>
   <relativePath/> <!-- lookup parent from repository -->
</parent>
增加feign的dependency
   <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-feign</artifactId>
   </dependency>
5.2、配置文件application.yml内容：
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8765
spring:
  application:
    name: service-feign
5.3、启动入口Application上面增加注解@EnableDiscoveryClient @EnableFeignClients
5.4、定义一个feign接口，通过@ FeignClient（“服务名”），来指定调用哪个服务。
@FeignClient(value = "service-hi")
public interface SchedualServiceHi {
    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);
}
5.5、新建HiController
@RestController
public class HiController {
    @Autowired
    SchedualServiceHi schedualServiceHi;

    @RequestMapping(value = "/hi")
    public String hi(@RequestParam String name){
        return schedualServiceHi.sayHiFromClientOne(name);
    }
}
5.6、右键运行ServiceFeignApplication启动service-feign

6、加入断路器（Hystrix）
6.1、在service-feign基础上增加依赖
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
6.2、启动入口Application上面增加注解@EnableHystrix启动hystrix
6.3、在FeignClient的SchedualServiceHi接口的注解中加上fallback的指定类
@FeignClient(value = "service-hi", fallback = SchedualServiceHiHystrix.class)
6.4、SchedualServiceHiHystric需要实现SchedualServiceHi 接口，并注入到Ioc容器中
@Component
public class SchedualServiceHiHystrix implements SchedualServiceHi {
    @Override
    public String sayHiFromClientOne(String name) {
        return "sorry "+name;
    }
}
6.5、在配置文件中配置打开断路器功能
feign:
  hystrix:
    enabled: true
6.6、右键运行ServiceFeignApplication启动service-feign

7、加入Hystrix Dashboard (断路器：Hystrix 仪表盘)
7.1、在6的service-feign基础上增加依赖
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
</dependency>
7.2、启动类中加入@EnableHystrixDashboard注解，开启hystrixDashboard
7.3、右键运行ServiceFeignApplication启动service-feign
7.4、访问http://localhost:8765/hystrix，输入http://localhost:8765/hystrix.stream
7.5、再开一个页面，访问http://localhost:8765/hi?name=wang，可以在图表中看到相关显示。

8、加入路由网关(zuul)
8.1、在pom中指定parent：
<parent>
   <groupId>com.wang</groupId>
   <artifactId>springclouddemo15</artifactId>
   <version>0.0.1-SNAPSHOT</version>
   <relativePath/> <!-- lookup parent from repository -->
</parent>
增加zuul的dependency
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-zuul</artifactId>
</dependency>
8.2、配置文件application.yml内容：
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8769
spring:
  application:
    name: service-zuul
zuul:
  routes:
    api-a:
      path: /api-a/**
      serviceId: service-ribbon
    api-b:
      path: /api-b/**
      serviceId: service-feign
8.3、启动入口Application上面增加注解@EnableZuulProxy @EnableEurekaClient
8.4、右键运行ServiceZuulApplication启动service-zuul
8.5、服务过滤
新建一个MyFilter
@Component
public class MyFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(MyFilter.class);
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.info(String.format("%s >>> %s", request.getMethod(), request.getRequestURL().toString()));
        Object accessToken = request.getParameter("token");
        if(accessToken == null) {
            log.warn("token is empty");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            try {
                ctx.getResponse().getWriter().write("token is empty");
            }catch (Exception e){}

            return null;
        }
        log.info("ok");
        return null;
    }
}
8.6、filterType：返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下：
pre：路由之前
routing：路由之时
post： 路由之后
error：发送错误调用
filterOrder：过滤的顺序
shouldFilter：这里可以写逻辑判断，是否要过滤，本文true,永远过滤。
run：过滤器的具体逻辑。可用很复杂，包括查sql，nosql去判断该请求到底有没有权限访问。
8.7、访问http://localhost:8769/api-a/hi?name=forezp会提示token is empty

9、增加分布式配置中心(Spring Cloud Config)
9.1、在pom中指定parent：
<parent>
   <groupId>com.wang</groupId>
   <artifactId>springclouddemo15</artifactId>
   <version>0.0.1-SNAPSHOT</version>
   <relativePath/> <!-- lookup parent from repository -->
</parent>
增加config-server的dependency
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-config-server</artifactId>
</dependency>
9.2、配置文件application.yml内容：
server:
  port: 8888

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/whol/SpringcloudConfig-1/
          search-paths: respo
          username:
          password:
      label: master
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
9.3、启动入口Application上面增加注解@EnableConfigServer @EnableEureka
9.4、右键运行ConfigServerApplication启动config-server

10、构建一个config client
10.1、在pom中指定parent：
<parent>
   <groupId>com.wang</groupId>
   <artifactId>springclouddemo15</artifactId>
   <version>0.0.1-SNAPSHOT</version>
   <relativePath/> <!-- lookup parent from repository -->
</parent>
增加config-server的dependency
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
10.2、配置文件application.yml内容：
spring:
  application:
    name: config-client
  cloud:
    config:
      label: master
      profile: dev
      uri: http://localhost:8888/
      discovery:
        enabled: true
        serviceId: config-server
server:
  port: 8881
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
10.3、新建controller
@RestController
@RefreshScope
public class ConfigClientController {
    @Value("${foo}")
    String foo;
    @RequestMapping(value = "/hi")
    public String hi(){
        return foo;
    }
}
10.4、右键运行ConfigClientApplication启动config-client

11、使用消息总线(Spring Cloud Bus)
11.1、安装rabbitMq
11.2、在config-server和config-client基础上增加dependency
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
11.3、在两个模块的配置文件中分别加上RabbitMq的配置
spring:
  rabbitmq:
    host: localhost
    port: 5672
  cloud:
    bus:
      trace:
        enabled: true
#关闭验证
management:
  security:
    enabled: false
11.4、向config-server发送post请求：http://localhost:8888/bus/refresh，访问config-client，查看获取到的内容是否更新。
11.5、另外，/bus/refresh接口可以指定服务，即使用”destination”参数，比如 “/bus/refresh?destination=customers:**” 即刷新服务名为customers的所有服务，不管ip。

12、服务链路追踪(Spring Cloud Sleuth)
12.1、新建server-zipkin服务器，在pom中指定parent：
<parent>
   <groupId>com.wang</groupId>
   <artifactId>springclouddemo15</artifactId>
   <version>0.0.1-SNAPSHOT</version>
   <relativePath/> <!-- lookup parent from repository -->
</parent>
增加zipkin的dependency
<dependency>
   <groupId>io.zipkin.java</groupId>
   <artifactId>zipkin-server</artifactId>
</dependency>
<dependency>
   <groupId>io.zipkin.java</groupId>
   <artifactId>zipkin-autoconfigure-ui</artifactId>
</dependency>
12.2、配置文件application.yml内容：
server:
  port: 9411
12.3、启动入口Application上面增加注解@EnableZipkinServer
12.4、右键运行ServerZipkinApplication启动server-zipkin

12.5、新建service-hi客户端，在pom中指定parent：
<parent>
   <groupId>com.wang</groupId>
   <artifactId>springclouddemo15</artifactId>
   <version>0.0.1-SNAPSHOT</version>
   <relativePath/> <!-- lookup parent from repository -->
</parent>
增加zipkin的dependency
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
12.6、配置文件application.yml内容：
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8762
spring:
  application:
    name: service-hi
  zipkin:
    base-url: http://localhost:9411
12.7、启动入口Application上面增加注解@EnableEurekaClient
@Bean
public RestTemplate getRestTemplate(){
   return new RestTemplate();
}
@Bean
public AlwaysSampler defaultSampler(){
   return new AlwaysSampler();
}
12.8、新建controller
@RestController
public class ServiceHiController {
    private static final Logger LOG = Logger.getLogger(ServiceHiController.class.getName());
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/hi")
    public String callHome(){
        LOG.log(Level.INFO, "calling trace service-hi  ");
        return restTemplate.getForObject("http://localhost:8989/hi", String.class);
    }
    @RequestMapping("/info22")
    public String info(){
        LOG.log(Level.INFO, "calling trace service-hi ");
        return "i'm service-hi";
    }
}
12.9、右键运行ServiceHiApplication启动server-hi

12.10、新建service-miya客户端，在pom中指定parent：
<parent>
   <groupId>com.wang</groupId>
   <artifactId>springclouddemo15</artifactId>
   <version>0.0.1-SNAPSHOT</version>
   <relativePath/> <!-- lookup parent from repository -->
</parent>
增加zipkin的dependency
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
12.11、配置文件application.yml内容：
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8989
spring:
  application:
    name: service-miya
  zipkin:
    base-url: http://localhost:9411
12.12、启动入口Application上面增加注解@EnableEurekaClient
@Bean
public RestTemplate getRestTemplate(){
   return new RestTemplate();
}
@Bean
public AlwaysSampler defaultSampler(){
   return new AlwaysSampler();
}
12.13、新建controller
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
12.14、右键运行ServiceMiyaApplication启动server-miya


13、加入断路器聚合监控(Hystrix Turbine)
13.1、在pom中指定parent：
<parent>
   <groupId>com.wang</groupId>
   <artifactId>springclouddemo15</artifactId>
   <version>0.0.1-SNAPSHOT</version>
   <relativePath/> <!-- lookup parent from repository -->
</parent>
增加config-server的dependency
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-turbine</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-netflix-turbine</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
</dependency>
13.2、配置文件application.yml内容：
spring:
  application:
    name: service-turbine
server:
  port: 8769
security:
  basic:
    enabled: false
turbine:
  aggregator:
    clusterConfig: default   # 指定聚合哪些集群，多个使用","分割，默认为default。可使用http://.../turbine.stream?cluster={clusterConfig之一}访问
  appConfig: service-lucy1,service-lucy2  # 配置Eureka中的serviceId列表，表明监控哪些服务
  clusterNameExpression: new String("default")
  # 1. clusterNameExpression指定集群名称，默认表达式appName；此时：turbine.aggregator.clusterConfig需要配置想要监控的应用名称
  # 2. 当clusterNameExpression: default时，turbine.aggregator.clusterConfig可以不写，因为默认就是default
  # 3. 当clusterNameExpression: metadata['cluster']时，假设想要监控的应用配置了eureka.instance.metadata-map.cluster: ABC，则需要配置，同时turbine.aggregator.clusterConfig: ABC
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
13.3、启动入口Application上面增加注解@EnableTurbine    @EnableHystrixDashboard

13.4、在需要加入断路器聚合监控的子系统的pom中增加dependency：
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
13.5、配置文件application.yml内容：
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8088
spring:
  application:
    name: service-lucy1
feign:
  hystrix:
    enabled: true
13.6、启动入口Application上面增加注解
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients
@EnableHystrix
13.7、controller内容：
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
13.8、feign client内容：
@FeignClient(value = "service-lucy2", fallback = ServiceLucy2Hystrix.class)
public interface ServiceLucy2Client {
    @RequestMapping(value = "/localhi",method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);
}
13.9、hystrix内容：
@Component
public class ServiceLucy2Hystrix implements ServiceLucy2Client {
    @Override
    public String sayHiFromClientOne(String name) {
        return "sorry "+name;
    }
}

13.10、启动Hystrix Dashboardhttp://localhost:8769/hystrix
内容输入turbine的监控数据路径http://localhost:8769/turbine.stream



