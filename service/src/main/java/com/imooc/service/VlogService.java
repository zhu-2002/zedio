package com.imooc.service;

import com.imooc.bo.UpdatedUserBO;
import com.imooc.bo.VlogBO;
import com.imooc.pojo.Users;
import com.imooc.pojo.Vlog;
import com.imooc.utils.PagedGridResult;
import com.imooc.vo.IndexVlogVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VlogService {

    /*
    * 新增vlog视频
    * */
    public void createVlog(VlogBO vlogBO);

    /*
     * 查询vlog视频列表
     * */
    public PagedGridResult getIndexVlogList(String userId,String search, Integer page , Integer pageSize);

    Integer getVlogBeLikedCounts(String vlogId);

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

    /**
     * 用户点赞视频
     */
    public void userLikeVlog(String userId, String vlogId);

    Vlog getVlog(String vlogId);

    /**
     * 用户取消点赞视频
     * */
    public void userUnlikeVlog(String userId, String vlogId);

    /*
    *查询用户点赞过的视频
    * */
    public PagedGridResult getMyLikedVlogList(String userId , Integer page , Integer pageSize);

    /*
     *查询关注用户的视频
     * */
    public PagedGridResult getMyFollowVlogList(String userId , Integer page , Integer pageSize);

    @Transactional
    void flushCounts(String vlogId, Integer counts);
}
