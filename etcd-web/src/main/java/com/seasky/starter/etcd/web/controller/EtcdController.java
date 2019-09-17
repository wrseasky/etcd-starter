package com.seasky.starter.etcd.web.controller;

import com.seasky.starter.etcd.web.service.EtcdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
public class EtcdController {
    @Autowired
    private EtcdService etcdService;

    @RequestMapping("/file")
    public String add(Model model) {
        // thymeleaf默认就会拼串
        // classpath:/templates/xxxx.html
        return "etcd/add";
    }

    @RequestMapping("/upload")
    public String addEtcdValues(@RequestParam("file") MultipartFile file, @RequestParam("projectName") String projectName) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }
        etcdService.uploadEtcd(file, projectName);
        return "redirect:/etcd/list";
    }

    @GetMapping("/list/{projectName}")
    public String index(@PathVariable("projectName") String projectName, Model model) {
        Map<String, String> etcdValus = etcdService.getEtcdValues(projectName);
        model.addAttribute("etcdValues", etcdValus);
        model.addAttribute("projectName", projectName);
        return "etcd/list";
    }

    @GetMapping("/edit/{projectName}/{key}")
    public String edit(@PathVariable("projectName") String projectName, @PathVariable("key") String key, Model model) {
        Map<String, String> etcdValue = etcdService.getEtcdValue(projectName, key);
        model.addAttribute("projectName", projectName);
        model.addAttribute("etcdKey", key);
        model.addAttribute("etcdValue", etcdValue.get(key));
        return "etcd/edit";
    }

    /**
     * 添加
     * @param projectName
     * @param etcdKey
     * @param etcdValue
     * @return
     */
    @PostMapping("/add")
    public String addEtcdValue(String projectName, String etcdKey, String etcdValue) {
        etcdService.addEtcdValue(projectName, etcdKey, etcdValue);
        return "redirect:/list/" + projectName;
    }

/*    @PutMapping("/add")
    public String updateEtcdValue(String projectName, String etcdKey, String etcdValue) {
        etcdService.addEtcdValue(projectName, etcdKey, etcdValue);
        return "redirect:/list/" + projectName;
    }*/
}
