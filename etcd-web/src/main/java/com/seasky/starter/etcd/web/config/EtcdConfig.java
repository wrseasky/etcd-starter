package com.seasky.starter.etcd.web.config;

import com.seasky.starter.etcd.web.utils.EtcdInstance;
import io.etcd.jetcd.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtcdConfig {

    @Value("${spring.etcd.url}")
    private String url;

    @Bean
    public Client client(){
        String[] uri = url.split(",");
        for (int i = 0; i < uri.length; i++) {
            uri[i] = "http://" + uri[i];
        }
        return Client.builder().endpoints(uri).build();
    }

    @Bean
    public EtcdInstance etcdInstance(Client client){
        return new EtcdInstance(client);
    }

}
