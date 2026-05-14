package com.example.cinema.data.statistics;

import com.example.cinema.po.AudiencePrice;
import com.example.cinema.po.MovieScheduleTime;
import com.example.cinema.po.MovieTotalBoxOffice;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author nju
 * @date 2019/4/16
 */
@Mapper
public interface StatisticsMapper {
    List<MovieTotalBoxOffice> selectMovieBoxOffice();
    List<AudiencePrice> selectAudiencePrice();
    List<MovieScheduleTime> selectMovieScheduleTime(Date date);
    List<String> selectMostPopularStars(int limit);

    // 👇 新增：大屏看板综合统计接口
    int selectTodayOrderCount();
    int selectVipCount();
    Double selectAveragePrice();
    List<Map<String, Object>> selectHallPopularity();
    List<Map<String, Object>> selectSevenDaysTrend();
    List<Map<String, Object>> selectTypePreference();
}