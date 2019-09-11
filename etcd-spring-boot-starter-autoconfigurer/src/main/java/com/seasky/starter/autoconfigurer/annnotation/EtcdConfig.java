package com.seasky.starter.autoconfigurer.annnotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EtcdConfig {

}
