package com.zelex.gmall.pms.service;

import com.zelex.gmall.pms.entity.Brand;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zelex.gmall.vo.PageInfoVo;

/**
 * <p>
 * 品牌表 服务类
 * </p>
 *
 * @author zelex
 * @since 2020-01-07
 */
public interface BrandService extends IService<Brand> {

    PageInfoVo brandPageInfo(String keyword, Integer pageNum, Integer pageSize);
}
