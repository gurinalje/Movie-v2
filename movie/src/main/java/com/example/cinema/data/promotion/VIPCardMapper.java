package com.example.cinema.data.promotion;

import com.example.cinema.po.VIPCard;
import com.example.cinema.po.VIPStrategy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.*;

/**
 * Created by liying on 2019/4/14.
 */
@Mapper
public interface VIPCardMapper {

    int insertOneCard(VIPCard vipCard);

    VIPCard selectCardById(int id);

    void updateCardBalance(@Param("id") int id, @Param("balance") double balance);

    void updateCardTotal(@Param("id") int id, @Param("total") double total);

    VIPCard selectCardByUserId(int userId);

    VIPStrategy selectVIPStrategyById(int id);

    int addVIPStrategy(VIPStrategy vip_strategy);

    void changeVIPStrategy(@Param("vipStrategyId") int vipStrategyId, @Param("chargeLimit") int chargeLimit, @Param("giftAmount") int giftAmount);

    List<VIPStrategy> getVIPStrategy();

    void deleteVIPStrategy(@Param("vipStrategyId") int vipStrategyId);

    List<VIPCard> selectAllVip();
}
