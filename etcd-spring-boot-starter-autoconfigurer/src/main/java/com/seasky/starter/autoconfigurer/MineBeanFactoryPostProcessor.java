package com.seasky.starter.autoconfigurer;

import com.seasky.starter.autoconfigurer.etcd.EtcdInstance;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.List;
import java.util.Properties;

public class MineBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ConfigurableEnvironment environment = beanFactory.getBean(ConfigurableEnvironment.class);
        String url = environment.getProperty("spring.etcd.url");
        String port = environment.getProperty("spring.etcd.port");
        String watchPoint = environment.getProperty("spring.etcd.watchPoint");

        Client client = Client.builder().endpoints("http://" + url + ":" + port).build();
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
        Properties values = ProperUtils.getValues();
        if(values.size() >0){
            MutablePropertySources mps = environment.getPropertySources();
            mps.addFirst(new PropertiesPropertySource("defaultProperties", values));
        }
    }
}
