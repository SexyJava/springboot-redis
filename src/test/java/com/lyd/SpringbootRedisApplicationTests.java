package com.lyd;

import com.lyd.bean.Employee;
import com.lyd.mapper.EmployeeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@SpringBootTest
class SpringbootRedisApplicationTests {
    @Autowired
    private EmployeeMapper employeeMapper;

    //操作字符串的
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    //操作对象的k-v
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedisTemplate<Object, Employee> myRedisTemplate;

    @Test
    void contextLoads() {
        Employee employee = employeeMapper.getEmployee(1);
        System.out.println(employee);
    }

    /**
     * String(字符串),List(列表),Set(集合),Hash(散列),ZSet(有序集合)
     * stringRedisTemplate.opsForValue(){String}
     * stringRedisTemplate.opsForHash(){Hash}
     * stringRedisTemplate.opsForList(){List}
     * stringRedisTemplate.opsForSet(){Set}
     * stringRedisTemplate.opsForZSet(){ZSet}
     */
    @Test
    void test(){
        //给redis中保存了一个数据
        //stringRedisTemplate.opsForValue().append("msg","hello world!");
        //String msg = stringRedisTemplate.opsForValue().get("msg");
        //System.out.println("msg = " + msg);
        //stringRedisTemplate.opsForList().leftPush("myList","1");
        //stringRedisTemplate.opsForList().leftPush("myList","2");
        //List<String> myList = stringRedisTemplate.opsForList().range("myList", 0, -1);
        //for (String s : myList) {
        //    System.out.println("s = " + s);
        //}
    }

    /**
     * 测试保存对象
     */
    @Test
    void test2(){
        Employee employee = employeeMapper.getEmployee(1);
        //默认如果保存对象，使用jdk序列化机制，序列化后的数据保存到redis中
        //redisTemplate.opsForValue().set("emp01",employee);
        //1.将数据以json的方式保存
        //自己将对象转为JSON
        //redisTemplate有默认的序列化规则:改变默认的序列化规则
        myRedisTemplate.opsForValue().set("emp01",employee);
    }

}
