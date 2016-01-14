package com.iokays.build.repository.builder;

import java.io.IOException;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultElement;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.iokays.build.domain.builder.ClazzAttributeBuilder;
import com.iokays.tools.test.domain.TestDomain;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public class MybatisRepositoryBuilder {

	public ClassName PARAM = ClassName.get("org.apache.ibatis.annotations", "Param");
	
	private final Class<?> clazz;
	
	private final ClassName className;
	private final String mappingName;
	private final String ENTITY;
	private final String tableName = "t_table_name";
	private final List<String> fieldNames;
	private final List<String> uniqueIndex = Lists.newArrayList("id");
	
	public MybatisRepositoryBuilder(Class<?> clazz, String... indexs) {
		
		this.clazz = clazz;
		final int lastIndex = clazz.getPackage().getName().lastIndexOf(".") + 1;
		className = ClassName.get(clazz.getName().substring(0, lastIndex) +"dao", this.clazz.getSimpleName() + "Dao");
		mappingName = this.clazz.getSimpleName() + "mapping.xml";
		
		if (indexs.length > 0) {
			for (String index : indexs) {
				if (!"id".equals(index)) {
					uniqueIndex.add(index);
				}
			}
		}
		this.ENTITY = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, clazz.getSimpleName());
		fieldNames = ClazzAttributeBuilder.prFields(clazz);
	}
	
	private MethodSpec findOne() {
		final MethodSpec.Builder builder = MethodSpec.methodBuilder("findOne")
				.returns(clazz)
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
		if (CollectionUtils.isNotEmpty(uniqueIndex)) {
			for (String index : uniqueIndex) {
				builder.addParameter(TypeName.get(String.class).annotated(AnnotationSpec.builder(PARAM).addMember("value", "$S", index).build()), index, Modifier.FINAL);
			}
		}
		return builder.build();
	}
	
	private Element findOneXML() {
		Element element = new DefaultElement("select");
		element.add(new DefaultAttribute("id",  "findOne"));
		element.add(new DefaultAttribute("parameterType",  clazz.getName()));
		element.addText("\n");
		element.addText("SELECT ");
		element.add(new DefaultElement("include").addAttribute("refid", ENTITY));
		element.addText("\n");
		element.addText("FROM " + tableName);
		element.addText("\n");
		element.addText(" WHERE ");
		element.addText("\n");
		final List<String> indexParams = Lists.newArrayList();
		for (String index : uniqueIndex) {
			indexParams.add(index + " = #{" + index +"}");
		}
		element.addText(Joiner.on(" and ").join(indexParams));
		return element;
	}
	
	private MethodSpec findAll() {
		final MethodSpec method = MethodSpec.methodBuilder("findOne")
				.returns(ParameterizedTypeName.get(List.class, clazz))
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.build();
		return method;
	}
	
	private Element findAllXML() {
		Element element = new DefaultElement("select");
		element.add(new DefaultAttribute("id",  "findAll"));
		element.add(new DefaultAttribute("parameterType",  clazz.getName()));
		element.addText("\n");
		element.addText("SELECT ");
		element.add(new DefaultElement("include").addAttribute("refid", ENTITY));
		element.addText("\n");
		element.addText("FROM " + tableName);
		return element;
	}
	
	
	private MethodSpec add() {
		final MethodSpec.Builder builder = MethodSpec.methodBuilder("add").addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).returns(int.class);
		builder.addParameter(TypeName.get(clazz).annotated(AnnotationSpec.builder(PARAM).addMember("value", "$S", "entity").build()), "entity",  Modifier.FINAL);
		return builder.build();
	}
	
	private Element addXml() {
		Element element = new DefaultElement("insert");
		element.add(new DefaultAttribute("id",  "add"));
		element.addText("\n INSERT INTO " + tableName);
		element.addText("\n (" + Joiner.on(",").join(fieldNames) + ")");
		element.addText("\n VALUES(");
		
		final List<String> fieldParams = Lists.newArrayList();
		for (String fieldName : fieldNames) {
			fieldParams.add("#{entity." + fieldName + "}");
		}
		
		element.addText("\n" + Joiner.on(",").join(fieldParams) + ")\n");
		return element;
	}
	
	private MethodSpec edit() {
		final MethodSpec.Builder builder = MethodSpec.methodBuilder("edit").returns(int.class).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
		if (CollectionUtils.isNotEmpty(uniqueIndex)) {
			for (String index : uniqueIndex) {
				builder.addParameter(TypeName.get(String.class).annotated(AnnotationSpec.builder(PARAM).addMember("value", "$S", index).build()), index, Modifier.FINAL);
			}
		}
		builder.addParameter(TypeName.get(clazz).annotated(AnnotationSpec.builder(PARAM).addMember("value", "$S", "entity").build()), "entity",  Modifier.FINAL);
		return builder.build();
	}
	
	private Element editXml() {
		Element element = new DefaultElement("update");
		element.add(new DefaultAttribute("id",  "edit"));
		element.addText("\n");
		element.addText("UPDATE " + tableName + " SET ");
		element.addText("\n");
		final List<String> updateParams = Lists.newArrayList();
		for (String fieldName : fieldNames) {
			if (!uniqueIndex.contains(fieldName)) {
				updateParams.add(fieldName + " = #{enity." + fieldName + "}");
			}
		}
		element.addText(Joiner.on(", ").join(updateParams));
		element.addText("\n");
		element.addText(" WHERE ");
		element.addText("\n");
		final List<String> indexParams = Lists.newArrayList();
		for (String index : uniqueIndex) {
			indexParams.add(index + " = #{" + index +"}");
		}
		element.addText(Joiner.on(" and ").join(indexParams));
		element.addText("\n");
		return element;
	}
	
	private MethodSpec delete() {
		final MethodSpec.Builder builder = MethodSpec.methodBuilder("delete").returns(int.class).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
		if (CollectionUtils.isNotEmpty(uniqueIndex)) {
			for (String index : uniqueIndex) {
				builder.addParameter(TypeName.get(String.class).annotated(AnnotationSpec.builder(PARAM).addMember("value", "$S", index).build()), index, Modifier.FINAL);
			}
		}
		return builder.build();
	}
	
	private Element deleteXml() {
		Element element = new DefaultElement("delete");
		element.add(new DefaultAttribute("id",  "delete"));
		element.addText("\n");
		element.addText("DELETE FROM " + tableName);
		element.addText(" WHERE ");
		element.addText("\n");
		final List<String> indexParams = Lists.newArrayList();
		for (String index : uniqueIndex) {
			indexParams.add(index + " = #{" + index +"}");
		}
		element.addText(Joiner.on(" AND ").join(indexParams));
		element.addText("\n");
		return element;
	}
	
	public JavaFile build() {
		final Builder builder = TypeSpec.interfaceBuilder(className.simpleName()).addModifiers(Modifier.PUBLIC);
		builder.addMethod(findAll());
		builder.addMethod(findOne());
		builder.addMethod(add());
		builder.addMethod(edit());
		builder.addMethod(delete());
		return JavaFile.builder(className.packageName(), builder.build()).build();
	}
	
	private Element sqlId() {
		Element element = new DefaultElement("sql");
		element.add(new DefaultAttribute("id",  ENTITY));
		
		final List<String> names = ClazzAttributeBuilder.prFields(clazz);
		if (CollectionUtils.isNotEmpty(names)) {
			final List<String> as = Lists.newArrayList();
			for (String name : names) {
				as.add(Joiner.on(" as ").join(name, name));
			}
			element.addText("\n" + Joiner.on(",\n").join(as) + "\n");
		}
		return element;
	}
	
	public void buildXML() throws IOException {
		final Document document = DocumentHelper.createDocument();
		document.addDocType("mapper", "-//mybatis.org//DTD Mapper 3.0//EN",
				"http://mybatis.org/dtd/mybatis-3-mapper.dtd");
		final Element mapper = document.addElement("mapper");
		mapper.add(new DefaultAttribute("namespace", className.toString()));

		mapper.add(sqlId());
		mapper.add(addXml());
		mapper.addText("\n");
		mapper.add(editXml());
		mapper.addText("\n");
		mapper.add(deleteXml());
		mapper.addText("\n");
		mapper.add(findAllXML());
		mapper.addText("\n");
		mapper.add(findOneXML());
		mapper.addText("\n");
		
		System.out.println(document.asXML());
		
//		StringWriter writer = new StringWriter();
//		OutputFormat xmlFormat = new OutputFormat();
//		xmlFormat.setEncoding("utf-8");
//		xmlFormat.setIndent(true);
//		xmlFormat.setIndent("    ");
//		XMLWriter xmlWriter = new XMLWriter(writer, xmlFormat);
//		xmlWriter.write(document);
//		xmlWriter.close();
//		System.out.println(writer.toString());
	}
	
	public static void main(String[] args) throws IOException {
		final Class<?> clazz = TestDomain.class;
		final MybatisRepositoryBuilder clazzBuilder = new MybatisRepositoryBuilder(clazz, "id", "pageId");
		clazzBuilder.build().writeTo(System.out);
		clazzBuilder.buildXML();
	}
}
