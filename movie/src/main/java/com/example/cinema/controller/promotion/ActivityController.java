package com.example.cinema.controller.promotion;

import com.example.cinema.bl.promotion.ActivityService;
import com.example.cinema.vo.ActivityForm;
import com.example.cinema.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * Created by liying on 2019/4/20.
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    ActivityService activityService;

    @PostMapping("/publish")
    public ResponseVO publishActivity(@RequestBody ActivityForm activityForm){
        return activityService.publishActivity(activityForm);
    }
    @GetMapping("/get")
    public ResponseVO getActivities(){
        return activityService.getActivities();
    }
    // 👇 新增：提供给前端的下线 API
    // 👇 修改这里的路径，只保留 /delete
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseVO deleteActivity(@RequestParam int activityId) {
        return activityService.deleteActivity(activityId);
    }
}
