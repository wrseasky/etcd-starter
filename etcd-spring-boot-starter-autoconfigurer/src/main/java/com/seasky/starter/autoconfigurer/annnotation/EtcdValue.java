package com.seasky.starter.autoconfigurer.annnotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EtcdValue {
    String value();

    String defaultValue();

}
