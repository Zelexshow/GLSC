package com.zelex.gmall.pms;

import com.zelex.gmall.pms.entity.Brand;
import com.zelex.gmall.pms.service.BrandService;
import com.zelex.gmall.pms.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class GmallPmsApplicationTests {
    @Autowired
    ProductService productService;

    @Autowired
    BrandService brandService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedisTemplate<Object,Object> redisTemplateObj;
    @Test
    public void contextLoads() {
//        Product byId = productService.getById(1);
//        System.out.println(byId.getName());
        //测试增删改在主库，查在从库
//        Brand brand=new Brand();
//        brand.setName("哈哈哈");
//        brandService.save(brand);
//        System.out.println("保存成功...");
        Brand byId=brandService.getById(53);
        System.out.println(byId.getName());
    }
    @Test
    public void redisTemplate(){
//        redisTemplate.opsForValue();//操作redis中string类型
//        redisTemplate.opsForHash();//操作redis中的hash类型
//        redisTemplate.opsForList();//操作redis中的list类型
        redisTemplate.opsForValue().set("hello","world");
        System.out.println("保存了数据");
        String hello = redisTemplate.opsForValue().get("hello");
        System.out.println("保存的值是："+hello);
    }
    @Test
    public void setRedisTemplateObj(){
        //以后要存对象将对象转为json字符串
        //去redis中取出来，逆转为对象
        Brand brand=new Brand();
        brand.setName("啊哈哈哈");
        redisTemplateObj.opsForValue().set("abc",new Brand());
        System.out.println("刚才存了一个对象");
        Brand abc = (Brand)redisTemplateObj.opsForValue().get("abc");
        System.out.println("刚才存的对象是："+abc);
    }

}
