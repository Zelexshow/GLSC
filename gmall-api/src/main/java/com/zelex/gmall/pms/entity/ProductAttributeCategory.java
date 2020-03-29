package com.zelex.gmall.pms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 产品属性分类表
 * </p>
 *
 * @author zelex
 * @since 2020-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pms_product_attribute_category")
@ApiModel(value="ProductAttributeCategory对象", description="产品属性分类表")
public class ProductAttributeCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @ApiModelProperty(value = "属性数量")//指的是商品的销售属性
    @TableField("attribute_count")
    private Integer attributeCount;

    @ApiModelProperty(value = "参数数量")//指的是商品的基本参数
    @TableField("param_count")
    private Integer paramCount;


}
