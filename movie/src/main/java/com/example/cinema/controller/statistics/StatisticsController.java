package com.example.cinema.controller.statistics;

import com.example.cinema.bl.statistics.StatisticsService;
import com.example.cinema.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/boxOffice/total")
    public ResponseVO getMovieBoxOffice(){ return statisticsService.getMovieBoxOffice(); }

    @GetMapping("/audiencePrice")
    public ResponseVO getAudiencePrice(){ return statisticsService.getAudiencePrice(); }

    @GetMapping("/scheduleTime")
    public ResponseVO getMovieScheduleTime(@RequestParam String date){ return statisticsService.getMovieScheduleTime(date); }

    @GetMapping("/popular/stars")
    public ResponseVO getPopularStars(){ return statisticsService.getMostPopularStars(); }

    // 🚀 新增：暴露大屏接口
    @GetMapping("/comprehensive")
    public ResponseVO getComprehensiveStatistics(){
        return statisticsService.getComprehensiveStatistics();
    }
}