package com.imooc.controller;//package com.imooc.controller;

import com.imooc.bo.VlogBO;
import com.imooc.enums.YesOrNo;
import com.imooc.result.GraceJSONResult;
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
    public Object indexList(@RequestParam(defaultValue = "") String search
                            ,@RequestParam(defaultValue = "") Integer page
                            ,@RequestParam(defaultValue = "") Integer pageSize) {

        if (page == null) {
            page = COMMON_START_PAGE ;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE ;
        }

        PagedGridResult pagedGridResult = vlogService.getIndexVlogList(search,page,pageSize);

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
}
