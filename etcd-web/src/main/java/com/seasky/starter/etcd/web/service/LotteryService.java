package com.seasky.starter.etcd.web.service;

import com.seasky.starter.etcd.web.entity.Lottery;
import com.seasky.starter.etcd.web.mapper.LotteryMapper;
import com.seasky.starter.etcd.web.utils.ProperUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Service
public class LotteryService {

    @Autowired
    private LotteryMapper lotteryMapper;

    private static final Logger logger = LoggerFactory.getLogger(LotteryService.class);

    public void uploadLoterryFile(MultipartFile file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
            List<Lottery> lotteries = new ArrayList<>();
            String result = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD");

            int line = 0;
            Lottery lottery = null;
            while ((result = bufferedReader.readLine()) != null && result.length() > 0) {
                line++;
                if (line == 1) continue;

                String[] split = result.split("\t");
                String strWinNumber = split[1];
                Lottery lotteryHis = lotteryMapper.getlotteryByWinNumber(strWinNumber);
                if (lotteryHis != null) continue;

                lottery = new Lottery();
                lottery.setStrWinNumber(strWinNumber);

                String strWinTime = split[2];
                Date dtWinTime = sdf.parse(strWinTime);
                lottery.setDtWinTime(dtWinTime);

                String redsBlues = split[3];
                String[] redBlue = redsBlues.split("\\\\");
                String reds = redBlue[0];
                lottery.setStrReds(reds);
                String[] redsSplit = reds.split(",");
                Integer nRed1 = Integer.parseInt(redsSplit[0]);
                Integer nRed2 = Integer.parseInt(redsSplit[1]);
                Integer nRed3 = Integer.parseInt(redsSplit[2]);
                Integer nRed4 = Integer.parseInt(redsSplit[3]);
                Integer nRed5 = Integer.parseInt(redsSplit[4]);
                Integer nRed6 = Integer.parseInt(redsSplit[5]);
                lottery.setnRed1(nRed1);
                lottery.setnRed2(nRed2);
                lottery.setnRed3(nRed3);
                lottery.setnRed4(nRed4);
                lottery.setnRed5(nRed5);
                lottery.setnRed6(nRed6);

                //历史资料有问题,有的蓝号两个
                String[] blues = redBlue[1].split(",");
                Integer blue = Integer.parseInt(blues[0]);
                lottery.setnBlue(blue);
                lotteries.add(lottery);
            }
            if(lotteries.isEmpty())
                return;
            lotteryMapper.insertLottery(lotteries);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    public List<Lottery> getlotteries(String lockyNums) {
        String[] split = lockyNums.split(",");
        String nRed1 = split[0];
        String nRed2 = split[1];
        String nRed3 = split[2];
        String nRed4 = split[3];
        String nRed5 = split[4];
        String nRed6 = split[5];
        return lotteryMapper.getlotteries(nRed1, nRed2, nRed3, nRed4, nRed5, nRed6);
    }

    public Long getlotteryCount() {
        return lotteryMapper.getlotteryCount();
    }
}
