package com.forestfull.helper.config;

import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.mybatis.spring.boot.autoconfigure.SqlSessionFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfig {

    @Bean
    SqlSessionFactoryBeanCustomizer sqlSessionFactoryBeanCustomizer(){
        return bean -> bean.setVfs(SpringBootVFS.class);
    }
}
