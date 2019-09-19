package com.seasky.starter.autoconfigurer;

import com.seasky.starter.autoconfigurer.annnotation.EtcdConfig;
import com.seasky.starter.autoconfigurer.annnotation.EtcdValue;
import com.seasky.starter.autoconfigurer.etcd.EtcdInstance;
import com.seasky.starter.autoconfigurer.etcd.EtcdProperties;
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
import java.util.*;

public class EtcdRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(EtcdRunner.class);

    private EtcdProperties etcdProperties;
    private ApplicationContext applicationContext;
    private EtcdInstance etcdInstance;


    private static Collection<Object> beansWithAnnotation = null;
    private static Map<String, Map<Object, List<Field>>> properBeanFieldCache = new HashMap<>();


    public EtcdRunner(EtcdProperties etcdProperties, ApplicationContext applicationContext, EtcdInstance etcdInstance) {
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
     * 将值设置到对应类的Field,并缓存pro对应的类以及字段
     */
    public void setPropertiesByInvoke() {
            if (beansWithAnnotation == null) {
                beansWithAnnotation = getBeansWithAnnotation(EtcdConfig.class);
                logger.info("获取的所有标记EtcdConfig的Bean :{}", beansWithAnnotation);
            }
            for (Object bean : beansWithAnnotation) {
                Class<?> aClass = bean.getClass();
                Field[] declaredFields = aClass.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    if (declaredField.isAnnotationPresent(EtcdValue.class)) {
                        if (!declaredField.isAccessible()) {
                            declaredField.setAccessible(true);
                        }
                        EtcdValue annotation = declaredField.getAnnotation(EtcdValue.class);
                        String proper = annotation.value();

                        String realValue = ProperUtils.getValue(proper);
                        if (StringUtils.isEmpty(realValue)) {
                            realValue = annotation.defaultValue();
                            ProperUtils.putValue(proper, realValue);
                        }

                        //从属性对应的bean 以及 bean中Field缓存起来
                        Map<Object, List<Field>> objectListMap = properBeanFieldCache.get(proper);
                        if (objectListMap == null) {
                            objectListMap = new HashMap<Object, List<Field>>();
                            properBeanFieldCache.put(proper, objectListMap);
                            List<Field> fieldsCache = new ArrayList<Field>();
                            fieldsCache.add(declaredField);
                            objectListMap.put(bean, fieldsCache);
                        } else {
                            List<Field> fieldsCache = objectListMap.get(bean);
                            if (fieldsCache == null) {
                                fieldsCache = new ArrayList<Field>();
                                fieldsCache.add(declaredField);
                                objectListMap.put(bean, fieldsCache);
                            } else {
                                fieldsCache.add(declaredField);
                            }
                        }
                        //设置bean对应的属性
                        invoke(proper, declaredField, bean);
                    }
                }
            }
    }


    public void invoke(String pro, Field declaredField, Object bean) {
        try {
            String realValue = ProperUtils.getValue(pro);
            Class<?> aClass = bean.getClass();
            Class<?> type = declaredField.getType();
            if (type == String.class) {
                declaredField.set(bean, realValue);
                logger.info("类={} 通过={} 将属性={} 设值为={}", bean, pro, declaredField, realValue);
                return;
            }
            String name = declaredField.getName();
            String replace = name.substring(0, 1).toUpperCase() + name.substring(1);
            Method method = aClass.getMethod("set" + replace, type);
            //否则，通过成员变量的实际类型对应的class利用反射机制，调用其valueOf()方法，将属性文件中的字符串强制转换成需要传入的形参类型，并执行该方法为该成员变量赋值
            Method valueOf = type.getDeclaredMethod("valueOf", String.class);
            method.invoke(bean, valueOf.invoke(declaredField, realValue));
            logger.info("类={} 通过={} 将属性={} 设值为={}", bean, pro, declaredField, realValue);
        } catch (Exception e) {
            logger.info("类={} 通过={} 将属性={} 失败={}", bean, pro, declaredField, e);
        }
    }


    /**
     * 获取指定注解的所有类
     *
     * @return
     */
    public Collection<Object> getBeansWithAnnotation(Class clazz) {
        Map<String, Object> beanWhithAnnotation = applicationContext.getBeansWithAnnotation(clazz);
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
                String wholeKeyName = new String(keyValue.getKey().getBytes());
                String shortKeyName = wholeKeyName.substring(wholeKeyName.lastIndexOf("/") + 1);
                String shortValue = new String(keyValue.getValue().getBytes());
                logger.info("配置变动的键为={}  值为={}", shortKeyName, shortValue);
                //放入properties中,properties在beanFactoryProcessor中放入了environment中,所以环境变量中属性也会改变
                ProperUtils.putValue(shortKeyName, shortValue);

                Map<Object, List<Field>> objectListMap = properBeanFieldCache.get(shortKeyName);
                logger.info("获取配置对应的所有bean以及fields,{}", objectListMap);
                Set<Map.Entry<Object, List<Field>>> entries = objectListMap.entrySet();
                for (Map.Entry<Object, List<Field>> entry : entries) {
                    Object bean = entry.getKey();
                    List<Field> value = entry.getValue();
                    for (Field field : value) {
                        //更改属性对应的bean的field值
                        invoke(shortKeyName, field, bean);
                    }
                }
            }
        });

        try {
            Watch watch = etcdInstance.getClient().getWatchClient();
            WatchOption option = WatchOption.newBuilder().withPrefix(key).build();
            Watch.Watcher watcher = watch.watch(key, option, listener);
            synchronized (watcher) {
                watcher.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
