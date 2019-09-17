package com.seasky.starter.autoconfigurer;

import com.seasky.starter.autoconfigurer.etcd.EtcdInstance;
import io.etcd.jetcd.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
@ConditionalOnClass({Client.class})
@EnableConfigurationProperties(EtcdProperties.class)
@Import(MineBeanFactoryPostProcessor.class)
@ConditionalOnProperty(prefix = "spring.etcd", name = "enabled")
public class EtcdAutoConfiguration {


    private EtcdProperties etcdProperties;
    private ApplicationContext context;
    private ConfigurableEnvironment environment;

    public EtcdAutoConfiguration(EtcdProperties etcdProperties, ApplicationContext context, ConfigurableEnvironment environment) {
        this.etcdProperties = etcdProperties;
        this.context = context;
        this.environment = environment;
    }

    //    @Bean
//    public Client client() {
//        return Client.builder().endpoints("http://" + etcdProperties.getUrl() + ":" + etcdProperties.getPort()).build();
//    }
//
//    @Bean
////    public EtcdInstance etcdInstance(Client client, EtcdProperties etcdProperties) {
////        return new EtcdInstance(client, etcdProperties);
////    }


    @Bean
    public EtcdRunner etcdRunner(EtcdInstance etcdInstance) {
        System.out.println("aaaaaaaaaaaa");
        return new EtcdRunner(etcdProperties, context, environment, etcdInstance);
    }

}
