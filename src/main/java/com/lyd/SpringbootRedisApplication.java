package com.lyd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 搭建基本环境
 * 1、导入数据库文件，创建department和employee表
 * 2、创建javaBean数据
 * 3、整合mybatis操作数据库
 *      1.使用注解版mybatis
 *          1.使用@MapperScan指定mapper接口所在的包
 *4、快速使用缓存
 *  1.开启基于注解的缓存:启动类加@EnableCaching
 *  2.标注缓存注解
 *      @Cacheable
 *      @CacheEvict
 *      @CachePut
 *5、整合Redis作为缓存
 *      1.docker安装redis
 *      2.引入redis的starter
 *      3.配置redis
 *      4.测试缓存
 *          原理：CacheManager === Cache 缓存组件来实际给缓存中存取数据
 *          1.引入redis的starter，容器中保存的是RedisCacheManager
 *          2.RedisCacheManager帮我创建RedisCache来作为缓存主键，RedisCache荣国操作redis缓存数据的
 *          3.默认保存数据k-v都是Object：利用序列化来保存到redis
 *          4.如何保存Json到redis：
 *              1.引入了redis的starter，CacheManager变为RedisCacheManager：
 *              2.默认创建的RedisCacheManager操作Redis的时候使用的是RedisTemplate<Object,Object>
 *              3.RedisTemplate<Object,Object>是默认使用jdk的序列化机制，不符合自己的要求自定义CacheManager
 *          5.自定义CacheManager：RedisCacheManager
 *
 */
@MapperScan("com.lyd.mapper")
@SpringBootApplication
@EnableCaching
public class SpringbootRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootRedisApplication.class, args);
    }

}
