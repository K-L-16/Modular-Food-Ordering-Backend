package com.sky.config;

import com.cloudinary.Cloudinary;
import com.sky.properties.CloudinaryProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class CloudinaryConfig {

    //方式一： 通过@Value注解一个属性一个属性的注入
//    @Value("${cloudinary.url}")
//    private String cloudinaryUrl;

    //方法二： 通过注入configurationpropertits（CloudinaryPropertire）
    @Autowired
    private CloudinaryProperties cloudinaryProperties;


    @Bean //@Bean项目启动时会自动调用这个方法，并将发发的返回值自动交给ioc容器管理（一般是第三方类需要用这个@Bean）
    @ConditionalOnMissingBean //如果ioc容器中不存在Cloudinary对象，则创建Cloudinary对象
    public Cloudinary cloudinary(){
        log.info("开始创建Cloudinary对象...");

        //获取cloudinarty properytites里的属性
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudinaryProperties.getCloudName());
        config.put("api_key", cloudinaryProperties.getApiKey());
        config.put("api_secret", cloudinaryProperties.getApiSecret());

        return new Cloudinary(config);
    }
}
