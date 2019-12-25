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

    public List<Lottery> getlotteries(String[] strReds);

    public Long getlotteryCount();

}
