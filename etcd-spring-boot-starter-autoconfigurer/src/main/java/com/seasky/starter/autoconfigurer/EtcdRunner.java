package com.seasky.starter.autoconfigurer;

import com.seasky.starter.autoconfigurer.annnotation.EtcdConfig;
import com.seasky.starter.autoconfigurer.annnotation.EtcdValue;
import com.seasky.starter.autoconfigurer.etcd.EtcdInstance;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

public class EtcdRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(EtcdRunner.class);

    private EtcdProperties etcdProperties;
    private ApplicationContext applicationContext;
    private EtcdInstance etcdInstance;

    public EtcdRunner(EtcdProperties etcdProperties, ApplicationContext applicationContext, ConfigurableEnvironment environment, EtcdInstance etcdInstance) {
        this.etcdProperties = etcdProperties;
        this.applicationContext = applicationContext;
        this.etcdInstance = etcdInstance;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        setPropertiesByInvoke();
        watch();
    }

    /**
     * 将从etcd获取的值载入,并将值设置到对应类的Field
     */
    public void setPropertiesByInvoke() {
        try {
            Collection<Object> beansWithAnnotation = getBeansWithAnnotation();
            for (Object bean : beansWithAnnotation) {
                Class<?> aClass = bean.getClass();
                Field[] declaredFields = aClass.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    if (declaredField.isAnnotationPresent(EtcdValue.class)) {
                        if (!declaredField.isAccessible()) {
                            declaredField.setAccessible(true);
                        }
                        EtcdValue annotation = declaredField.getAnnotation(EtcdValue.class);
                        String value = annotation.value();
                        String realValue = ProperUtils.getValue(value);
                        if (StringUtils.isEmpty(realValue)) {
                            realValue = annotation.defaultValue();
                        }

                        Class<?> type = declaredField.getType();
                        if (type == String.class) {
                            declaredField.set(bean, realValue);
                            continue;
                        }
                        String name = declaredField.getName();
                        String replace = name.substring(0, 1).toUpperCase() + name.substring(1);
                        Method method = aClass.getMethod("set" + replace, type);
                        //否则，通过成员变量的实际类型对应的class利用反射机制，调用其valueOf()方法，将属性文件中的字符串强制转换成需要传入的形参类型，并执行该方法为该成员变量赋值
                        Method valueOf = type.getDeclaredMethod("valueOf", String.class);
                        method.invoke(bean, valueOf.invoke(declaredField, realValue));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定注解的所有类
     *
     * @return
     */
    public Collection<Object> getBeansWithAnnotation() {
        Map<String, Object> beanWhithAnnotation = applicationContext.getBeansWithAnnotation(EtcdConfig.class);
        return beanWhithAnnotation.values();
    }

    /**
     * 监控
     */
    public void watch() {
        ByteSequence key = ByteSequence.from(etcdProperties.getWatchPoint(), Charset.defaultCharset());
        Watch.Listener listener = Watch.listener(response -> {
            for (WatchEvent event : response.getEvents()) {
                KeyValue keyValue = event.getKeyValue();
                String keyName = new String(keyValue.getKey().getBytes());
                String fileType = keyName.substring(keyName.lastIndexOf(".") + 1);
                ProperUtils.load(fileType, keyValue.getValue().getBytes());
                setPropertiesByInvoke();
            }
        });

        try {
            Watch watch = etcdInstance.getClient().getWatchClient();
            WatchOption option = WatchOption.newBuilder().withPrefix(key).withPrevKV(true).build();
            Watch.Watcher watcher = watch.watch(key, option, listener);
            synchronized (watcher) {
                watcher.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
