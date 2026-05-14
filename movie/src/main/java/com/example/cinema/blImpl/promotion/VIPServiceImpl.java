package com.example.cinema.blImpl.promotion;

import com.example.cinema.bl.promotion.VIPService;
import com.example.cinema.data.promotion.VIPCardMapper;
import com.example.cinema.data.user.AccountMapper;
import com.example.cinema.data.user.HistoryMapper;
import com.example.cinema.po.VIP_Strategy;
import com.example.cinema.po.historyItem;
import com.example.cinema.vo.VIPCardForm;
import com.example.cinema.po.User;
import com.example.cinema.po.VIPCard;
import com.example.cinema.vo.ResponseVO;
import com.example.cinema.vo.VIPInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class VIPServiceImpl implements VIPService {
    @Autowired
    VIPCardMapper vipCardMapper;
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    HistoryMapper historyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO addVIPCard(int userId) {
        User user = accountMapper.getAccountById(userId);
        VIPCard vipCard = new VIPCard();
        vipCard.setUserId(userId);
        vipCard.setBalance(0);
        vipCard.setTotal(25);
        vipCard.setName(user != null ? user.getUsername() : "VIP用户");

        try {
            vipCardMapper.insertOneCard(vipCard);
            int realNewCardId = vipCard.getId();
            try {
                historyItem history = new historyItem();
                history.setUserId(userId);
                history.setKind(0);
                history.setMoney(-25.0);
                history.setDescription("购买会员卡");
                historyMapper.insertHistory(history);
            } catch (Exception e) { System.out.println("开卡流水插入失败: " + e.getMessage()); }

            return ResponseVO.buildSuccess(vipCardMapper.selectCardById(realNewCardId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("开卡失败");
        }
    }

    @Override
    public ResponseVO getCardById(int id) {
        try {
            return ResponseVO.buildSuccess(vipCardMapper.selectCardById(id));
        } catch (Exception e) {
            return ResponseVO.buildFailure("失败");
        }
    }

    @Override
    public ResponseVO getVIPInfo() {
        VIPInfoVO vipInfoVO = new VIPInfoVO();
        try {
            List<VIP_Strategy> strategies = vipCardMapper.getVIP_Strategy();
            List<String> description = new ArrayList<>();
            if (strategies != null) {
                for (int i = 0; i < strategies.size(); i++) {
                    description.add(strategies.get(i).getDescription());
                }
            }
            vipInfoVO.setDescription(description);
            // 🚀 核心修复：把从数据库查出来的真实策略对象列表传给前端！
            vipInfoVO.setStrategies(strategies);
            vipInfoVO.setPrice(25.0);
            return ResponseVO.buildSuccess(vipInfoVO);
        } catch (Exception e) {
            vipInfoVO.setPrice(25.0);
            return ResponseVO.buildSuccess(vipInfoVO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO charge(VIPCardForm vipCardForm) {
        VIPCard vipCard = vipCardMapper.selectCardById(vipCardForm.getVipId());
        if (vipCard == null) return ResponseVO.buildFailure("会员卡不存在");

        double addedBalance = vipCardForm.getAmount();
        try {
            List<VIP_Strategy> strategies = vipCardMapper.getVIP_Strategy();
            int maxGift = 0;
            if (strategies != null) {
                for (VIP_Strategy s : strategies) {
                    if (s.getChargeLimit() > 0 && vipCardForm.getAmount() >= s.getChargeLimit()) {
                        int times = (int) (vipCardForm.getAmount() / s.getChargeLimit());
                        int gift = times * s.getGiftAmount();
                        if (gift > maxGift) maxGift = gift;
                    }
                }
            }
            addedBalance += maxGift;
        } catch (Exception e) {
            System.out.println("策略匹配异常，将按原价充值: " + e.getMessage());
        }

        vipCard.setBalance(vipCard.getBalance() + addedBalance);
        vipCard.setTotal(vipCard.getTotal() + vipCardForm.getAmount());

        try {
            vipCardMapper.updateCardBalance(vipCardForm.getVipId(), vipCard.getBalance());
            vipCardMapper.updateCardTotal(vipCardForm.getVipId(), vipCard.getTotal());
            try {
                historyItem history = new historyItem();
                history.setUserId(vipCard.getUserId());
                history.setKind(1);
                history.setMoney(vipCardForm.getAmount());
                history.setDescription("会员卡充值 (实际到账 ￥" + addedBalance + ")");
                historyMapper.insertHistory(history);
            } catch (Exception e) { System.out.println("充值流水插入失败: " + e.getMessage()); }

            return ResponseVO.buildSuccess(vipCardMapper.selectCardById(vipCardForm.getVipId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("充值失败");
        }
    }

    @Override
    public ResponseVO getCardByUserId(int userId) {
        try {
            VIPCard vipCard = vipCardMapper.selectCardByUserId(userId);
            if (vipCard == null) return ResponseVO.buildFailure("用户卡不存在");
            return ResponseVO.buildSuccess(vipCard);
        } catch (Exception e) { return ResponseVO.buildFailure("失败"); }
    }

    @Override
    public ResponseVO issueVIP_Strategy(int chargeLimit, int giftAmount) {
        try {
            VIP_Strategy vip_strategy = new VIP_Strategy();
            vip_strategy.setGiftAmount(giftAmount);
            vip_strategy.setChargeLimit(chargeLimit);
            int VIP_Strategy_ID = vipCardMapper.addVIP_Strategy(vip_strategy);
            return ResponseVO.buildSuccess(vipCardMapper.selectVIP_StrategyById(VIP_Strategy_ID));
        } catch (Exception e) { return ResponseVO.buildFailure("失败"); }
    }

    @Override
    public ResponseVO changeVIP_Strategy(int VIP_Strategy_ID, int chargeLimit, int giftAmount) {
        try {
            vipCardMapper.changeVIP_Strategy(VIP_Strategy_ID, chargeLimit, giftAmount);
            return ResponseVO.buildSuccess(vipCardMapper.selectVIP_StrategyById(VIP_Strategy_ID));
        } catch (Exception e) { return ResponseVO.buildFailure("失败"); }
    }

    @Override
    public ResponseVO getAllVip_Strategy() {
        try {
            List<VIP_Strategy> list = vipCardMapper.getVIP_Strategy();
            return (ResponseVO.buildSuccess(list));
        } catch (Exception e) { return ResponseVO.buildFailure("失败"); }
    }

    @Override
    public ResponseVO deleteVIP_Strategy(int VIP_Strategy_ID) {
        try {
            vipCardMapper.deleteVIP_Strategy(VIP_Strategy_ID);
            return ResponseVO.buildSuccess();
        } catch (Exception e) { return ResponseVO.buildFailure("失败"); }
    }

    @Override
    public ResponseVO getVipByMoney(int money) {
        try {
            List<VIPCard> list1 = vipCardMapper.selectAllVip();
            List<VIPCard> list2 = new ArrayList<>();
            for (VIPCard it : list1) {
                if (it.getTotal() >= money) list2.add(it);
            }
            return ResponseVO.buildSuccess(list2);
        } catch (Exception e) { return ResponseVO.buildFailure("失败"); }
    }
}