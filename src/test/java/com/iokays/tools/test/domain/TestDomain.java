package com.iokays.tools.test.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class TestDomain implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer i;
	private String id;
	private BigDecimal yuan = null;

	public Integer getI() {
		return i;
	}

	public void setI(Integer i) {
		this.i = i;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigDecimal getYuan() {
		return yuan;
	}

	public void setYuan(BigDecimal yuan) {
		this.yuan = yuan;
	}

}
