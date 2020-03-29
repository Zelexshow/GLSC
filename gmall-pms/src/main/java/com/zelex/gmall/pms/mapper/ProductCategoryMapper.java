package com.zelex.gmall.pms.mapper;

import com.zelex.gmall.pms.entity.ProductCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zelex.gmall.vo.product.PmsProductCategoryWithChildrenItem;

import java.util.List;

/**
 * <p>
 * 产品分类 Mapper 接口
 * </p>
 *
 * @author zelex
 * @since 2020-01-07
 */
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {
    //自己添加的方法
    List<PmsProductCategoryWithChildrenItem> listCatelogWithChilder(Integer id);
}
