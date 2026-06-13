package com.example.cinema.data.user;
import com.example.cinema.po.HistoryItem;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
@Mapper
public interface HistoryMapper {
      List<HistoryItem> getHistoryByUserId(int userId);

     void insertHistory(HistoryItem history);
}
