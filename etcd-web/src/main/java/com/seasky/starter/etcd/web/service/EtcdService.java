package com.seasky.starter.etcd.web.service;

import com.seasky.starter.etcd.web.utils.EtcdInstance;
import com.seasky.starter.etcd.web.utils.ProperUtils;
import io.etcd.jetcd.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class EtcdService {

    @Autowired
    private EtcdInstance etcdInstance;

    private static final Logger logger = LoggerFactory.getLogger(EtcdService.class);


    public void uploadEtcd(MultipartFile file, String projectName) {
        try {
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            byte[] bytes = file.getBytes();
            Properties properties = ProperUtils.getProperties(suffix, bytes);
            logger.info("Properties:  " + properties.toString());
            Set<Map.Entry<Object, Object>> entries = properties.entrySet();
            for (Map.Entry<Object, Object> entry : entries) {
                String proKey = String.valueOf(entry.getKey());
                String proValue = String.valueOf(entry.getValue());
                etcdInstance.putEtcdSource(projectName + "/" + proKey, proValue);
            }
        } catch (Exception e) {

        }
    }

    public Map<String, String> getEtcdValues(String projectName) {
        List<KeyValue> etcdKeyWithPrefix = etcdInstance.getEtcdKeyWithPrefix(projectName);
        Map<String, String> map = new HashMap<>(etcdKeyWithPrefix.size());
        for (KeyValue keyWithPrefix : etcdKeyWithPrefix) {
            String key = new String(keyWithPrefix.getKey().getBytes());
            key = key.substring(key.lastIndexOf("/") + 1);
            String value = new String(keyWithPrefix.getValue().getBytes());
            map.put(key, value);
        }
        return map;
    }

    public Map<String, String> getEtcdValue(String projectName, String sourceKey) {
        KeyValue etcdKey = etcdInstance.getEtcdKey(projectName + "/" + sourceKey);
        Map<String, String> map = new HashMap<>(1);
        String key = new String(etcdKey.getKey().getBytes());
        key = key.substring(key.lastIndexOf("/") + 1);
        String value = new String(etcdKey.getValue().getBytes());
        map.put(key, value);
        return map;
    }


    public void addEtcdValue(String projectName, String etcdKey, String etcdValue) {
        etcdInstance.putEtcdSource(projectName + "/" + etcdKey, etcdValue);
    }

    public void delEtcdValue(String projectName, String etcdKey) {
        etcdInstance.delEtcdByKey(projectName + "/" + etcdKey);
    }

    public List<String> getProjectNames(){
        return Arrays.asList("springBootWeb","springBootService","springBootDao",
                "springWeb","springService","springDao",
                "BootWeb","BootService","BootDao",
                "web","service","dao", "webweb","serviceservice","daodao", "webwebweb","serviceserviceservice","daodaodao", "web4","service4","dao4",
                "BootBootWeb","BootBootService","BootBootDao","BootBootBootWeb","BootBootBootService","BootBootBootBootDao",
                "EtcdAutoConfiguration","EtcdConfig","EtcdValue","EtcdInstance","EtcdProperties","EtcdRunner","ProperUtils",
                "templates","commons","emp","error","etcd","edit.html","listEtcd.html","listProject.html","upload.html","resources","dashboard.html",
                "login.html","success.html","application.properties","EtcdWebApplicationTests","java");
    }
}
