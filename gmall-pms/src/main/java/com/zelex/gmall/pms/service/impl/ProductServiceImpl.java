package com.zelex.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zelex.gmall.constant.EsConstant;
import com.zelex.gmall.pms.entity.*;
import com.zelex.gmall.pms.mapper.*;
import com.zelex.gmall.pms.service.ProductService;
import com.zelex.gmall.to.es.EsProduct;
import com.zelex.gmall.to.es.EsProductAttributeValue;
import com.zelex.gmall.to.es.EsSkuProductInfo;
import com.zelex.gmall.vo.PageInfoVo;
import com.zelex.gmall.vo.product.PmsProductParam;
import com.zelex.gmall.vo.product.PmsProductQueryParam;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author zelex
 * @since 2020-01-07
 *
 * 查询要多试验几次，增删改要快速失败
 */
@Slf4j
@Service
@Component
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    ProductAttributeValueMapper productAttributeValueMapper;
    @Autowired
    ProductFullReductionMapper productFullReductionMapper;
    @Autowired
    ProductLadderMapper productLadderMapper;
    @Autowired
    SkuStockMapper skuStockMapper;
    @Autowired
    JestClient jestClient;
    //当前线程共享同样的数据
    //ThreadLocal的原理就是使用了一个Map,key是当前线程的名字，value是值
   ThreadLocal<Long> threadLocal =new ThreadLocal<>();


    @Override
    public Product productInfo(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public PageInfoVo productPageInfo(PmsProductQueryParam productQueryParam) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        if(productQueryParam.getBrandId()!=null){
            //前端传了品牌id
            queryWrapper.eq("brand_id",productQueryParam.getBrandId());
        }
        if(!StringUtils.isEmpty(productQueryParam.getKeyword())){
            //前端传了关键字，使用模糊查询
            queryWrapper.like("name",productQueryParam.getKeyword());
        }
        if(productQueryParam.getProductCategoryId()!=null){
            //前端传了商品分类id
            queryWrapper.eq("product_category_id",productQueryParam.getProductCategoryId());
        }
        if(!StringUtils.isEmpty(productQueryParam.getProductSn())){
            //前端传了货号
            queryWrapper.like("product_sn",productQueryParam.getProductSn());
        }
        if(productQueryParam.getVerifyStatus()!=null){
            //前端传了审核状态
            queryWrapper.eq("verify_status",productQueryParam.getVerifyStatus());
        }
        if(productQueryParam.getPublishStatus()!=null){
            queryWrapper.eq("publish_status",productQueryParam.getPublishStatus());
        }
        IPage<Product> page = productMapper.selectPage(new Page<Product>(productQueryParam.getPageNum(), productQueryParam.getPageSize()), queryWrapper);
        PageInfoVo pageInfoVo=new PageInfoVo(page.getTotal(),page.getPages(),
                productQueryParam.getPageSize(),page.getRecords(),page.getCurrent());
        return pageInfoVo;
    }

    /**
     * 超级大保存
     * @param productParam
     * 考虑事务
     * 1）哪些东西是一定哟回滚的、哪些即时出错了也不必要回滚的
     *  商品的核心信息（基本数据、sku）保存的时候，不要受到别的无关信息的影响
     *  无关信息的出问题，核心信息也不用回滚
     * 2）、事务的传播行为;propagation:当前方法的事务[是否要和别人公用一个事务]如何传播下去（里面的方法如果用事务，是否和他公用一个事务）
     *
     *
     *      Propagation propagation() default Propagation.REQUIRED;
     *
     *
     *
     *      REQUIRED:(必须):
     *          Support a current transaction, create a new one if none exists.
     *          如果以前有事务，就和之前的事务公用一个事务，没有就创建一个事务；
     *
     *      REQUIRES_NEW（总是用新的事务）:
     *           Create a new transaction, and suspend the current transaction if one exists.
     *          创建一个新的事务，如果以前有事务，暂停前面的事务。
     *
     *      SUPPORTS（支持）:
     *          Support a current transaction, execute non-transactionally if none exists.
     *          之前有事务，就以事务的方式运行，没有事务也可以；
     *
     *      MANDATORY（强制）:没事务就报错
     *          Support a current transaction, throw an exception if none exists
     *          一定要有事务，如果没事务就报错
     *
     *
     *      NOT_SUPPORTED（不支持）:
     *          Execute non-transactionally, suspend the current transaction if one exists
     *          不支持在事务内运行，如果已经有事务了，就挂起当前存在的事务
     *
     *      NEVER（从不使用）:
     *           Execute non-transactionally, throw an exception if a transaction exists.
     *           不支持在事务内运行，如果已经有事务了，抛异常
     *
     *
     *      NESTED:
     *          Execute within a nested transaction if a current transaction exists,
     *          开启一个子事务（MySQL不支持），需要支持还原点功能的数据库才行；
     *
     *
     * 一家人带着老王去旅游；
     *      一家人：开自己的车还是坐老王的车
     *
     *      Required：坐老王车
     *      Requires_new：一定得开车，开新的
     *
     *      SUPPORTS：用车，有车就用，没车走路；
     *      MANDATORY：用车，没车就骂街。。。
     *
     *      NOT_SUPPORTED：不支持用车。有车放那不用
     *      NEVER：从不用车，有车抛异常
     *
     *
     *
     *
     * 外事务{
     *
     *     A();//事务.Required：跟着回滚
     *
     *     b();//事务.Requires_new：不回滚
     *
     *     //自己给数据库插入数据
     *
     *     int i = 10/0;
     *
     * }
     *
     * Required_new
     * 外事务{
     *
     *     A（）；Required; A
     *     B（）;Requires_new B
     *     try{
     *         C();Required; C
     *     }catch(Exception e){
     *         //c出异常？
     *     }
     *
     *     D();Requires_new; D
     *
     *     //给数据库存 --外
     *
     *    // int i = 10/0;
     *
     * }
     *
     * 场景1：
     *      A方法出现了异常；由于异常机制导致代码停止，下面无法执行，数据库什么都没有
     * 场景2：
     *     C方法出现异常；A回滚，B成功，C回滚，D无法执行，外无法执行
     * 场景3：
     *      外成了后，int i = 10/0; B,D成功。A,C,外都执行了但是必须回滚
     * 场景4：
     *     D炸；抛异常。外事务感知到异常。A,C回滚，外执行不到，D自己回滚，B成功
     * 场景5：
     *     C用try-catch执行；C出了异常回滚，由于异常被捕获，外事务没有感知异常。A,B,D都成，C自己回滚
     *
     * 总结：
     *      传播行为过程中，只要Requires_new被执行过就一定成功，不管后面出不出问题。异常机制还是一样的，出现异常代码以后不执行。
     * Required只要感觉到异常就一定回滚。和外事务是什么传播行为无关。
     *
     * 传播行为总是来定义，当一个事务存在的时候，他内部的事务该怎么执行。
     *
     * 事务中Spring中是怎么做的
     * TransactionManager;
     * AOP做，动态代理
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveProduct(PmsProductParam productParam) {
        /**
         * 必须要拿对象调方法才能有事务功能
         */
        ProductServiceImpl proxy = (ProductServiceImpl)AopContext.currentProxy();
        //1)、保存基本信息
        proxy.saveBaseInfo(productParam);

        //5）、pms_sku_stock:sku_库存表
        proxy.saveSkuStock(productParam);
        //2）、pms_product_attribute_value:保存这个商品对应的所有属性的值
        proxy.saveProductAttributeValue(productParam);
        //3）、pms_product_full_reduction:保存商品的满减信息
        proxy.saveFullReduction(productParam);
        //4）、pms_product_ladder:满减表
        proxy.saveProductLadder(productParam);
    }

    /**
     * 1）、dubbo的默认集群容错有哪几种？怎么做？
     * failover/failfast/failsafe/failback/forking
     *     @Service注解上就行
     * @param ids
     * @param publishStatus
     */
    @Override
    public void updatePublishStatus(List<Long> ids, Integer publishStatus) {
        if(publishStatus == 0){
            ids.forEach((id)->{
                //下架
                //改数据库状态
                setProductPublishStatus(publishStatus, id);
                //删es
                deleteProductFromEs(id);
            });

        }else {
            //上架
            ids.forEach((id)->{
                //该数据状态
                setProductPublishStatus(publishStatus, id);
                //加es
                saveProductToEs(id);
            });
        }
    }

    @Override
    public EsProduct productAllInfo(Long id){
        EsProduct esProduct=null;
        //按照id查询商品
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.termQuery("id",id));
        Search build = new Search.Builder(builder.toString())
                .addIndex(EsConstant.PRODUCT_ES_INDEX)
                .addType(EsConstant.PRODUCT_INFO_ES_TYPE)
                .build();
        try {
            SearchResult execute = jestClient.execute(build);
            List<SearchResult.Hit<EsProduct, Void>> hits = execute.getHits(EsProduct.class);
            esProduct=hits.get(0).source;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return esProduct;
    }

    @Override
    public EsProduct productSkuInfo(Long id) {
        EsProduct esProduct=null;
        //按照id查询商品
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.nestedQuery("skuProductInfos",QueryBuilders.termQuery("skuProductInfos.id",id), ScoreMode.None));
        Search build = new Search.Builder(builder.toString())
                .addIndex(EsConstant.PRODUCT_ES_INDEX)
                .addType(EsConstant.PRODUCT_INFO_ES_TYPE)
                .build();
        try {
            SearchResult execute = jestClient.execute(build);
            List<SearchResult.Hit<EsProduct, Void>> hits = execute.getHits(EsProduct.class);
            esProduct=hits.get(0).source;
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return esProduct;
    }

    @Override
    public SkuStock skuInfoById(Long skuId) {
        return skuStockMapper.selectById(skuId);

    }

    @Override
    public EsProduct produSkuInfo(Long skuId) {
        EsProduct esProduct = null;
        //按照id查出商品
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.nestedQuery("skuProductInfos",QueryBuilders.termQuery("skuProductInfos.id",skuId), ScoreMode.None));


        Search build = new Search.Builder(builder.toString())
                .addIndex(EsConstant.PRODUCT_ES_INDEX)
                .addType(EsConstant.PRODUCT_INFO_ES_TYPE)
                .build();
        try {
            SearchResult execute = jestClient.execute(build);

            List<SearchResult.Hit<EsProduct, Void>> hits = execute.getHits(EsProduct.class);
            esProduct = hits.get(0).source;
        } catch (IOException e) {

        }
        return esProduct;
    }

    private void deleteProductFromEs(Long id) {
        Delete delete = new Delete.Builder(id.toString()).index(EsConstant.PRODUCT_ES_INDEX).type(EsConstant.PRODUCT_INFO_ES_TYPE).build();
        try {
            DocumentResult execute = jestClient.execute(delete);
            boolean succeeded = execute.isSucceeded();
            if (succeeded){
                log.info("商品：{} ==>>ES下架成功",id);
            }else {
//                deleteProductFromEs(id);
                log.error("商品：{} ==>>ES下架失败",id);
            }
        } catch (Exception e) {
//            deleteProductFromEs(id);
            log.error("商品：{} ==>>ES下架失败",id);
        }
    }

    private void saveProductToEs(Long id) {

        //1、查出商品的基本信息
        Product productInfo = productInfo(id);
        EsProduct esProduct=new EsProduct();
        //1、复制基本信息
        BeanUtils.copyProperties(productInfo,esProduct);

        //2、复制sku信息，对于es要保存商品的信息，还要查出这个商品的sku,给es保存
        List<SkuStock> stocks = skuStockMapper.selectList(new QueryWrapper<SkuStock>().eq("product_id",id));
        List<EsSkuProductInfo> esSkuProductInfos=new ArrayList<>(stocks.size());

        //查出当前商品sku属性
        List<ProductAttribute> skuAttributeNames=productAttributeValueMapper.selectProductSaleAttrName(id);
        stocks.forEach((skuStock)->{
            EsSkuProductInfo info=new EsSkuProductInfo();
            BeanUtils.copyProperties(skuStock,info);

            String subTitle=esProduct.getName();
            if(!StringUtils.isEmpty(skuStock.getSp1())){
                subTitle+=skuStock.getSp1();
            }
            if(!StringUtils.isEmpty(skuStock.getSp2())){
                subTitle+=skuStock.getSp2();
            }
            if(!StringUtils.isEmpty(skuStock.getSp3())){
                subTitle+=skuStock.getSp3();
            }
            //sku的特色标题
            info.setSkuTitle(subTitle);

            List<EsProductAttributeValue> skuAttributeValues=new ArrayList<>();

            for(int i=0;i<skuAttributeNames.size();i++){
                //skuAttr 颜色/尺码
                EsProductAttributeValue value=new EsProductAttributeValue();
                value.setName(skuAttributeNames.get(i).getName());
                value.setProductAttributeId(skuAttributeNames.get(i).getId());
                value.setType(skuAttributeNames.get(i).getType());
                //颜色 尺码；让es去统计，改掉查询商品的属性分类中里面所有属性的时候，按照sort字段排好
                if (i==0){
                    value.setValue(skuStock.getSp1());
                }
                if (i==1){
                    value.setValue(skuStock.getSp2());
                }
                if (i==2){
                    value.setValue(skuStock.getSp3());
                }
                skuAttributeValues.add(value);
            }


            info.setAttributeValues(skuAttributeValues);
            //sku有多个销售属性：颜色，尺码
            esSkuProductInfos.add(info);
            //查出销售属性的名
        });
        //查出这个sku所有销售属性对应的值.要统计数据库中这个sku有多少个值
        esProduct.setSkuProductInfos(esSkuProductInfos);

        List<EsProductAttributeValue> attributeValues=productAttributeValueMapper.selectProductBaseAttributeAndValue(id);

        //3、复制公共属性信息，查出这个商品的公共属性
        esProduct.setAttrValueList(attributeValues);

        try {
            //把商品保存到es中
            Index build = new Index.Builder(esProduct)
                    .index(EsConstant.PRODUCT_ES_INDEX)
                    .type(EsConstant.PRODUCT_INFO_ES_TYPE)
                    .id(id.toString())
                    .build();
            DocumentResult execute = jestClient.execute(build);
            boolean succeed=execute.isSucceeded();
            if (succeed){
                log.info("ES中；id为{}商品上架完成",id);
            }else {
                log.error("ES中,id为{}商品未保存成功，开始重试",id);
//                saveProductToEs(id);
            }
        } catch (Exception e) {
            log.error("ES中；id为{}商品数据保存异常：{}", id, e.getMessage());
//            saveProductToEs(id);
        }
    }

    public void setProductPublishStatus(Integer publishStatus, Long id) {
        //javaBean应该都去用包装类型
        Product product = new Product();
        //默认所有属性为null
        product.setId(id);
        product.setPublishStatus(publishStatus);
        //mybatis-plus自带的更新方法是哪个字段有值就更哪个字段
        productMapper.updateById(product);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = {Exception.class})
    public void saveSkuStock(PmsProductParam productParam) {
        List<SkuStock> skuStockList = productParam.getSkuStockList();
        for(int i=1;i<=skuStockList.size();i++){
            SkuStock skuStock=skuStockList.get(i-1);
            if(StringUtils.isEmpty(skuStock.getSkuCode())){
                //生成规则 商品id_sku自增id
                skuStock.setSkuCode(threadLocal.get()+"_"+i);
            }
            skuStock.setProductId(threadLocal.get());
            skuStockMapper.insert(skuStock);
        }
//        int i=2/0;
        log.debug("当前线程....{}-->{}",Thread.currentThread().getId(),Thread.currentThread().getName());
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductLadder(PmsProductParam productParam) {
        List<ProductLadder> productLadderList = productParam.getProductLadderList();
        productLadderList.forEach(productLadder -> {
            productLadder.setProductId(threadLocal.get());
            productLadderMapper.insert(productLadder);
        });
        log.debug("当前线程....{}-->{}",Thread.currentThread().getId(),Thread.currentThread().getName());
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFullReduction(PmsProductParam productParam) {
        //3）、pms_product_full_reduction:保存商品的满减信息
        List<ProductFullReduction> fullReductionList = productParam.getProductFullReductionList();
        fullReductionList.forEach((reduction)->{
            reduction.setProductId(threadLocal.get());
            productFullReductionMapper.insert(reduction);
        });
        log.debug("当前线程....{}-->{}",Thread.currentThread().getId(),Thread.currentThread().getName());
    }

    /**
     * 保存基本信息
     * @param productParam
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBaseInfo(PmsProductParam productParam){
        //1）、pms_product:保存商品的基本信息
        Product product=new Product();
        BeanUtils.copyProperties(productParam,product);
        productMapper.insert(product);
        //mybatis-plus能自动获取到刚才这个数据的id
        log.debug("刚才的商品id: {}",product.getId());
        threadLocal.set(product.getId());
        log.debug("当前线程....{}-->{}",Thread.currentThread().getId(),Thread.currentThread().getName());
    }

    /**
     *  保存这个商品对应的所有属性的值
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductAttributeValue(PmsProductParam productParam){
        //2）、pms_product_attribute_value:保存这个商品对应的所有属性的值
        List<ProductAttributeValue> valueList = productParam.getProductAttributeValueList();
        valueList.forEach((item)->{
            item.setProductId(threadLocal.get());
            productAttributeValueMapper.insert(item);
        });
        log.debug("当前线程....{}-->{}",Thread.currentThread().getId(),Thread.currentThread().getName());
    }

}
