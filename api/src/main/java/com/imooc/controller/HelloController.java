package com.imooc.controller;

import com.imooc.base.RabbitMQConfig;
import com.imooc.result.GraceJSONResult;
import com.imooc.utils.SMSUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// log4j的注释
@Api(tags = "hello 测试接口")
// springcloud 刷新配置
@RefreshScope
public class HelloController {
    @ApiOperation(value = "对接口的简要介绍")
    @GetMapping("hello")
    public Object hello(){
        return "Hello spring" ;
    }

    @Autowired
    private SMSUtils smsUtils ;

    @Value("${nacos.counts}")
    private Integer nacosCounts ;

    @GetMapping("nacosCounts")
    public Object nacosCounts() {
        return GraceJSONResult.ok("nacosCounts:"+nacosCounts);
    }

    @GetMapping("sms")
    public Object sms() throws Exception {
        smsUtils.sendSMS("15672142317","1234");
        return GraceJSONResult.ok();
    }

    @Autowired
    public RabbitTemplate rabbitTemplate ;

    @GetMapping("produce")
    public Object produce() throws Exception{

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MSG,"sys.msg.send","我发了一个消息");


        /*路由规则
        * route-key
        * display.*.*
        * display.a.b yes
        * display.a.b.c no
        * 多个占位符要使用 #
        * */

        return GraceJSONResult.ok() ;
    }
}
