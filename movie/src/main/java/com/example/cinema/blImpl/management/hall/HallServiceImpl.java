package com.example.cinema.blImpl.management.hall;

import com.example.cinema.bl.management.HallService;
import com.example.cinema.blImpl.management.schedule.ScheduleServiceImpl;
import com.example.cinema.data.management.HallMapper;
import com.example.cinema.data.management.ScheduleMapper;
import com.example.cinema.po.Hall;
import com.example.cinema.po.ScheduleItem;
import com.example.cinema.vo.HallVO;
import com.example.cinema.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @author fjj
 * @date 2019/4/12 2:01 PM
 */
@Service
public class HallServiceImpl implements HallService, HallServiceForBl {
    @Autowired
    private HallMapper hallMapper;
    @Autowired
    private ScheduleMapper scheduleMapper;

    @Override
    public ResponseVO searchAllHall() {
        try {
            return ResponseVO.buildSuccess(hallList2HallVOList(hallMapper.selectAllHall()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }

    @Override
    public Hall getHallById(int id) {
        try {
            return hallMapper.selectHallById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<HallVO> hallList2HallVOList(List<Hall> hallList){
        List<HallVO> hallVOList = new ArrayList<>();
        for(Hall hall : hallList){
            hallVOList.add(new HallVO(hall));
        }
        return hallVOList;
    }

    @Override
    public ResponseVO addHall(Hall addHallForm){
        try{
            Hall hall=new Hall();
            hall.setColumn(addHallForm.getColumn());
            hall.setName(addHallForm.getName());
            hall.setRow(addHallForm.getRow());

            // ✅ 新增：将前端传来的营业状态保存下来，如果没有传默认给 1（正常营业）
            if (addHallForm.getStatus() != null) {
                hall.setStatus(addHallForm.getStatus());
            } else {
                hall.setStatus(1);
            }

            hallMapper.insertHall(hall);//新增影厅
            List<Hall> halls=hallMapper.selectAllHall();
            return ResponseVO.buildSuccess(halls);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("添加影厅失败");
        }
    }

    @Override
    public ResponseVO updateHall(Hall editHallForm){
        int id=editHallForm.getId();
        Date nowDate=new Date();
        ScheduleServiceImpl scheduleService=new ScheduleServiceImpl();
        //7天内该影厅有排片则不能修改该影厅
        Date nextDate=scheduleService.getNumDayAfterDate(nowDate,7);
        //7天内该影厅是否有排片
        List<ScheduleItem> scheduleItems= scheduleMapper.selectSchedule(id,nowDate,nextDate);
        if(scheduleItems.size()==0){
            try {
                hallMapper.updateHall(editHallForm);
                List<Hall> halls = hallMapper.selectAllHall();
                return ResponseVO.buildSuccess(halls);//返回修改后的所有影厅
            }catch(Exception e){
                e.printStackTrace();
                return ResponseVO.buildFailure("修改影厅失败");
            }
        }
        else{
            return ResponseVO.buildFailure("修改失败：七天内该影厅已有排片安排");
        }
    }

    // ✅ 新增：这就是修复报错的最关键方法！实现了接口中定义的删除逻辑
    @Override
    public ResponseVO deleteHall(int id) {
        try {
            hallMapper.deleteHall(id);
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            // 捕获数据库外键异常，防止由于该影厅已经卖过票导致的删除崩溃
            return ResponseVO.buildFailure("删除失败，该影厅已有历史排片或售票记录！");
        }
    }
}