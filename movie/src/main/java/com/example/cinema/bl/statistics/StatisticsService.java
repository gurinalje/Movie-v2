package com.example.cinema.bl.statistics;

import com.example.cinema.vo.ResponseVO;

public interface StatisticsService {
    ResponseVO getMovieBoxOffice();
    ResponseVO getAudiencePrice();
    ResponseVO getMovieScheduleTime(String date);
    ResponseVO getPopularMovies();
    ResponseVO getMostPopularStars();

    // 👇 新增：获取大屏综合数据
    ResponseVO getComprehensiveStatistics();
}