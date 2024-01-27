package com.imooc.controller;

import com.imooc.result.GraceJSONResult;
import com.imooc.utils.SMSUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// log4j的注释
@Api(tags = "hello 测试接口")
public class HelloController {
    @ApiOperation(value = "对接口的简要介绍")
    @GetMapping("hello")
    public Object hello(){
        return "Hello spring" ;
    }

    @Autowired
    private SMSUtils smsUtils ;

    @GetMapping("sms")
    public Object sms() throws Exception {
        smsUtils.sendSMS("15672142317","1234");
        return GraceJSONResult.ok();
    }
}
