package com.lyd.service;

import com.lyd.bean.Employee;
import com.lyd.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

//抽取缓存的公共配置
@CacheConfig(cacheNames = "emp",cacheManager = "empCacheManager")
@Service
public class EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 将方法的运行结果进行缓存，以后要相同的数据，直接从缓存中获取，不用调用方法
     * CacheManager管理多个Cache组件的，对缓存的真正CRUD操作在Cache组件中，每一个缓存组件有自己唯一的名字
     * 属性：
     *  cacheNames/value：指定缓存组件的名字，将方法的返回结果放在哪个缓存中，是数组的形式，可以放到多个缓存中
     *  key:缓存数据时使用的key，可以指定key的值，默认是使用方法的参数的值
     *  如果方法的参数为1那么他的key为1value为这个方法的返回值
     *  key也可以是SpEl表达式:
     *      getEmp(1):key = "#root.methodName+'['+#id+']'"
     *      "#root.args[0]"
     *      #id:参数id的值
     *      #result:获取结果（返回）值
     *  keyGenerator:key的生成器，可以自己指定key的生成的组件id,keyGenerator = "myKeyGenerator"
     *  key和keyGenerator二选一
     *  cacheManager：指定缓存管理器或者指定cacheResolver缓存解析器二选一
     *  condition:指定符合条件的情况下才缓存
     *      condition = "#id>1":参数id的值大于1的时候才进行缓存
     *  unless:否定缓存，当unless指定的条件为true，方法的返回值不会被缓存，可以获取到结果进行判断
     *      unless = "#id==2":如果参数id的值为2，结果不缓存
     *  sync：是否使用异步模式，异步模式下unless属性不可以使用
     *
     *  原理：
     *  1.自动配置类：CacheAutoConfiguration
     *  2.缓存配置类：
     *  org.springframework.boot.autoconfigure.cache.GenericCacheConfiguration
     *  org.springframework.boot.autoconfigure.cache.JCacheCacheConfiguration
     *  org.springframework.boot.autoconfigure.cache.EhCacheCacheConfiguration
     *  org.springframework.boot.autoconfigure.cache.HazelcastCacheConfiguration
     *  org.springframework.boot.autoconfigure.cache.InfinispanCacheConfiguration
     *  org.springframework.boot.autoconfigure.cache.CouchbaseCacheConfiguration
     *  org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration
     *  org.springframework.boot.autoconfigure.cache.CaffeineCacheConfiguration
     *  org.springframework.boot.autoconfigure.cache.SimpleCacheConfiguration
     *  org.springframework.boot.autoconfigure.cache.NoOpCacheConfiguration
     *  3.哪个配置类默认生效：SimpleCacheConfiguration
     *  4.给容器中注册了一个CacheManager：ConcurrentMapCacheManager
     *  5.可以获取和创建ConcurrentMapCache类型的缓存组件：他的作用是将数据保存到ConcurrentMap中
     *  6.运行的流程:
     *      @Cacheable:
     *      1.方法运行之前，先去查询Cache(缓存组件)，按照cacheName的指定的名字获取；
     *      （CacheManager先获取相应的缓存）第一次获取缓存如果没有Cache组件会自动创建出来
     *      2.去Cache中查找缓存的内容，使用一个key，默认就是方法的参数
     *      key是按照某种策略生成的；默认是使用keyGenerator生成的，默认使用SimpleKeyGenerator生成key
     *      SimpleKeyGenerator生成key的默认策略：
     *          如果没有参数：key=newSimpleKey();
     *          如果有一个参数：key=参数的值;
     *          如果有多个参数：key=new SimpleKey(params);
     *      3.没有查到缓存就调用目标方法；
     *      4.将目标方法返回的结果，放进缓存中
     *      总结：@Cacheable标注的方法执行之前先来检查缓存中有没有这个数据，默认按照参数的值作为key去查询缓存，
     *      如果没有就运行方法并将结果放入缓存，以后再来调用就可以直接使用缓存中的数据
     *      核心：
     *          1.使用CacheManager（ConcurrentMapCacheManager）按照名字得到Cache（ConcurrentMapCach）组件，如果没有配置CacheManager则默认使用
     *          2.key使用keyGenerator生成的，默认使用SimpleKeyGenerator生成key
     *
     * @param id
     * @return
     */
    @Cacheable(/*value = "emp"*//*,keyGenerator = "myKeyGenerator",condition = "#id>1",unless = "#id==2"*/)
    public Employee getEmp(Integer id){
        Employee employee = employeeMapper.getEmployee(id);
        return employee;
    }

    /**
     * @CachePut:即调用方法，又更新缓存
     * 修改了数据库的某个方法，同时更新缓存
     * 1.运行流程：
     *      1.先调用目标方法
     *      2.将目标方法的结果缓存起来
     * 2.测试步骤
     *      1.查询1号员工：第一次查询将结果放在缓存中，第二次直接冲缓存中获取
     *          key:1,value:Employee [id=1, lastName=张三, email=123@163.com, gender=1, dId=1]
     *      2.更新1号员工：Employee [id=1, lastName=刘云达, email=null, gender=0, dId=null]
     *          将方法的返回值也放进缓存了
     *          key:employee,value:Employee [id=1, lastName=刘云达, email=null, gender=0, dId=null]
     *      3.再次查询1号员工：未更新前的数据Employee [id=1, lastName=张三, email=123@163.com, gender=1, dId=1]
     *          问题：1号员工没有在缓存中更新
     *          解决：
     *              指定key为员工的id（key = "#employee.id）
     *              指定key为员工的idkey = "#result.id
     *              @Cacheable的key是不能使用#result
     * @param employee
     * @return
     */
    @CachePut(/*value = "emp",*/key = "#employee.id")
    public Employee updateEmp(Employee employee){
        System.out.println("updateEmp:"+employee);
        employeeMapper.updateEmp(employee);
        return employee;
    }

    /**
     * @CacheEvict:缓存清除
     * key:指定要删除的数据
     * allEntries = true：删除emp缓存中所有数据
     * beforeInvocation = false:缓存的清除是否在方法之前执行，默认false代表方法执行之后执行，如果方法出现异常,缓存清除失败，如果设置为true在方法执行之前执行清除缓存，则缓存清除成功
     *
     * @param id
     */
    @CacheEvict(/*value = "emp",key = "#id",*/allEntries = true)
    public void deleteEmp(Integer id){
        System.out.println("deleteEmp:"+id);
        int i = 10/0;
        //employeeMapper.deleteEmp(id);
    }

    /**
     * @Caching注解定义复杂的缓存规则
     *
     * @param lastName
     * @return
     */
    @Caching(
            cacheable = {
                    @Cacheable(/*value = "emp",*/key = "#lastName")
            },
            put = {
                    @CachePut(/*value = "emp",*/key = "#result.id"),
                    @CachePut(/*value = "emp",*/key = "#result.email")
            }
    )
    public Employee getEmpByLastName(String lastName){
        Employee empByLastName = employeeMapper.getEmpByLastName(lastName);
        return empByLastName;
    }
}
