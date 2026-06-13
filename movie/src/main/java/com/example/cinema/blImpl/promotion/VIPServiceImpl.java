package com.example.cinema.blImpl.promotion;

import com.example.cinema.bl.promotion.VIPService;
import com.example.cinema.data.promotion.VIPCardMapper;
import com.example.cinema.data.user.AccountMapper;
import com.example.cinema.data.user.HistoryMapper;
import com.example.cinema.po.VIPStrategy;
import com.example.cinema.po.HistoryItem;
import com.example.cinema.vo.VIPCardForm;
import com.example.cinema.po.User;
import com.example.cinema.po.VIPCard;
import com.example.cinema.vo.ResponseVO;
import com.example.cinema.vo.VIPInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * VIP服务实现类
 * 
 * ✅ 修复：使用 BigDecimal 进行所有金额计算
 * 避免浮点精度问题
 */
@Service
public class VIPServiceImpl implements VIPService {
    private static final Logger logger = LoggerFactory.getLogger(VIPServiceImpl.class);

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
        vipCard.setBalance(BigDecimal.ZERO);
        vipCard.setTotal(VIPCard.PRICE); // ✅ 使用常量
        vipCard.setName(user != null ? user.getUsername() : "VIP用户");

        try {
            vipCardMapper.insertOneCard(vipCard);
            int realNewCardId = vipCard.getId();
            try {
                HistoryItem history = new HistoryItem();
                history.setUserId(userId);
                history.setKind(0);
                history.setMoney(VIPCard.PRICE.negate()); // ✅ 使用 BigDecimal
                history.setDescription("购买会员卡");
                historyMapper.insertHistory(history);
            } catch (Exception e) { 
                logger.error("开卡流水插入失败: userId={}", userId, e); 
            }

            return ResponseVO.buildSuccess(vipCardMapper.selectCardById(realNewCardId));
        } catch (Exception e) {
            logger.error("开卡失败: userId={}", userId, e);
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
            List<VIPStrategy> strategies = vipCardMapper.getVIPStrategy();
            List<String> description = new ArrayList<>();
            if (strategies != null) {
                for (int i = 0; i < strategies.size(); i++) {
                    description.add(strategies.get(i).getDescription());
                }
            }
            vipInfoVO.setDescription(description);
            vipInfoVO.setStrategies(strategies);
            vipInfoVO.setPrice(VIPCard.PRICE.doubleValue()); // ✅ 使用常量
            return ResponseVO.buildSuccess(vipInfoVO);
        } catch (Exception e) {
            vipInfoVO.setPrice(VIPCard.PRICE.doubleValue());
            return ResponseVO.buildSuccess(vipInfoVO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO charge(VIPCardForm vipCardForm) {
        VIPCard vipCard = vipCardMapper.selectCardById(vipCardForm.getVipId());
        if (vipCard == null) return ResponseVO.buildFailure("会员卡不存在");

        // ✅ 使用 BigDecimal 进行金额计算
        BigDecimal amount = new BigDecimal(String.valueOf(vipCardForm.getAmount()));
        BigDecimal addedBalance = amount;
        
        try {
            List<VIPStrategy> strategies = vipCardMapper.getVIPStrategy();
            BigDecimal maxGift = BigDecimal.ZERO;
            
            if (strategies != null) {
                for (VIPStrategy s : strategies) {
                    BigDecimal chargeLimit = new BigDecimal(s.getChargeLimit());
                    if (s.getChargeLimit() > 0 && amount.compareTo(chargeLimit) >= 0) {
                        // 计算倍数
                        int times = amount.divide(chargeLimit, 0, BigDecimal.ROUND_DOWN).intValue();
                        BigDecimal gift = new BigDecimal(times * s.getGiftAmount());
                        if (gift.compareTo(maxGift) > 0) {
                            maxGift = gift;
                        }
                    }
                }
            }
            addedBalance = amount.add(maxGift);
        } catch (Exception e) {
            logger.warn("策略匹配异常，将按原价充值: {}", e.getMessage());
        }

        // ✅ 使用 BigDecimal 更新余额和累计金额
        BigDecimal newBalance = vipCard.getBalance().add(addedBalance);
        BigDecimal newTotal = vipCard.getTotal().add(amount);
        
        vipCard.setBalance(newBalance);
        vipCard.setTotal(newTotal);

        try {
            vipCardMapper.updateCardBalance(vipCardForm.getVipId(), vipCard.getBalance());
            vipCardMapper.updateCardTotal(vipCardForm.getVipId(), vipCard.getTotal());
            try {
                HistoryItem history = new HistoryItem();
                history.setUserId(vipCard.getUserId());
                history.setKind(1);
                history.setMoney(amount); // ✅ 使用 BigDecimal
                history.setDescription("会员卡充值 (实际到账 ￥" + addedBalance + ")");
                historyMapper.insertHistory(history);
            } catch (Exception e) { 
                logger.error("充值流水插入失败: vipId={}", vipCardForm.getVipId(), e); 
            }

            return ResponseVO.buildSuccess(vipCardMapper.selectCardById(vipCardForm.getVipId()));
        } catch (Exception e) {
            logger.error("充值失败: vipId={}", vipCardForm.getVipId(), e);
            return ResponseVO.buildFailure("充值失败");
        }
    }

    @Override
    public ResponseVO getCardByUserId(int userId) {
        try {
            VIPCard vipCard = vipCardMapper.selectCardByUserId(userId);
            if (vipCard == null) return ResponseVO.buildFailure("用户卡不存在");
            return ResponseVO.buildSuccess(vipCard);
        } catch (Exception e) { 
            return ResponseVO.buildFailure("失败"); 
        }
    }

    @Override
    public ResponseVO issueVIPStrategy(int chargeLimit, int giftAmount) {
        try {
            VIPStrategy vip_strategy = new VIPStrategy();
            vip_strategy.setGiftAmount(giftAmount);
            vip_strategy.setChargeLimit(chargeLimit);
            int VIPStrategy_ID = vipCardMapper.addVIPStrategy(vip_strategy);
            return ResponseVO.buildSuccess(vipCardMapper.selectVIPStrategyById(VIPStrategy_ID));
        } catch (Exception e) { 
            return ResponseVO.buildFailure("失败"); 
        }
    }

    @Override
    public ResponseVO changeVIPStrategy(int VIPStrategy_ID, int chargeLimit, int giftAmount) {
        try {
            vipCardMapper.changeVIPStrategy(VIPStrategy_ID, chargeLimit, giftAmount);
            return ResponseVO.buildSuccess(vipCardMapper.selectVIPStrategyById(VIPStrategy_ID));
        } catch (Exception e) { 
            return ResponseVO.buildFailure("失败"); 
        }
    }

    @Override
    public ResponseVO getAllVipStrategy() {
        try {
            List<VIPStrategy> list = vipCardMapper.getVIPStrategy();
            return (ResponseVO.buildSuccess(list));
        } catch (Exception e) { 
            return ResponseVO.buildFailure("失败"); 
        }
    }

    @Override
    public ResponseVO deleteVIPStrategy(int VIPStrategy_ID) {
        try {
            vipCardMapper.deleteVIPStrategy(VIPStrategy_ID);
            return ResponseVO.buildSuccess();
        } catch (Exception e) { 
            return ResponseVO.buildFailure("失败"); 
        }
    }

    @Override
    public ResponseVO getVipByMoney(int money) {
        try {
            List<VIPCard> list1 = vipCardMapper.selectAllVip();
            List<VIPCard> list2 = new ArrayList<>();
            BigDecimal threshold = new BigDecimal(money);
            for (VIPCard it : list1) {
                if (it.getTotal().compareTo(threshold) >= 0) {
                    list2.add(it);
                }
            }
            return ResponseVO.buildSuccess(list2);
        } catch (Exception e) { 
            return ResponseVO.buildFailure("失败"); 
        }
    }

    @Override
    public ResponseVO getAllVipCards() {
        try {
            List<VIPCard> list = vipCardMapper.selectAllVip();
            return ResponseVO.buildSuccess(list);
        } catch (Exception e) {
            return ResponseVO.buildFailure("获取VIP列表失败");
        }
    }
}