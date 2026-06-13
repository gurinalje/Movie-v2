package com.example.cinema.bl.sales;

import com.example.cinema.vo.ResponseVO;

import java.util.List;

/**
 * 支付服务接口
 * 将原 TicketServiceImpl 中的支付逻辑独立出来，降低圈复杂度
 */
public interface PaymentService {

    /**
     * 使用银行卡完成购票支付
     *
     * @param ticketIds    电影票ID列表
     * @param couponId     优惠券ID（0表示不使用）
     * @param userId       用户ID
     * @return 支付结果
     */
    ResponseVO payByBankCard(List<Integer> ticketIds, int couponId, int userId);

    /**
     * 使用VIP卡完成购票支付
     *
     * @param ticketIds    电影票ID列表
     * @param couponId     优惠券ID（0表示不使用）
     * @param userId       用户ID
     * @return 支付结果
     */
    ResponseVO payByVIPCard(List<Integer> ticketIds, int couponId, int userId);
}
