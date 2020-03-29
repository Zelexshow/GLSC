package com.zelex.gmall.pms.service;

import com.zelex.gmall.pms.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zelex.gmall.pms.entity.SkuStock;
import com.zelex.gmall.to.es.EsProduct;
import com.zelex.gmall.vo.PageInfoVo;
import com.zelex.gmall.vo.product.PmsProductParam;
import com.zelex.gmall.vo.product.PmsProductQueryParam;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 * @author zelex
 * @since 2020-01-07
 */

/**
 * 根据复杂条件查询商品并返回分页数据
 */
public interface ProductService extends IService<Product> {
    /**
     * 查询商品 详情
     * @param id
     * @return
     */
    Product productInfo(Long id);

    /**
     * 根据复杂查询条件返回分页数据
     * @param productQueryParam
     * @return
     */
    PageInfoVo productPageInfo(PmsProductQueryParam productQueryParam);

    /**
     * 保存商品数据
     * @param productParam
     */
    void saveProduct(PmsProductParam productParam);
    /**
     * 批量是上下架
     * @param ids
     * @param publishStatus
     */
    void updatePublishStatus(List<Long> ids, Integer publishStatus);

    EsProduct productAllInfo(Long id);

    EsProduct productSkuInfo(Long id);

    SkuStock skuInfoById(Long skuId);

    EsProduct produSkuInfo(Long skuId);
}
