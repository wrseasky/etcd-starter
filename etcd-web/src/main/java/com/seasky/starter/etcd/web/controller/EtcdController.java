package com.seasky.starter.etcd.web.controller;

import com.seasky.starter.etcd.web.service.EtcdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
public class EtcdController {
    private static final Logger logger = LoggerFactory.getLogger(EtcdController.class);


    @Autowired
    private EtcdService etcdService;

    @RequestMapping("/toUpload")
    public String toUploadPage(Model model) {
        List<String> projectNames = etcdService.getProjectNames();
        model.addAttribute("projectNames",projectNames);
        return "etcd/upload";
    }

    @RequestMapping("/upload")
    public String uploadEtcdFile(@RequestParam("file") MultipartFile file, @RequestParam("projectName") String projectName) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }
        etcdService.uploadEtcd(file, projectName);
        return "redirect:/etcd/" + projectName;
    }

    @GetMapping("/listProject")
    public String toListPage(Model model) {
        List<String> projectNames = etcdService.getProjectNames();
        model.addAttribute("projectNames",projectNames);
        return "etcd/listProject";
    }

    @GetMapping("/etcd/{projectName}")
    public String index(@PathVariable("projectName") String projectName, Model model) {
        Map<String, String> etcdValus = etcdService.getEtcdValues(projectName);
        model.addAttribute("etcdValues", etcdValus);
        model.addAttribute("projectName", projectName);
        return "etcd/listEtcd";
    }

    @GetMapping("/etcd")
    public String toAddPage(Model model) {
        List<String> projectNames = etcdService.getProjectNames();
        model.addAttribute("projectNames",projectNames);
        return "etcd/edit";
    }

    @GetMapping("/etcd/{projectName}/{key}")
    public String toEditPage(@PathVariable("projectName") String projectName, @PathVariable("key") String key, Model model) {
        Map<String, String> etcdValue = etcdService.getEtcdValue(projectName, key);
        model.addAttribute("projectName", projectName);
        model.addAttribute("etcdKey", key);
        model.addAttribute("etcdValue", etcdValue.get(key));

        List<String> projectNames = etcdService.getProjectNames();
        model.addAttribute("projectNames",projectNames);
        return "etcd/edit";
    }

    /**
     * 添加 修改
     * @param projectName
     * @param etcdKey
     * @param etcdValue
     * @return
     */
    @PostMapping(value = "/add", produces="text/plain;charset=UTF-8")
    public String addEtcdValue(String projectName, String etcdKey, String etcdValue) {
        logger.info("etcdKey:  " + etcdKey + ",  etcdValue:  " + etcdValue);
        etcdService.addEtcdValue(projectName, etcdKey, etcdValue);
        return "redirect:/etcd/" + projectName;
    }


    @DeleteMapping("/etcd/{projectName}/{key}")
    public String delete(@PathVariable("projectName") String projectName, @PathVariable("key") String key, Model model) {
        etcdService.delEtcdValue(projectName, key);
        return "redirect:/etcd/" + projectName;
    }


}
