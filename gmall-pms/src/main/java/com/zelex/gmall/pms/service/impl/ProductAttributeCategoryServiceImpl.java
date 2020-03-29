package com.zelex.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zelex.gmall.pms.entity.ProductAttributeCategory;
import com.zelex.gmall.pms.mapper.ProductAttributeCategoryMapper;
import com.zelex.gmall.pms.service.ProductAttributeCategoryService;
import com.zelex.gmall.vo.PageInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 产品属性分类表 服务实现类
 * </p>
 *
 * @author zelex
 * @since 2020-01-07
 */
@Component
@Service
public class ProductAttributeCategoryServiceImpl extends ServiceImpl<ProductAttributeCategoryMapper, ProductAttributeCategory> implements ProductAttributeCategoryService {
    @Autowired
    ProductAttributeCategoryMapper productAttributeCategoryMapper;
    @Override
    public PageInfoVo productAttributeCategoryPageInfo(Integer pageNum, Integer pageSize) {
        IPage<ProductAttributeCategory> page = productAttributeCategoryMapper.selectPage(new Page<ProductAttributeCategory>(pageNum, pageSize), null);
        PageInfoVo vo = PageInfoVo.getVo(page,pageSize.longValue());//getVo是定义了一个工具类方法，避免每次都new取封装对象
        //返回分页数据对象
        return vo;
    }
}
