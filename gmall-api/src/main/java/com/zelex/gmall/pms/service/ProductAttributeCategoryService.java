package com.zelex.gmall.pms.service;

import com.zelex.gmall.pms.entity.ProductAttributeCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zelex.gmall.vo.PageInfoVo;

/**
 * <p>
 * 产品属性分类表 服务类
 * </p>
 *
 * @author zelex
 * @since 2020-01-07
 */
public interface ProductAttributeCategoryService extends IService<ProductAttributeCategory> {
    /**
     * 分页查询所有的属性分类
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfoVo productAttributeCategoryPageInfo(Integer pageNum, Integer pageSize);
}
