package com.zelex.gmall.pms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zelex.gmall.pms.entity.ProductLadder;
import com.zelex.gmall.pms.mapper.ProductLadderMapper;
import com.zelex.gmall.pms.service.ProductLadderService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产品阶梯价格表(只针对同商品) 服务实现类
 * </p>
 *
 * @author zelex
 * @since 2020-01-07
 */
@Service
public class ProductLadderServiceImpl extends ServiceImpl<ProductLadderMapper, ProductLadder> implements ProductLadderService {

}
