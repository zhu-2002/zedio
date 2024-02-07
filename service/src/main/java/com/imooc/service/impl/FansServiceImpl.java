package com.imooc.service.impl;


import com.github.pagehelper.PageHelper;
import com.imooc.base.BaseInfoProperties;
import com.imooc.base.RabbitMQConfig;
import com.imooc.enums.MessageEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.FansMapper;
import com.imooc.mapper.FansMapperCustom;
import com.imooc.mo.MessageMO;
import com.imooc.pojo.Fans;
import com.imooc.service.FansService;
import com.imooc.service.MsgService;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.PagedGridResult;
import com.imooc.vo.FansVO;
import com.imooc.vo.VlogerVO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FansServiceImpl extends BaseInfoProperties implements FansService {

    @Autowired
    private FansMapper fansMapper ;

    @Autowired
    private FansMapperCustom fansMapperCustom ;

    @Autowired
    private MsgService msgService ;

    @Autowired
    public RabbitTemplate rabbitTemplate ;

    @Autowired
    private Sid sid ;

    @Override
    @Transactional
    public void createFollow(String myId, String vlogerId) {

        String fid = sid.nextShort();

        Fans fans = new Fans();

        fans.setId(fid);
        fans.setFanId(myId);
        fans.setVlogerId(vlogerId);

        // 判断视频发布者是否事先关注过观看者
        Fans vloger = queryFansRealation(vlogerId, myId);
        if ( vloger != null){
            fans.setIsFanFriendOfMine(YesOrNo.YES.type);
            vloger.setIsFanFriendOfMine(YesOrNo.YES.type);
            fansMapper.updateByPrimaryKey(vloger);
        }else {
            fans.setIsFanFriendOfMine(YesOrNo.NO.type);
        }
        fansMapper.insert(fans);

        // 系统消息 ： 关注
        MessageMO messageMO = new MessageMO();
        // msgService.createMsg(myId,vlogerId, MessageEnum.FOLLOW_YOU.type, null);
        messageMO.setFromUserId(myId);
        messageMO.setToUserId(vlogerId);
        // 使用消息队列解耦
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MSG
                ,"sys.msg."+MessageEnum.FOLLOW_YOU.enValue
                ,JsonUtils.objectToJson(messageMO));

    }

    @Override
    @Transactional
    public void cancelFollow(String myId, String vlogerId) {
        // 判断是否有关系
        Fans fans = queryFansRealation(myId, vlogerId);
        if (fans != null && fans.getIsFanFriendOfMine().equals(YesOrNo.YES.type)){
            Fans pendingFan = queryFansRealation(vlogerId,myId);
            pendingFan.setIsFanFriendOfMine(YesOrNo.NO.type);
            fansMapper.updateByPrimaryKeySelective(pendingFan) ;
        }
        // 删除自己的关注关联表记录
        fansMapper.delete(fans) ;
    }

    @Override
    public boolean queryFollow(String myId, String vlogerId) {
        Fans vloger = queryFansRealation(myId, vlogerId);
        return vloger != null;
    }

    @Override
    public PagedGridResult queryMyFollows(String myId,
                                          Integer page,
                                          Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("myId", myId);

        PageHelper.startPage(page, pageSize);

        List<VlogerVO> list = fansMapperCustom.queryMyFollows(map);

        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult queryMyFans(String myId,
                                       Integer page,
                                       Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("myId", myId);

        PageHelper.startPage(page, pageSize);

        List<FansVO> list = fansMapperCustom.queryMyFans(map);

        for (FansVO f : list) {
            String relationship = redis.get(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" + f.getFanId());
            if (StringUtils.isNotBlank(relationship) && relationship.equalsIgnoreCase("1")) {
                f.setFriend(true);
            }
        }

        return setterPagedGrid(list, page);
    }


    public Fans queryFansRealation(String fanId , String vlogerId){
        Example example = new Example(Fans.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("vlogerId", vlogerId);
        criteria.andEqualTo("fanId", fanId);

        List list =  fansMapper.selectByExample(example);

        Fans fan = null;
        if (list != null && list.size() > 0 && !list.isEmpty()) {
            fan = (Fans)list.get(0);
        }

        return fan ;
    }
}
