package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.imooc.base.BaseInfoProperties;
import com.imooc.bo.VlogBO;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.MyLikedVlogMapper;
import com.imooc.mapper.VlogMapper;
import com.imooc.mapper.VlogMapperCustom;
import com.imooc.pojo.MyLikedVlog;
import com.imooc.pojo.Vlog;
import com.imooc.service.FansService;
import com.imooc.service.VlogService;
import com.imooc.utils.PagedGridResult;
import com.imooc.vo.IndexVlogVO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VlogServiceImpl extends BaseInfoProperties implements VlogService {

    @Autowired
    private VlogMapper vlogMapper;

    @Autowired
    private VlogMapperCustom vlogMapperCustom ;

    @Autowired
    private MyLikedVlogMapper myLikedVlogMapper ;

    @Autowired
    private Sid sid;

    @Autowired
    private FansService fansService ;

    @Override
    @Transactional
    public void createVlog(VlogBO vlogBO) {
        String vid = sid.nextShort();
        Vlog vlog = new Vlog();

        BeanUtils.copyProperties(vlogBO,vlog);

        vlog.setId(vid);
        vlog.setLikeCounts(0);
        vlog.setCommentsCounts(0);
        vlog.setIsPrivate(YesOrNo.NO.type);
        vlog.setCreatedTime(new Date());
        vlog.setUpdatedTime(new Date());

        vlogMapper.insert(vlog) ;
    }

    @Override
    public PagedGridResult getIndexVlogList(String userId,String search , Integer page , Integer pageSize) {
        Map<String,Object> map = new HashMap<>() ;

        // 分库分表
        PageHelper.startPage(page,pageSize) ;

        if (StringUtils.isNotBlank(search)){
            map.put("search",search) ;
        }
        List<IndexVlogVO> indexVlogList = vlogMapperCustom.getIndexVlogList(map);

        for (IndexVlogVO v :
                indexVlogList) {
            String vlogId = v.getVlogId();
            // String vlogerId = v.getVlogerId();

            // 判断当前用户是否点赞过视频
            if ( StringUtils.isNotBlank(userId) ){
                // 用户是否关注该博主
                v.setDoIFollowVloger(fansService.queryFollow(userId,vlogId));

                v.setDoILikeThisVlog(doLikeVlog(userId,vlogId)) ;
            }

            // 获取当前视频被点赞过的总数
            if ( StringUtils.isNotBlank(userId) ){
                v.setLikeCounts(getVlogBeLikedCounts(vlogId)); ;
            }

        }

        return setterPagedGrid(indexVlogList,page);
    }

    @Override
    public Integer getVlogBeLikedCounts(String vlogId){
        String s = redis.get(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId);
        if (StringUtils.isNotBlank(s)){
            s = "0" ;
        }
        return Integer.valueOf(s) ;
    }
    public boolean doLikeVlog( String myId , String vlogId ){
        String doLike = redis.get(REDIS_USER_LIKE_VLOG + ":" + myId + ":" + vlogId);
        boolean isLike = false ;

        if (StringUtils.isNotBlank(doLike) && doLike.equalsIgnoreCase("1")){
            isLike = true ;
        }
        return  isLike ;
    }

    @Override
    public IndexVlogVO getVlogDetailById(String vlogId) {
        Map<String,Object> map = new HashMap<>() ;
        map.put("vlogId",vlogId);

        List<IndexVlogVO> indexVlogList = vlogMapperCustom.getIndexVlogList(map);
        if (indexVlogList != null && indexVlogList.size() > 0 && !indexVlogList.isEmpty() ) {
            IndexVlogVO indexVlogVO = indexVlogList.get(0);
            return indexVlogVO ;
        }
        return null ;
    }

    @Transactional
    @Override
    public void changeToPrivateOrPublic(String userId,
                                        String vlogId,
                                        Integer yesOrNo) {
        Example example = new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", vlogId);
        criteria.andEqualTo("vlogerId", userId);

        Vlog pendingVlog = new Vlog();
        pendingVlog.setIsPrivate(yesOrNo);

        vlogMapper.updateByExampleSelective(pendingVlog, example);
    }

    @Override
    public PagedGridResult queryMyVlogList(String userId,
                                           Integer page,
                                           Integer pageSize,
                                           Integer yesOrNo) {

        Example example = new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("vlogerId", userId);
        criteria.andEqualTo("isPrivate", yesOrNo);

        PageHelper.startPage(page, pageSize);
        List<Vlog> list = vlogMapper.selectByExample(example);

        return setterPagedGrid(list, page);
    }

    @Transactional
    @Override
    public void userLikeVlog(String userId, String vlogId) {

        String rid = sid.nextShort();

        MyLikedVlog likedVlog = new MyLikedVlog();
        likedVlog.setId(rid);
        likedVlog.setVlogId(vlogId);
        likedVlog.setUserId(userId);

        myLikedVlogMapper.insert(likedVlog);


//        // 系统消息：点赞短视频
//        Vlog vlog = this.getVlog(vlogId);
//        Map msgContent = new HashMap();
//        msgContent.put("vlogId", vlogId);
//        msgContent.put("vlogCover", vlog.getCover());
//        msgService.createMsg(userId,
//                vlog.getVlogerId(),
//                MessageEnum.LIKE_VLOG.type,
//                msgContent);
    }

    @Override
    @Transactional
    public void userUnlikeVlog(String userId, String vlogId) {
        MyLikedVlog myLikedVlog = new MyLikedVlog();
        myLikedVlog.setUserId(userId);
        myLikedVlog.setVlogId(vlogId);

        myLikedVlogMapper.delete(myLikedVlog) ;
    }

    @Override
    public PagedGridResult getMyLikedVlogList(String userId, Integer page, Integer pageSize) {
        Map<String,Object> map = new HashMap<>() ;

        // 分库分表
        PageHelper.startPage(page,pageSize) ;
        map.put("userId",userId) ;
        List<IndexVlogVO> myLikedVlogList = vlogMapperCustom.getMyLikedVlogList(map);

        return setterPagedGrid(myLikedVlogList,page);
    }

    @Override
    public PagedGridResult getMyFollowVlogList(String userId, Integer page, Integer pageSize) {
        Map<String,Object> map = new HashMap<>() ;

        // 分库分表
        PageHelper.startPage(page,pageSize) ;
        map.put("myId",userId) ;
        List<IndexVlogVO> myLikedVlogList = vlogMapperCustom.getMyFollowVlogList(map);

        for (IndexVlogVO v :
                myLikedVlogList) {
            String vlogId = v.getVlogId();

            if( StringUtils.isNotBlank(userId) ){
                v.setDoIFollowVloger(true);
                v.setDoILikeThisVlog(doLikeVlog(userId,vlogId));
            }

            v.setLikeCounts(getVlogBeLikedCounts(vlogId));
        }
        return setterPagedGrid(myLikedVlogList,page);
    }

}
