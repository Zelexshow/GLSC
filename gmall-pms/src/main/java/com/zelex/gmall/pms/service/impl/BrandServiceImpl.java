package com.zelex.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zelex.gmall.pms.entity.Brand;
import com.zelex.gmall.pms.mapper.BrandMapper;
import com.zelex.gmall.pms.service.BrandService;
import com.zelex.gmall.vo.PageInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 品牌表 服务实现类
 * </p>
 *
 * @author zelex
 * @since 2020-01-07
 */
@Service
@Component
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {
    @Autowired
    BrandMapper mapper;
    @Override
    public PageInfoVo brandPageInfo(String keyword, Integer pageNum, Integer pageSize) {
        QueryWrapper<Brand> name=null;
        if(!StringUtils.isEmpty(keyword)){
            //        自动添加百分号
            name= new QueryWrapper<Brand>().like("name", keyword);
        }
        IPage<Brand> page = mapper.selectPage(new Page<Brand>(pageNum.longValue(), pageSize.longValue()), name);
        PageInfoVo pageInfoVo=new PageInfoVo(page.getTotal(),page.getPages(),pageSize.longValue(),page.getRecords(),
                page.getCurrent());
        return pageInfoVo;
    }
}
