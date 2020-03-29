package com.zelex.gmall.ums.service;

import com.zelex.gmall.ums.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zelex.gmall.ums.entity.MemberReceiveAddress;

import java.util.List;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author zelex
 * @since 2020-01-07
 */
public interface MemberService extends IService<Member> {

    Member login(String username, String password);
    Member getMemberByAccessToken(String accessToken);

    List<MemberReceiveAddress> setMemberAddresses(Long id);

    MemberReceiveAddress getMemberAddressByAddressId(Long addressId);
}
