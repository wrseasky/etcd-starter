package com.seasky.starter.etcd.web.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ResourceUtils;

import java.util.List;

public class MineEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    //yaml 和properties解析器
    private final List<PropertySourceLoader> propertySourceLoaders;

    public MineEnvironmentPostProcessor() {
        this.propertySourceLoaders = SpringFactoriesLoader.loadFactories(PropertySourceLoader.class, getClass().getClassLoader());
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            for (PropertySourceLoader loader : this.propertySourceLoaders) {
                for (String fileExtension : loader.getFileExtensions()) {
                    String location = ResourceUtils.CLASSPATH_URL_PREFIX + activeProfile + "" + fileExtension;

                    try {
                        Resource[] resources = this.resourcePatternResolver.getResources(location);
                        for (Resource resource : resources) {
                            List<PropertySource<?>> propertySources = loader.load(resource.getFilename(), resource);
                            if (null != propertySources && !propertySources.isEmpty()) {
                                propertySources.forEach(environment.getPropertySources()::addLast);
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }
    }
}
