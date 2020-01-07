package com.zelex.gmall.pms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zelex.gmall.pms.entity.MemberPrice;
import com.zelex.gmall.pms.mapper.MemberPriceMapper;
import com.zelex.gmall.pms.service.MemberPriceService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品会员价格表 服务实现类
 * </p>
 *
 * @author zelex
 * @since 2020-01-07
 *
 * 1、将逆向工程的东西，先复制mapper进来
 * 2、给当前项目中手动创建一个service包，再把impl包下所有复制进来
 */
@Service
public class MemberPriceServiceImpl extends ServiceImpl<MemberPriceMapper, MemberPrice> implements MemberPriceService {

}
