package com.seasky.starter.etcd.web.mapper;

import com.seasky.starter.etcd.web.entity.Lottery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface LotteryMapper {

    int insertLottery(List<Lottery> list);

    Lottery getlotteryByWinNumber(String strWinNumber);

    public List<Lottery> getlotteries(@Param("nRed1") String nRed1,
                                      @Param("nRed2") String nRed2,
                                      @Param("nRed3") String nRed3,
                                      @Param("nRed4") String nRed4,
                                      @Param("nRed5") String nRed5,
                                      @Param("nRed6") String nRed6);

    public Long getlotteryCount();

}
