package com.imooc.service.impl;


import com.imooc.enums.YesOrNo;
import com.imooc.mapper.FansMapper;
import com.imooc.pojo.Fans;
import com.imooc.service.FansService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class FansServiceImpl implements FansService {

    @Autowired
    private FansMapper fansMapper ;

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
        return vloger != null ;
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
