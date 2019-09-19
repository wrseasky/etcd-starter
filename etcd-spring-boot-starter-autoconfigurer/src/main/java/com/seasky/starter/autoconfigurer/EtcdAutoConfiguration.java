package com.seasky.starter.autoconfigurer;

import com.seasky.starter.autoconfigurer.etcd.EtcdInstance;
import com.seasky.starter.autoconfigurer.etcd.EtcdProperties;
import io.etcd.jetcd.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
@ConditionalOnClass({Client.class})
@EnableConfigurationProperties(EtcdProperties.class)
@Import(MineBeanFactoryPostProcessor.class)
@ConditionalOnProperty(prefix = "spring.etcd", name = "enabled")
public class EtcdAutoConfiguration {


    private EtcdProperties etcdProperties;
    private ApplicationContext context;

    public EtcdAutoConfiguration(EtcdProperties etcdProperties, ApplicationContext context) {
        this.etcdProperties = etcdProperties;
        this.context = context;
    }

    @Bean
    public EtcdRunner etcdRunner(EtcdInstance etcdInstance) {
        return new EtcdRunner(etcdProperties, context, etcdInstance);
    }

}
