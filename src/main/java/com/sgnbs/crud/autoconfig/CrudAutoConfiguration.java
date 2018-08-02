package com.sgnbs.crud.autoconfig;


import com.sgnbs.crud.cache.CrudCache;
import com.sgnbs.crud.controller.CrudController;
import com.sgnbs.crud.util.CrudUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CrudProperties.class)
@ConditionalOnClass({CrudCache.class})
public class CrudAutoConfiguration {

    @Bean
    public CrudCache getCrudSerice(CrudProperties crudProperties,ApplicationContext applicationContext){
        return new CrudCache(crudProperties,applicationContext);
    }

    @Bean
    public CrudUtil getCrudUtil(CrudProperties crudProperties, ApplicationContext applicationContext){
        return new CrudUtil(crudProperties,applicationContext);
    }

    @Bean
    public CrudController getCrudController(){
        return new CrudController();
    }
}
