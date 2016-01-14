/* 
 * Copyright (C), 2014-2014, 时代宇通(深圳)软件公司
 * File Name: @(#)BaseSearchPojo.java
 * Encoding UTF-8
 * Author: chengke
 * Version: 1.0
 * Date: 2014-4-24
 */
package com.chinatime.common.elasticsearch.pojo;

import java.io.Serializable;

/** 
 * 功能描述
 * 
 * <p>
 * <a href="BaseSearchPojo.java"><i>View Source</i></a>
 * </p>
 * @author chengke
 * @version 1.0
 * @since 1.0 
 */
public abstract class BaseSearchPojo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 5930026186573499422L;
    /*
     * 搜索服务器ES中存储的pojo对象中不再包含route和id值，这样有利于减少ES服务器所需的存储空间。
     * 同时这两个值在pojo对象中没有作用，可以由pojo中的其他对象生成
     */
//    protected String route;
    
    public static class OperateType{
        public static final String ADD = "ADD";
        public static final String DELETE = "DELETE";
        public static final String UPDATE = "UPDATE";
    }

    /**
     * 对于全局联系人Id值为contactId，个人联系人Id值为contactId+accountId
     * 对于全局时代群Id值为groupId
     * 对于全局职位Id值为jobId
     * 对于全局机构Id值为orgId
     */
    protected String id;
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    
    public void setId(String id){
        this.id=id;
    }
    
    /**
     * @return the route
     *//*
    public String getRoute() {
        return route;
    }

    *//**
     * @param route the route to set
     *//*
    public void setRoute(String route) {
        this.route = route;
    }*/
}
