package com.seasky.starter.etcd.web.controller;

import com.seasky.starter.etcd.web.entity.Lottery;
import com.seasky.starter.etcd.web.entity.User;
import com.seasky.starter.etcd.web.service.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/lottery")
public class LotteryController {
    @Autowired
    private LotteryService lotteryService;

    @RequestMapping("/toLotteryListPage")
    public String toLotteryListPage(Model model) {
        return "lottery/lotteryList";
    }

    @RequestMapping("/toLotteryUpload")
    public String toUploadPage(Map<String, Object> map, Model model, HttpSession session) {
        //上传判断
        if (!"seasky".equals(String.valueOf(session.getAttribute("loginUser")))) {
            map.put("msg", "您无权上传文件!");
            return "lottery/lotteryList";
        }
        return "lottery/lotteryUpload";
    }

    @RequestMapping(value = "/lotteryUpload", method = RequestMethod.POST)
    public String uploadLotteryFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }
        lotteryService.uploadLoterryFile(file,request);
        return "redirect:/lottery/toLotteryUpload";
    }

    @RequestMapping(value = "/getlottery", method = RequestMethod.POST)
    public String getlotteries(@RequestParam("lockyNums") String lockyNums, Model model) {
        List<Lottery> lotteries = lotteryService.getlotteries(lockyNums);
        model.addAttribute("lotteries", lotteries);
        model.addAttribute("lockyNums", lockyNums);
        return "lottery/lotteryList";
    }

    @RequestMapping(value = "/getlottery", method = RequestMethod.GET)
    public String getlotteries() {
        return "lottery/lotteryList";
    }

    @RequestMapping(value = "/getlotteryCount", method = RequestMethod.POST)
    @ResponseBody
    public String getlotteryCount(Model model) {
        Long lotteryCount = lotteryService.getlotteryCount();
        model.addAttribute("lotteryCount", lotteryCount);
        return "" + lotteryCount;
    }

}
