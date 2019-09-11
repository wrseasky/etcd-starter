package com.seasky.starter.autoconfigurer;

import com.seasky.starter.autoconfigurer.annnotation.EtcdConfig;
import com.seasky.starter.autoconfigurer.annnotation.EtcdValue;
import org.springframework.stereotype.Component;

@Component
@EtcdConfig
public class Bean {
    @EtcdValue(value = "com.baidu.com",defaultValue = "zhangsan")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
