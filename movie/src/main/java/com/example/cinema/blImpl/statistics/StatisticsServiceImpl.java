package com.example.cinema.blImpl.statistics;

import com.example.cinema.bl.statistics.StatisticsService;
import com.example.cinema.data.statistics.StatisticsMapper;
import com.example.cinema.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private StatisticsMapper statisticsMapper;

    @Override
    public ResponseVO getMovieBoxOffice() {
        try { return ResponseVO.buildSuccess(statisticsMapper.selectMovieBoxOffice()); }
        catch (Exception e) { return ResponseVO.buildFailure("失败"); }
    }

    @Override
    public ResponseVO getAudiencePrice() {
        try { return ResponseVO.buildSuccess(statisticsMapper.selectAudiencePrice()); }
        catch (Exception e) { return ResponseVO.buildFailure("失败"); }
    }

    @Override
    public ResponseVO getMovieScheduleTime(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return ResponseVO.buildSuccess(statisticsMapper.selectMovieScheduleTime(sdf.parse(date)));
        } catch (Exception e) { return ResponseVO.buildFailure("失败"); }
    }

    @Override
    public ResponseVO getPopularMovies() { return ResponseVO.buildSuccess(); }

    @Override
    public ResponseVO getMostPopularStars() {
        try {
            List<String> rawStars = statisticsMapper.selectMostPopularStars(6);
            Set<String> starSet = new LinkedHashSet<>();
            for (String s : rawStars) {
                if (s == null || s.isEmpty()) continue;
                for (String name : s.split("[/\\s]+")) {
                    if (!name.trim().isEmpty()) starSet.add(name.trim());
                }
            }
            List<String> result = new ArrayList<>(starSet);
            return ResponseVO.buildSuccess(result.size() > 8 ? result.subList(0, 8) : result);
        } catch (Exception e) { return ResponseVO.buildFailure("获取明星数据失败"); }
    }

    // 🚀 新增：一键查询所有大屏数据
    @Override
    public ResponseVO getComprehensiveStatistics() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("todayOrderCount", statisticsMapper.selectTodayOrderCount());
            data.put("vipCount", statisticsMapper.selectVipCount());
            data.put("averagePrice", statisticsMapper.selectAveragePrice());
            data.put("hallPopularity", statisticsMapper.selectHallPopularity());
            data.put("sevenDaysTrend", statisticsMapper.selectSevenDaysTrend());
            data.put("typePreference", statisticsMapper.selectTypePreference());
            return ResponseVO.buildSuccess(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("获取综合统计数据失败");
        }
    }
}