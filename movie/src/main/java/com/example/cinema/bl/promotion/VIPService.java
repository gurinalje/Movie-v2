package com.example.cinema.bl.promotion;

import com.example.cinema.vo.VIPCardForm;
import com.example.cinema.vo.ResponseVO;



/**
 * Created by liying on 2019/4/14.
 */

public interface VIPService {

    ResponseVO addVIPCard(int userId);

    ResponseVO getCardById(int id);

    ResponseVO getVIPInfo();

    ResponseVO charge(VIPCardForm vipCardForm);

    ResponseVO getCardByUserId(int userId);

    ResponseVO issueVIPStrategy(int chargeLimit, int giftAmount);

    ResponseVO changeVIPStrategy(int vipStrategyId, int chargeLimit, int giftAmount);

    public ResponseVO getAllVipStrategy();

    ResponseVO deleteVIPStrategy(int vipStrategyId);

    ResponseVO getVipByMoney(int money);

    ResponseVO getAllVipCards();
}
