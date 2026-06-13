package com.example.cinema.bl.sales;

import com.example.cinema.po.RefundPolicy;
import com.example.cinema.vo.ResponseVO;

/**
 * 退票服务接口
 * 职责：退票操作、退票策略管理
 */
public interface RefundService {

    /**
     * 根据电影票ID退票
     *
     * @param ticketId 电影票ID
     * @return 退票结果（含退回金额）
     */
    ResponseVO refundTicket(int ticketId);

    /**
     * 获取所有退票策略
     *
     * @return 退票策略列表
     */
    ResponseVO getAllPolicy();

    /**
     * 修改退票策略
     *
     * @param refundPolicy 退票策略
     * @return 操作结果
     */
    ResponseVO updateRefundPolicy(RefundPolicy refundPolicy);

    /**
     * 新增退票策略
     *
     * @param refundPolicy 退票策略
     * @return 操作结果
     */
    ResponseVO addRefundPolicy(RefundPolicy refundPolicy);

    /**
     * 根据ID删除退票策略
     *
     * @param id 策略ID
     * @return 操作结果
     */
    ResponseVO deleteRefundPolicy(int id);

    /**
     * 获取退票时间限制（小时）
     *
     * @return 限制时间
     */
    ResponseVO getRefundInfo();

    /**
     * 更新退票时间限制
     *
     * @param limitTime 限制时间（小时）
     * @return 操作结果
     */
    ResponseVO updateRefundInfo(int limitTime);
}
