package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.result.GraceJSONResult;
import com.imooc.result.ResponseStatusEnum;
import com.imooc.service.FansService;
import com.imooc.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "粉丝相关业务 测试接口")
@RequestMapping("fans")
@RestController
public class FansController extends BaseInfoController {

    @Autowired
    private UserService userService;
    @Autowired
    private FansService fansService;

    @PostMapping("follow")
    public Object follow(@RequestParam String myId,
                                  @RequestParam String vlogerId) {

        // 判断两个id不能为空
        if (StringUtils.isBlank(myId) || StringUtils.isBlank(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
        }

        // 判断当前用户，自己不能关注自己
        if (myId.equalsIgnoreCase(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        }

        // 判断两个id对应的用户是否存在
        Users vloger = userService.getUser(vlogerId);
        Users myInfo = userService.getUser(myId);


        if (myInfo == null || vloger == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        }

        // 保存粉丝关系到数据库
        fansService.createFollow(myId, vlogerId);

        // 博主的粉丝+1，我的关注+1
        redis.increment(REDIS_MY_FOLLOWS_COUNTS + ":" + myId, 1);
        redis.increment(REDIS_MY_FANS_COUNTS + ":" + vlogerId, 1);

        // 我和博主的关联关系，依赖redis，不要存储数据库，避免db的性能瓶颈
        redis.set(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" + vlogerId, "1");

        return GraceJSONResult.ok();
    }

    @PostMapping("cancel")
    public GraceJSONResult cancel(@RequestParam String myId,
                                  @RequestParam String vlogerId) {

        // 删除业务的执行
        fansService.cancelFollow(myId, vlogerId);

        // 博主的粉丝-1，我的关注-1
        redis.decrement(REDIS_MY_FOLLOWS_COUNTS + ":" + myId, 1);
        redis.decrement(REDIS_MY_FANS_COUNTS + ":" + vlogerId, 1);

        // 我和博主的关联关系，依赖redis，不要存储数据库，避免db的性能瓶颈
        redis.del(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" + vlogerId);

        return GraceJSONResult.ok();
    }

    @GetMapping("queryDoIFollowVloger")
    public GraceJSONResult queryDoIFollowVloger(@RequestParam String myId,
                                                @RequestParam String vlogerId) {
        return GraceJSONResult.ok(fansService.queryFollow(myId, vlogerId));
    }
//
//    @GetMapping("queryMyFollows")
//    public GraceJSONResult queryMyFollows(@RequestParam String myId,
//                                          @RequestParam Integer page,
//                                          @RequestParam Integer pageSize) {
//        return GraceJSONResult.ok(
//                fansService.queryMyFollows(
//                        myId,
//                        page,
//                        pageSize));
//    }
//
//    @GetMapping("queryMyFans")
//    public GraceJSONResult queryMyFans(@RequestParam String myId,
//                                          @RequestParam Integer page,
//                                          @RequestParam Integer pageSize) {
//        return GraceJSONResult.ok(
//                fansService.queryMyFans(
//                        myId,
//                        page,
//                        pageSize));
//    }
}
