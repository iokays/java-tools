package com.iokays.build.domain.builder;

import java.io.Serializable;
import java.util.List;

public class StructParam implements Serializable {
	private String name;
	private List<ObjectParam> params;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ObjectParam> getParams() {
		return params;
	}

	public void setParams(List<ObjectParam> params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "StructParam [name=" + name + ", params=" + params + "]";
	}

}
