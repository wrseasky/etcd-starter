package com.seasky.starter.autoconfigurer.etcd;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.etcd")
public class EtcdProperties {
    private String url;

    private String watchPoint;

    public String getWatchPoint() {
        return watchPoint;
    }

    public void setWatchPoint(String watchPoint) {
        this.watchPoint = watchPoint;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
