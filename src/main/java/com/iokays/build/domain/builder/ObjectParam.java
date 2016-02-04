package com.iokays.build.domain.builder;

import java.io.Serializable;

public class ObjectParam implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type;
	private String name;
	private String javadoc;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJavadoc() {
		return javadoc;
	}

	public void setJavadoc(String javadoc) {
		this.javadoc = javadoc;
	}

	public String toMarkdown() {
		return "|" + this.getName() + " |是 |" + this.getType() + " |" + this.getJavadoc() + "";
	}

	@Override
	public String toString() {
		return "IceParam [type=" + type + ", name=" + name + ", javadoc=" + javadoc + "]";
	}

}