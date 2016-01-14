package com.chinatime.datacenter.org.pojo;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.chinatime.common.elasticsearch.pojo.BaseSearchPojo;

public class ProductPageSearch extends BaseSearchPojo {

	private static final long serialVersionUID = 1L;

	private String name; // 产品名称

	private String orgId; // 公司id
	private String parentId; // 服务产品所属网页id
	private String orgPageId; // 产品关联主页id
	private String productId; // 产品线Id
	private String productName;

	private String aboutInfo; // 产品服务简介
	private String picId; // 产品服务图片id,ID为fastDFS中生成的id, 多值，逗号分开
	private String adminId; // 创建该产品的管理员Id
	private String features; // 特点
	private BigDecimal price; // 柜台价
	private BigDecimal disCount;// 优惠价
	private String tradeId; // 产品所属行业类别
	private String customer; // 产品消费群体
	private String weight; // 产品重量
	private String material; // 产品材质
	private String standardpicId; // 多值，逗号分开. 产品规则参数，多个图片之间用,隔开，最多3张图片
	private String publishTime; // 上市时间
	private Long createdTime;
	private Long modifiedTime;

	private Integer shareNum;
	private Integer collectNum;

	private Integer pageType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getOrgPageId() {
		return orgPageId;
	}

	public void setOrgPageId(String orgPageId) {
		this.orgPageId = orgPageId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getAboutInfo() {
		return aboutInfo;
	}

	public void setAboutInfo(String aboutInfo) {
		this.aboutInfo = aboutInfo;
	}

	public String getPicId() {
		return picId;
	}

	public void setPicId(String picId) {
		this.picId = picId;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getDisCount() {
		return disCount;
	}

	public void setDisCount(BigDecimal disCount) {
		this.disCount = disCount;
	}

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getStandardpicId() {
		return standardpicId;
	}

	public void setStandardpicId(String standardpicId) {
		this.standardpicId = standardpicId;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public Long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}

	public Long getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public Integer getShareNum() {
		return shareNum;
	}

	public void setShareNum(Integer shareNum) {
		this.shareNum = shareNum;
	}

	public Integer getCollectNum() {
		return collectNum;
	}

	public void setCollectNum(Integer collectNum) {
		this.collectNum = collectNum;
	}

	public Integer getPageType() {
		return pageType;
	}

	public void setPageType(Integer pageType) {
		this.pageType = pageType;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
