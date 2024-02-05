package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.imooc.base.BaseInfoProperties;
import com.imooc.bo.UpdatedUserBO;
import com.imooc.bo.VlogBO;
import com.imooc.enums.Sex;
import com.imooc.enums.UserInfoModifyType;
import com.imooc.enums.YesOrNo;
import com.imooc.exceptions.GraceException;
import com.imooc.mapper.UsersMapper;
import com.imooc.mapper.VlogMapper;
import com.imooc.mapper.VlogMapperCustom;
import com.imooc.pojo.Users;
import com.imooc.pojo.Vlog;
import com.imooc.result.ResponseStatusEnum;
import com.imooc.service.UserService;
import com.imooc.service.VlogService;
import com.imooc.utils.DateUtil;
import com.imooc.utils.DesensitizationUtil;
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
    private Sid sid;

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
    public PagedGridResult getIndexVlogList(String search , Integer page , Integer pageSize) {
        Map<String,Object> map = new HashMap<>() ;

        // 分库分表
        PageHelper.startPage(page,pageSize) ;

        if (StringUtils.isNotBlank(search)){
            map.put("search",search) ;
        }
        List<IndexVlogVO> indexVlogList = vlogMapperCustom.getIndexVlogList(map);

        return setterPagedGrid(indexVlogList,page);
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

}
