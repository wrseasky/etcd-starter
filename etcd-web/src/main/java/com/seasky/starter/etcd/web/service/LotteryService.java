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

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Service
public class LotteryService {

    @Autowired
    private LotteryMapper lotteryMapper;

    private static final Logger logger = LoggerFactory.getLogger(LotteryService.class);

    public void uploadLoterryFile(MultipartFile file, HttpServletRequest request) {
        String contextPath = request.getServletContext().getRealPath("uploadFiles");
        String filePath = contextPath + new Date().getTime() + file.getOriginalFilename();
        File originFile = new File(filePath);
        try {
            OutputStream os = new FileOutputStream(originFile);
            InputStream is = file.getInputStream();
            int temp;
            while ((temp = is.read()) != (-1)) {
                os.write(temp);
            }
            os.flush();
            os.close();
            is.close();
        } catch (Exception e) {
            logger.error(e.toString());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(originFile), "UTF-8"));
                    List<Lottery> lotteries = new ArrayList<>();
                    String result = "";
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    int line = 0;
                    Lottery lottery = null;
                    while ((result = bufferedReader.readLine()) != null && result.length() > 0) {
                        line++;
                        if (line == 1) continue;

                        String[] split = result.split("\t");
                        String strWinNumber = split[1];
                        String lotteryHis = lotteryMapper.getlotteryByWinNumber(strWinNumber);
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
                    originFile.delete();
                    logger.info("-----------待保存数据数量: " + lotteries.size());
                    if (lotteries.isEmpty())
                        return;
                    lotteryMapper.insertLottery(lotteries);

                } catch (Exception e) {
                    logger.error(e.toString());
                }
            }
        }).start();
    }

    public List<Lottery> getlotteries(String lockyNums) {
        String[] split = lockyNums.split(",");

        List<Lottery> lotteries = lotteryMapper.getlotteries(split);
        String prefix = "<font color='red'>";
        String suffix = "</font>";
        for (Lottery lottery : lotteries) {
            String strReds = lottery.getStrReds();
            for (String red : split) {
                if (strReds.contains(red)) {
                    strReds = strReds.replace(red, prefix + red + suffix);
                }
            }
            lottery.setStrReds(strReds);
        }
        return lotteries;
    }

    public Long getlotteryCount() {
        return lotteryMapper.getlotteryCount();
    }
}
