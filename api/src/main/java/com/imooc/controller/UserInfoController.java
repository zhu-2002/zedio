package com.imooc.controller;

import com.imooc.bo.UpdatedUserBO;
import com.imooc.enums.UserInfoModifyType;
import com.imooc.pojo.Users;
import com.imooc.result.GraceJSONResult;
import com.imooc.service.UserService;
import com.imooc.utils.SMSUtils;
import com.imooc.vo.UsersVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// log4j的注释
@Api(tags = "UserInfo 用户信息接口模块")
@RequestMapping("userInfo")
@RestController
public class UserInfoController extends BaseInfoProperties{
    @Autowired
    private UserService userService ;

    @GetMapping("query")
    public Object query(@RequestParam String userId) throws Exception {
        Users user = userService.getUser(userId);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);

        // 扩展信息
        // 我的关注
        String myFollowsCountsStr = redis.get(REDIS_MY_FOLLOWS_COUNTS+":"+userId);
        // 我的粉丝
        String myFansCountsStr = redis.get(REDIS_MY_FANS_COUNTS+":"+userId);
        // 获赞总数
        String likedVlogCountsStr = redis.get(REDIS_VLOG_BE_LIKED_COUNTS+":"+userId);
        String likedVlogerCountsStr = redis.get(REDIS_VLOGER_BE_LIKED_COUNTS+":"+userId);

        Integer myFollowsCounts = 0 ;
        Integer myFansCounts = 0 ;
        Integer likedVlogCounts = 0;
        Integer likedVlogerCounts = 0;
        Integer totalLikeMeCounts = 0 ;

        if (StringUtils.isNotBlank(myFollowsCountsStr)) {
            myFollowsCounts = Integer.valueOf(myFollowsCountsStr);
        }
        if (StringUtils.isNotBlank(myFansCountsStr)) {
            myFansCounts = Integer.valueOf(myFansCountsStr);
        }
        if (StringUtils.isNotBlank(likedVlogCountsStr)) {
            likedVlogCounts = Integer.valueOf(likedVlogCountsStr);
        }
        if (StringUtils.isNotBlank(likedVlogerCountsStr)) {
            likedVlogerCounts = Integer.valueOf(likedVlogerCountsStr);
        }
        totalLikeMeCounts = likedVlogCounts + likedVlogerCounts ;

        usersVO.setMyFollowsCounts(myFollowsCounts);
        usersVO.setMyFansCounts(myFansCounts);
        usersVO.setTotalLikeMeCounts(totalLikeMeCounts);

        return GraceJSONResult.ok() ;
    }

    @PostMapping("modifyUserInfo")
    public Object modifyUserInfo(@RequestBody UpdatedUserBO updatedUserBO
                                , @RequestParam Integer type
                    ) throws Exception {
        // 检查类型是否存在

        UserInfoModifyType.checkUserInfoTypeIsRight(type);

        Users newUserInfo = userService.updateUserInfo(updatedUserBO, type) ;

        return GraceJSONResult.ok(newUserInfo) ;
    }

}
