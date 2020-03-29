package com.zelex.gmall.oms.service;

import com.zelex.gmall.oms.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zelex.gmall.vo.order.OrderConfirmVo;
import com.zelex.gmall.vo.order.OrderCreateVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author zelex
 * @since 2020-01-07
 */
public interface OrderService extends IService<Order> {

    /**
     * 订单确认
     * @param id
     * @return
     */
    OrderConfirmVo orderConfirm(Long id);


    OrderCreateVo createOrder(BigDecimal totalPrice, Long addressId, String note);

    String pay(String orderSn, String accessToken);

    /**
     * 解析最终的支付结果
     * @param params
     * @return
     */
    String resolvePayResult(Map<String, String> params);
}
