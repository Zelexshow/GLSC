package com.zelex.gmall.pms;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 1、配置整合dubbo
 * 2、配置整合mybatis plus
 *
 * logstash整合
 * 1）导jar
 * 2）导日志配置
 * 3）在kibana里面建立好日志的索引，就可以可视化检索
 *
 * springboot里面充满了各中设计模式
 * 像在整合redis模块中就采用了：模板模式
 *  JdbcTemplate、RedisTemplate、MongoTemplate
 *
 *  Springboot的原理：
 *  引入一个场景，猜这个场景的xxAutoConfiguration,
 *  帮我们注入这个组件，这个场景配置信息都在xxProperties中说明了（例如：prefix = "spring.redis"）使用哪种
 *  前缀配置
 *
 *  2)、整合Redis两大步
 *   1）、导入starter-data-redis jar包
 *   2）、application.properties配置与 spring.redis相关的
 *   注意：
 *      RedisTemplate；存数据默认使用jdk的方式序列化存过去。
 *      我们推荐都应该存成json；
 *      做法：
 *          将默认的序列化器改为json的
 *
 * 2、如果发现事务加不上。开启基于注解的事务功能  @EnableTransactionManagement
 *  如果要真的开启什么功能就显式的加上这个注解。。。。
 *
 * 3、事务的最终解决方案；
 *    1）、普通加事务。导入jdbc-starter，@EnableTransactionManagement，加@Transactional
 *    2）、方法自己调自己类里面的加不上事务。
 *          1）、导入aop包，开启代理对象的相关功能
 *               <dependency>
 *                   <groupId>org.springframework.boot</groupId>
 *                   <artifactId>spring-boot-starter-aop</artifactId>
 *               </dependency>
 *          2）、获取到当前类真正的代理对象，去掉方法即可
 *                 1）、@EnableAspectJAutoProxy(exposeProxy = true):暴露代理对象
 *                 2）、获取代理对象；
 *
 * 复习：事务传播行为，
 * ====================================================================
 * 隔离级别：解决读写加锁问题的（数据底层的方案）。  可重复读（快照）；
 *
 * 读未提交：
 * 读已提交：
 * 可重复读：
 * 串行化：
 *
 * ===========================================================
 * 异常回滚策略
 * 异常：
 *      运行时异常（不受检查异常）
 *          ArithmeticException ......
 *      编译时异常（受检异常）
 *            FileNotFound；1）要么throw要么try- catch
 *
 * 运行的异常默认是一定回滚
 * 编译时异常默认是不回滚的；
 *      rollbackFor：指定哪些异常一定回滚的。
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableDubbo
@MapperScan(basePackages = "com.zelex.gmall.pms.mapper")
@SpringBootApplication
public class GmallPmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallPmsApplication.class, args);
    }

}
