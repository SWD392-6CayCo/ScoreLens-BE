package com.scorelens.Service.Interface.customAnnotation;


import com.scorelens.Enums.KafkaCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



//su dung cho class, k su dung cho method, field, parameter
@Target(ElementType.TYPE)
//ton tai trong thoi gian run => runtime
@Retention(RetentionPolicy.RUNTIME)
//custom annotation
public @interface KafkaCodeMapping {
    KafkaCode value();
}
