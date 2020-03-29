package com.zelex.gmall.pms.mapper;

import com.zelex.gmall.pms.entity.ProductAttribute;
import com.zelex.gmall.pms.entity.ProductAttributeValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zelex.gmall.to.es.EsProductAttributeValue;

import java.util.List;

/**
 * <p>
 * 存储产品参数信息的表 Mapper 接口
 * </p>
 *
 * @author zelex
 * @since 2020-01-07
 */
public interface ProductAttributeValueMapper extends BaseMapper<ProductAttributeValue> {

    List<EsProductAttributeValue> selectProductBaseAttributeAndValue(Long id);

    List<ProductAttribute> selectProductSaleAttrName(Long id);
}
