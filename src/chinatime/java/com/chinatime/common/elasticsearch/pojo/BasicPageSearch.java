package com.chinatime.common.elasticsearch.pojo;

import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 主页基本公用属性
 * 
 * <p>
 * <a href="BasicPageSearch.java"><i>View Source</i></a>
 * </p>
 * 
 * @author pengyuanbing
 * @version 1.0
 * @since 1.0
 */
public abstract class BasicPageSearch extends BaseSearchPojo {

    private static final long serialVersionUID = 1L;

    private String pageName; // 主页名称
    private String pageNamePinyin; // 拼音

    private String tradeId; // 行业

    private String pageSign; // 企业签名

    private Long followNum;// 关注数

    private String sqLogopicId;// 正方形log图标

    private String cityId;// 网页所属城市Id
    private String areaId;

    private Long createdTime;// 创建日期
    private Long modifiedTime;// 修改日期[对ES的修改时间]
    
    private Integer pageType; // 页面类型 0=机构部门 1=产品服务 2=招聘主页 3=其他主页
    
    private List<String> homeTags;

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPageNamePinyin() {
        return pageNamePinyin;
    }

    public void setPageNamePinyin(String pageNamePinyin) {
        this.pageNamePinyin = pageNamePinyin;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getPageSign() {
        return pageSign;
    }

    public void setPageSign(String pageSign) {
        this.pageSign = pageSign;
    }

    public Long getFollowNum() {
        return followNum;
    }

    public void setFollowNum(Long followNum) {
        this.followNum = followNum;
    }

    public String getSqLogopicId() {
        return sqLogopicId;
    }

    public void setSqLogopicId(String sqLogopicId) {
        this.sqLogopicId = sqLogopicId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
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

    public List<String> getHomeTags() {
        return homeTags;
    }

    public void setHomeTags(List<String> homeTags) {
        this.homeTags = homeTags;
    }

    public Integer getPageType() {
		return pageType;
	}

	public void setPageType(Integer pageType) {
		this.pageType = pageType;
	}

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
