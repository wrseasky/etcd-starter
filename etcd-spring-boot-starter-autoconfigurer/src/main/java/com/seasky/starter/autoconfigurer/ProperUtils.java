package com.seasky.starter.autoconfigurer;


import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ProperUtils {

    private static Map<String, String> values = new HashMap<String, String>();

    public static void load(String type, byte[] bytes){
        if("properties".equals(type)){
            loadPropertiesValue(bytes);
        }else if("yaml".equals(type)){
            loadYamlValue(bytes);
        }
    }

    private static void loadPropertiesValue(byte[] bytes) {
        try {
            ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
            Properties properties = new Properties();
            properties.load(bai);
            bai.close();
            values.putAll((Map) properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadYamlValue(byte[] bytes) {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ByteArrayResource(bytes));
        Map map = (Map)yaml.getObject();
        assert map != null;
        values.putAll(map);
    }

    public static String getValue(String key) {
        return String.valueOf(values.get(key));
    }

}
