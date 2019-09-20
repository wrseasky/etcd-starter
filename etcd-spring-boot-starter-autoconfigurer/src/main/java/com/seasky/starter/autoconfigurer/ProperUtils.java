package com.seasky.starter.autoconfigurer;


import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayInputStream;
import java.util.Properties;

public class ProperUtils {

    private static Properties properties = new Properties();

    public static Properties getProperties(String type, byte[] bytes) {
        if ("properties".equals(type)) {
            return getPropertiesByPro(bytes);
        } else if ("yml".equals(type)) {
            return getPropertiesByYaml(bytes);
        }
        return null;
    }

    private static Properties getPropertiesByPro(byte[] bytes) {
        try {
            ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
            Properties proper = new Properties();
            proper.load(bai);
            bai.close();
            return proper;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Properties getPropertiesByYaml(byte[] bytes) {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ByteArrayResource(bytes));
        return yaml.getObject();
    }

    public static String getValue(String key) {
        Object obj = properties.get(key);
        if(obj == null){
            obj = "";
        }
        return String.valueOf(obj);
    }

    public static void putValue(String key, String value) {
        properties.put(key, value);
    }

    public static Properties getValues() {
        return properties;
    }

    private static void loadYamlValue(byte[] bytes) {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ByteArrayResource(bytes));
        Properties proper = yaml.getObject();
        properties.putAll(proper);
    }

    private static void loadPropertiesValue(byte[] bytes) {
        try {
            ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
            Properties proper = new Properties();
            proper.load(bai);
            bai.close();
            properties.putAll(proper);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
