package com.iokays.tools.test.domain;

import java.math.BigDecimal;

public class TestDomain2 {
	
	private int i;
	private String id;
	private BigDecimal yuan = null;
	
	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public void setYuan(BigDecimal yuan) {
		this.yuan = yuan;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigDecimal yuan() {
		return yuan;
	}
	
}
