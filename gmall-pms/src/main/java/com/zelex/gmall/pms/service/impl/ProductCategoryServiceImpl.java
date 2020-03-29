package com.zelex.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zelex.gmall.constant.SysCacheConstant;
import com.zelex.gmall.pms.entity.ProductCategory;
import com.zelex.gmall.pms.mapper.ProductCategoryMapper;
import com.zelex.gmall.pms.service.ProductCategoryService;
import com.zelex.gmall.vo.product.PmsProductCategoryWithChildrenItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 产品分类 服务实现类
 * </p>
 *
 * @author zelex
 * @since 2020-01-07
 */
@Slf4j
@Component
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {
    @Autowired
    ProductCategoryMapper categoryMapper;

    @Autowired
    RedisTemplate<Object,Object> redisTemplate;

    /**
     * 分布式缓存采用redis来做
     * @param id
     * @return
     */
    @Override
    public List<PmsProductCategoryWithChildrenItem> listCatelogWithChilder(Integer id) {
        Object cacheMenu=redisTemplate.opsForValue().get(SysCacheConstant.CATEGORY_MENU_CACHE_KEY);
        List<PmsProductCategoryWithChildrenItem> items;
        if(cacheMenu!=null){
            //缓存中有
            log.debug("菜单数据命中缓存....");
            items=( List<PmsProductCategoryWithChildrenItem>)cacheMenu;
        }else{
            items=categoryMapper.listCatelogWithChilder(id);
            redisTemplate.opsForValue().set(SysCacheConstant.CATEGORY_MENU_CACHE_KEY,items);
        }

       return items;
    }
}
