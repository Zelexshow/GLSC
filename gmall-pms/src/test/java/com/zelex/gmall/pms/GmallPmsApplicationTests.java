package com.zelex.gmall.pms;

import com.zelex.gmall.pms.entity.Brand;
import com.zelex.gmall.pms.entity.Product;
import com.zelex.gmall.pms.service.BrandService;
import com.zelex.gmall.pms.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallPmsApplicationTests {
    @Autowired
    ProductService productService;

    @Autowired
    BrandService brandService;
    @Test
    void contextLoads() {
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

}
