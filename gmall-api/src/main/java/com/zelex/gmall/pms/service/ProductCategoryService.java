package com.zelex.gmall.pms.service;

import com.zelex.gmall.pms.entity.ProductCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zelex.gmall.vo.product.PmsProductCategoryWithChildrenItem;

import java.util.List;

/**
 * <p>
 * 产品分类 服务类
 * </p>
 *
 * @author zelex
 * @since 2020-01-07
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    /**
     * 查询这个菜单及其子菜单
     * @param id
     * @return
     */
    List<PmsProductCategoryWithChildrenItem> listCatelogWithChilder(Integer id);
}
