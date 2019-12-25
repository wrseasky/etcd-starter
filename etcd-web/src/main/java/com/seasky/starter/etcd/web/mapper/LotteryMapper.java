package com.seasky.starter.etcd.web.mapper;

import com.seasky.starter.etcd.web.entity.Lottery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface LotteryMapper {

    int insertLottery(List<Lottery> list);

    String getlotteryByWinNumber(String strWinNumber);

    public List<Lottery> getlotteries(@Param("strReds") String[] strReds,
                                      @Param("strWinNumber")String strWinNumber,
                                      @Param("dtWinTimeStart")String dtWinTimeStart, @Param("dtWinTimeEnd")String dtWinTimeEnd);

    public Long getlotteryCount();

}
