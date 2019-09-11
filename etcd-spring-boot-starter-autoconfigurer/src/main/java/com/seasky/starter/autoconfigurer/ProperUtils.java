package com.seasky.starter.autoconfigurer;


import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ProperUtils {

    static Map<String, String> values = new HashMap<String, String>();

    public static void loadPropertiesValue(byte[] bytes) {
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

    public static void loadYamlValue(byte[] bytes) {
        Yaml yaml = new Yaml();
        ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
        Map<String, String> map = (Map<String, String>) yaml.load(bai);
        values.putAll(map);
    }

    public static String getValue(String key) {
        return values.get(key);
    }

}
