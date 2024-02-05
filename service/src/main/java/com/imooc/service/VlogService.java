package com.imooc.service;

import com.imooc.bo.UpdatedUserBO;
import com.imooc.bo.VlogBO;
import com.imooc.pojo.Users;
import com.imooc.utils.PagedGridResult;
import com.imooc.vo.IndexVlogVO;

import java.util.List;

public interface VlogService {

    /*
    * 新增vlog视频
    * */
    public void createVlog(VlogBO vlogBO);

    /*
     * 查询vlog视频列表
     * */
    public PagedGridResult getIndexVlogList(String search, Integer page , Integer pageSize);

    /*
     * 根据视频主键查询vlog
     * */
    public IndexVlogVO getVlogDetailById(String vlogId);


    /**
     * 用户把视频改为公开/私密的视频
     */
    public void changeToPrivateOrPublic(String userId,
                                        String vlogId,
                                        Integer yesOrNo);

    /**
     * 查询用的公开/私密的视频列表
     */
    public PagedGridResult queryMyVlogList(String userId,
                                           Integer page,
                                           Integer pageSize,
                                           Integer yesOrNo);



}
