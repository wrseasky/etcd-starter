package com.seasky.starter.autoconfigurer;

import com.seasky.starter.autoconfigurer.etcd.EtcdInstance;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.List;

public class MineBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MineBeanFactoryPostProcessor.class);

    private static String httpPrefix = "http://";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ConfigurableEnvironment environment = beanFactory.getBean(ConfigurableEnvironment.class);
        String url = environment.getProperty("spring.etcd.url");
        String watchPoint = environment.getProperty("spring.etcd.watchPoint");
        logger.info("项目启动加载配置文件属性 spring.etcd.url={} spring.etcd.watchPoint={}", url, watchPoint);
        String[] uri = url.split(",");
        for (int i = 0; i < uri.length; i++) {
            uri[i] = httpPrefix + uri[i];
        }
        Client client = Client.builder().endpoints(uri).build();
        beanFactory.registerSingleton("etcdClient", client);
        EtcdInstance etcdInstance = new EtcdInstance(client);
        beanFactory.registerSingleton("etcdInstance", etcdInstance);

        List<KeyValue> etcdKeyWithPrefix = etcdInstance.getEtcdKeyWithPrefix(watchPoint);
        for (KeyValue keyWithPrefix : etcdKeyWithPrefix) {
            String wholeKeyName = new String(keyWithPrefix.getKey().getBytes());
            String shortKeyName = wholeKeyName.substring(wholeKeyName.lastIndexOf("/") + 1);
            String shortValue = new String(keyWithPrefix.getValue().getBytes());
            ProperUtils.putValue(shortKeyName, shortValue);
        }
        MutablePropertySources mps = environment.getPropertySources();
        mps.addFirst(new PropertiesPropertySource("defaultProperties", ProperUtils.getValues()));
    }
}
