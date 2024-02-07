package com.imooc.controller;//package com.imooc.controller;

import com.imooc.bo.VlogBO;
import com.imooc.enums.YesOrNo;
import com.imooc.result.GraceJSONResult;
import com.imooc.result.ResponseStatusEnum;
import com.imooc.service.VlogService;
import com.imooc.utils.PagedGridResult;
import com.imooc.vo.IndexVlogVO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
// log4j的注释
@Api(tags = "视频业务 测试接口")
@RequestMapping("vlog")
public class VlogController extends BaseInfoController {
    @Autowired
    private VlogService vlogService;

    @PostMapping("publish")
    public Object publish(@RequestBody VlogBO vlogBO) {
        vlogService.createVlog(vlogBO);
        return GraceJSONResult.ok();
    }

    @GetMapping("indexList")
    public Object indexList(@RequestParam(defaultValue = "") String userId
                            ,@RequestParam(defaultValue = "") String search
                            ,@RequestParam(defaultValue = "") Integer page
                            ,@RequestParam(defaultValue = "") Integer pageSize) {

        if (page == null) {
            page = COMMON_START_PAGE ;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE ;
        }

        PagedGridResult pagedGridResult = vlogService.getIndexVlogList(userId,search,page,pageSize);

        return GraceJSONResult.ok(pagedGridResult);
    }

    @GetMapping("detail")
    public Object detail(@RequestParam(defaultValue = "") String userId
                        ,@RequestParam String vlogId ) {

        IndexVlogVO vlogDetailById = vlogService.getVlogDetailById(vlogId);

        return GraceJSONResult.ok(vlogDetailById);
    }

    @PostMapping("changeToPrivate")
    public GraceJSONResult changeToPrivate(@RequestParam String userId,
                                           @RequestParam String vlogId) {
        vlogService.changeToPrivateOrPublic(userId,
                vlogId,
                YesOrNo.YES.type);
        return GraceJSONResult.ok();
    }

    @PostMapping("changeToPublic")
    public GraceJSONResult changeToPublic(@RequestParam String userId,
                                          @RequestParam String vlogId) {
        vlogService.changeToPrivateOrPublic(userId,
                vlogId,
                YesOrNo.NO.type);
        return GraceJSONResult.ok();
    }

    @GetMapping("myPublicList")
    public GraceJSONResult myPublicList(@RequestParam String userId,
                                        @RequestParam Integer page,
                                        @RequestParam Integer pageSize) {

        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult gridResult = vlogService.queryMyVlogList(userId,
                page,
                pageSize,
                YesOrNo.NO.type);
        return GraceJSONResult.ok(gridResult);
    }

    @GetMapping("myPrivateList")
    public GraceJSONResult myPrivateList(@RequestParam String userId,
                                         @RequestParam Integer page,
                                         @RequestParam Integer pageSize) {

        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult gridResult = vlogService.queryMyVlogList(userId,
                page,
                pageSize,
                YesOrNo.YES.type);
        return GraceJSONResult.ok(gridResult);
    }

    @PostMapping("like")
    public GraceJSONResult like(@RequestParam String userId,
                                         @RequestParam String vlogerId,
                                         @RequestParam String vlogId) {

        if (userId == null || vlogerId == null || vlogId == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO) ;
        }
        // 我点赞的视频，关联关系保存到数据库
        vlogService.userLikeVlog(userId, vlogId) ;

        // 点赞后，视频和视频发布者的赞都会 +1
        redis.increment(REDIS_VLOGER_BE_LIKED_COUNTS+":"+vlogerId,1) ;
        redis.increment(REDIS_VLOG_BE_LIKED_COUNTS+":"+vlogId,1) ;

        // 我点赞的视频，需要在redis中保存关联关系
        redis.set(REDIS_USER_LIKE_VLOG+":"+userId+":"+vlogId,"1") ;


        return GraceJSONResult.ok();
    }

    @PostMapping("unlike")
    public GraceJSONResult unlike(@RequestParam String userId,
                                @RequestParam String vlogerId,
                                @RequestParam String vlogId) {

        if (userId == null || vlogerId == null || vlogId == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO) ;
        }
        // 我取消点赞的视频，关联关系修改保存到数据库
        vlogService.userUnlikeVlog(userId, vlogId) ;

        // 取消点赞后，视频和视频发布者的赞都会 -1
        redis.decrement(REDIS_VLOGER_BE_LIKED_COUNTS+":"+vlogerId,1) ;
        redis.decrement(REDIS_VLOG_BE_LIKED_COUNTS+":"+vlogId,1) ;

        // 我取消点赞的视频，删除在redis中的关联关系
        redis.del(REDIS_USER_LIKE_VLOG+":"+userId+":"+vlogId) ;


        return GraceJSONResult.ok() ;
    }


    // 刷新点赞后该视频的点赞总数
    @PostMapping("totalLikedCounts")
    public GraceJSONResult totalLikedCounts( @RequestParam String vlogId) {
        return GraceJSONResult.ok(vlogService.getVlogBeLikedCounts(vlogId)) ;
    }

    @GetMapping("myLikedList")
    public GraceJSONResult myLikedList(@RequestParam String userId,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize) {

        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult gridResult = vlogService.getMyLikedVlogList(userId,
                page,
                pageSize);
        return GraceJSONResult.ok(gridResult);
    }

    @GetMapping("followList")
    public GraceJSONResult followList(@RequestParam String myId,
                                      @RequestParam Integer page,
                                      @RequestParam Integer pageSize) {

        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult gridResult = vlogService.getMyFollowVlogList(myId,
                page,
                pageSize);
        return GraceJSONResult.ok(gridResult);
    }
}
