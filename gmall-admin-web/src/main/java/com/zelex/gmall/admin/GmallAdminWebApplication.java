package com.zelex.gmall.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
 * 不进行数据源的自动配置
 *
 * 如果导入的依赖，引入一个自动配置场景：
 * 1）这个场景自动默认配置生效，我们必须配置他
 * 2）不想配置：
 * i:引入的时候排除这个场景依赖
 * ii：排除这个场景的自动配置类
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GmallAdminWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallAdminWebApplication.class, args);
    }

}
