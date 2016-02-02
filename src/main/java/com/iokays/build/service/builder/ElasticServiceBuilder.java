package com.iokays.build.service.builder;

import java.io.IOException;

import javax.lang.model.element.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iokays.build.domain.builder.ClazzFieldsBuilder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public class ElasticServiceBuilder {
	private final Class<?> clazz;

	private final ClassName className;
	private final ClassName classImplName;
	
	private final ClassName paramClazz;

	public ElasticServiceBuilder(Class<?> clazz) {
		this.clazz = clazz;
		final int lastIndex = clazz.getPackage().getName().lastIndexOf(".") + 1;
		className = ClassName.get(clazz.getName().substring(0, lastIndex) + "service",
				this.clazz.getSimpleName() + "Service");
		classImplName = ClassName.get(className.packageName() + ".impl", className.simpleName() + "Impl");
		
		paramClazz = ClassName.get(this.clazz.getPackage().getName(), clazz.getSimpleName() + "Param");
	}

	private MethodSpec save() {
		final MethodSpec method = MethodSpec.methodBuilder("save").addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(paramClazz, "param", Modifier.FINAL)
				.returns(String.class).build();
		return method;
	}

	
	private MethodSpec edit() {
		final MethodSpec method = MethodSpec.methodBuilder("edit").addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(paramClazz, "param", Modifier.FINAL)
				.returns(String.class).build();
		return method;
	}


	private MethodSpec delete() {
		final MethodSpec method = MethodSpec.methodBuilder("delete").addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(String.class, "id", Modifier.FINAL).addParameter(String.class, "routing", Modifier.FINAL)
				.build();
		return method;
	}


	public JavaFile build() {
		final Builder builder = TypeSpec.interfaceBuilder(className.simpleName()).addModifiers(Modifier.PUBLIC);
		builder.addMethod(save());
		builder.addMethod(delete());
		return JavaFile.builder(className.packageName(), builder.build()).build();
	}

	public JavaFile buildImpl() {
		final Builder builder = TypeSpec.classBuilder(classImplName.simpleName()).addSuperinterface(className)
				.addModifiers(Modifier.PUBLIC);

		builder.addField(FieldSpec.builder(Logger.class, "logger", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
				.initializer("$T.getLogger($T.class)", LoggerFactory.class, classImplName).build());
		builder.addField(FieldSpec.builder(String.class, "index", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
				.initializer("$T._index", new ClazzFieldsBuilder(clazz).className()).build());
		builder.addField(FieldSpec.builder(String.class, "type", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
				.initializer("$T._type", new ClazzFieldsBuilder(clazz).className()).build());
		builder.addField(FieldSpec.builder(ParameterizedTypeName.get(Class.class, clazz), "clazz", Modifier.PRIVATE,
				Modifier.STATIC, Modifier.FINAL).initializer("$T.class", clazz).build());
		builder.addField(FieldSpec
				.builder(ClassName.get("com.chinatime.common.elasticsearch.utils", "DefaultResultMapper"),
						"resultsMapper", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
				.initializer("new DefaultResultMapper()").build());
		/*builder.addField(FieldSpec.builder(Client.class, "client", Modifier.PRIVATE, Modifier.FINAL).build());

		builder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
				.addParameter(Client.class, "client").addStatement("this.client = client").build());*/

		return JavaFile.builder(classImplName.packageName(), builder.build()).build();
	}

	
}
