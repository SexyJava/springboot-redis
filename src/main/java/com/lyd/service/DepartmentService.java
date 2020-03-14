package com.lyd.service;

import com.lyd.bean.Department;
import com.lyd.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
@CacheConfig(cacheNames = "dept"/*,cacheManager = "deptCacheManager"*/)
@Service
public class DepartmentService {
    @Autowired
    DepartmentMapper departmentMapper;

    @Qualifier("deptCacheManager")
    @Autowired
    RedisCacheManager deptCacheManager;
    /**
     * 缓存的数据能存入redis
     * 第二次从缓存中查询不能反序列回来
     * 存的是dept的json数据；CacheManager默认使用new Jackson2JsonRedisSerializer<Employee>(Employee.class))操作redis
     *
     * @param id
     * @return
     */
    //@Cacheable
    //public Department getDeptById(Integer id){
    //    System.out.println("查询部门"+id);
    //    Department department = departmentMapper.getDeptById(id);
    //    return department;
    //}

    //使用缓存管理器，进行api操作
    public Department getDeptById(Integer id){
        System.out.println("查询部门"+id);
        Department department = departmentMapper.getDeptById(id);
        //获取某个缓存
        Cache dept = deptCacheManager.getCache("dept");
        dept.put("dept:1",department);
        return department;
    }
}
