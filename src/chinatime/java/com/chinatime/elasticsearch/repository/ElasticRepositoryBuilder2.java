package com.chinatime.elasticsearch.repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.iokays.tools.domain.builder.ClazzFieldsBuilder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public class ElasticRepositoryBuilder2 {
	
	public static final ClassName INDEX_REQUEST = ClassName.get("org.elasticsearch.action.index", "IndexRequest");
	public static final ClassName INDEX_RESPONSE = ClassName.get("org.elasticsearch.action.index", "IndexResponse");
	public static final ClassName UPDATE_REQUEST_BUILDER = ClassName.get("org.elasticsearch.action.update", "UpdateRequestBuilder");
	public static final ClassName UPDATE_REQUEST = ClassName.get("org.elasticsearch.action.update", "UpdateRequest");
	public static final ClassName DELETE_REQUEST = ClassName.get("org.elasticsearch.action.delete", "DeleteRequest");
	public static final ClassName QUERY_BUILDER = ClassName.get("org.elasticsearch.index.query", "QueryBuilder");
	public static final ClassName SORT_BUILDER = ClassName.get("org.elasticsearch.search.sort", "SortBuilder");
	public static final ClassName OP_TYPE = ClassName.get("org.elasticsearch.action.index.IndexRequest", "OpType");
	public static final ClassName CLIENT = ClassName.get("org.elasticsearch.client", "Client");
	public static final ClassName ELASTICSEARCH_EXCEPTION = ClassName.get("org.elasticsearch", "ElasticsearchException");
	public static final ClassName SEARCH_ENTITY_PAGE = ClassName.get("com.chinatime.common.elasticsearch.utils", "SearchEntityPage");
	public static final ClassName ABSTRACT_AGGREGATION_BUILDER = ClassName.get("org.elasticsearch.search.aggregations", "AbstractAggregationBuilder");
	public static final ClassName STRINGS = ClassName.get("java.lang", "String...");
	
	private final Class<?> clazz;
	
	private final ClassName className;
	private final ClassName classImplName;
	
	public ElasticRepositoryBuilder2(Class<?> clazz) {
		
		this.clazz = clazz;
		final int lastIndex = clazz.getPackage().getName().lastIndexOf(".") + 1;
		
		className = ClassName.get(clazz.getName().substring(0, lastIndex) +"repository", this.clazz.getSimpleName() + "Repository");
		classImplName = ClassName.get(className.packageName() + ".impl", className.simpleName() + "Impl");
		
	}
	
	private MethodSpec save() {
		final MethodSpec method = MethodSpec.methodBuilder("save")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(clazz, "entity", Modifier.FINAL)
				.addParameter(String.class, "routing", Modifier.FINAL)
				.returns(String.class).build();
		return method;
	}
	private MethodSpec saveImpl() {
		final MethodSpec method = MethodSpec.methodBuilder("save")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(clazz, "entity", Modifier.FINAL)
				.addParameter(String.class, "routing", Modifier.FINAL)
				.beginControlFlow("try")
				.addStatement("final $T request = indexRequest(entity, routing)", INDEX_REQUEST)
				.addStatement("$T response = client.index(request).actionGet()", INDEX_RESPONSE)
				.addStatement("return response.getId()")
				.endControlFlow()
				.beginControlFlow("catch ($T e)", IOException.class)
				.addStatement("logger.error(\"failed to index the document [id: \" + entity.getId() + \"]\", e)")
				.addStatement("throw new $T(\"failed to index the document [id: \" + entity.getId() + \"]\", e)", ELASTICSEARCH_EXCEPTION)
				.endControlFlow()
				.returns(String.class).build();
		return method;
	}
	
	private MethodSpec update() {
		final MethodSpec method = MethodSpec.methodBuilder("update")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(String.class, "id", Modifier.FINAL)
				.addParameter(String.class, "routing", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(Map.class, String.class, Object.class), "map", Modifier.FINAL)
				.build();
		return method;
	}
	
	private MethodSpec updateImpl() {
		final MethodSpec method = MethodSpec.methodBuilder("update")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(String.class, "id", Modifier.FINAL)
				.addParameter(String.class, "routing", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(Map.class, String.class, Object.class), "map", Modifier.FINAL)
				.addStatement("$T.checkArgument($T.isNotBlank(id), \"id 不能为为NULL或空字符.\")", Preconditions.class, StringUtils.class)
				.addStatement("$T.checkArgument($T.isNotBlank(routing), \"routing 不能为为NULL或空字符.\")", Preconditions.class, StringUtils.class)
				.addStatement("$T.checkArgument(!map.isEmpty(), \"map 不能为空.\")", Preconditions.class)
				.addStatement("$T updateRequestBuilder = this.client.prepareUpdate(index, type, id).setRouting(routing)", UPDATE_REQUEST_BUILDER)
				.beginControlFlow("try")
				.addStatement("updateRequestBuilder.setDoc(resultsMapper.entityMapper.mapToString(map))")
				.addStatement("updateRequestBuilder.get()")
				.endControlFlow()
				.beginControlFlow("catch (Exception e)")
				.addStatement("throw new $T(\"failed to udpate the document [id: \" + id + \"]\", e)", ELASTICSEARCH_EXCEPTION)
				.endControlFlow()
				.build();
		return method;
	}
	
	private MethodSpec delete() {
		final MethodSpec method = MethodSpec.methodBuilder("delete")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(String.class, "id", Modifier.FINAL)
				.addParameter(String.class, "routing", Modifier.FINAL)
				.build();
		return method;
	}
	
	private MethodSpec deleteImpl() {
		final MethodSpec method = MethodSpec.methodBuilder("delete")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(String.class, "id", Modifier.FINAL)
				.addParameter(String.class, "routing", Modifier.FINAL)
				.addStatement("$T.checkArgument($T.isNotBlank(id), \"id 不能为为NULL或空字符.\")", Preconditions.class, StringUtils.class)
				.addStatement("$T.checkArgument($T.isNotBlank(routing), \"routing 不能为为NULL或空字符.\")", Preconditions.class, StringUtils.class)
				.addStatement("$T request = new DeleteRequest(index, type, id).refresh(true).routing(routing)", DELETE_REQUEST)
				.beginControlFlow("try")
				.addStatement("client.delete(request).get()")
				.endControlFlow()
				.beginControlFlow("catch (Exception e)")
				.addStatement("throw new $T(\"failed to delete the document [id: \" + id + \"]\", e)", ELASTICSEARCH_EXCEPTION)
				.endControlFlow()
				.build();
		return method;
	}
	
	private MethodSpec indexRequest() {
		final MethodSpec method = MethodSpec.methodBuilder("indexRequest").addException(IOException.class)
				.addModifiers(Modifier.PRIVATE)
				.returns(INDEX_REQUEST)
				.addParameter(clazz, "entity", Modifier.FINAL)
				.addParameter(String.class, "routing", Modifier.FINAL)
				.addStatement("final String id = entity.getId()")
				.addStatement("$T.checkArgument($T.isNotBlank(id), \"id 不能为为NULL或空字符.\")", Preconditions.class, StringUtils.class)
				.addStatement("$T.checkArgument($T.isNotBlank(routing), \"routing 不能为为NULL或空字符.\")", Preconditions.class, StringUtils.class)
				.addStatement("final $T request = new IndexRequest(index, type, String.valueOf(id))", INDEX_REQUEST)
				.addStatement("request.routing(routing)")
				.addStatement("request.opType($T.INDEX)", OP_TYPE)
				.addStatement("request.source(resultsMapper.entityMapper.mapToString(entity))")
				.addStatement("return request")
				.build();
		return method;
	}
	
	private MethodSpec search() {
		final MethodSpec method = MethodSpec.methodBuilder("search")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(QUERY_BUILDER, "query", Modifier.FINAL)
				.addParameter(SORT_BUILDER, "sort", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(List.class, String.class), "routing", Modifier.FINAL)
				.addParameter(int.class, "from", Modifier.FINAL)
				.addParameter(int.class, "size", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(ClassName.get("java.util", "List"), ABSTRACT_AGGREGATION_BUILDER), "aggregations", Modifier.FINAL)
				.addParameter(STRINGS, "brightFields", Modifier.FINAL)
				.addStatement("final String id = entity.getId()")
				.build();
		return method;
	}
	
	private MethodSpec searchImpl() {
		final MethodSpec method = MethodSpec.methodBuilder("search")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(QUERY_BUILDER, "query", Modifier.FINAL)
				.addParameter(SORT_BUILDER, "sort", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(List.class, String.class), "routing", Modifier.FINAL)
				.addParameter(int.class, "from", Modifier.FINAL)
				.addParameter(int.class, "size", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(ClassName.get("java.util", "List"), ABSTRACT_AGGREGATION_BUILDER), "aggregations", Modifier.FINAL)
				.addParameter(STRINGS, "brightFields", Modifier.FINAL)
				.build();
		
		return method;
	}
	
	public JavaFile build() {
		final Builder builder = TypeSpec.interfaceBuilder(className.simpleName()).addModifiers(Modifier.PUBLIC);
		builder.addMethod(save());
		builder.addMethod(update());
		builder.addMethod(delete());
		return JavaFile.builder(className.packageName(), builder.build()).build();
	}
	
	public JavaFile buildImpl() {
		final Builder builder = TypeSpec.classBuilder(classImplName.simpleName()).addSuperinterface(className).addModifiers(Modifier.PUBLIC);
		
		builder.addField(FieldSpec.builder(Logger.class, "logger", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("$T.getLogger($T.class)", LoggerFactory.class, classImplName).build());
		builder.addField(FieldSpec.builder(String.class, "index", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer("$T._index", new ClazzFieldsBuilder(clazz).className()).build());
		builder.addField(FieldSpec.builder(String.class, "type", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer("$T._type", new ClazzFieldsBuilder(clazz).className()).build());
		builder.addField(FieldSpec.builder(ParameterizedTypeName.get(Class.class, clazz), "clazz", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("$T.class", clazz).build());
		builder.addField(FieldSpec.builder(ClassName.get("com.chinatime.common.elasticsearch.utils", "DefaultResultMapper"), "resultsMapper", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("new DefaultResultMapper()").build());
		builder.addField(FieldSpec.builder(CLIENT, "client", Modifier.PRIVATE, Modifier.FINAL).build());
		
		builder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).addParameter(CLIENT, "client").addStatement("this.client = client").build());
		
		builder.addMethod(saveImpl());
		builder.addMethod(updateImpl());
		builder.addMethod(deleteImpl());
		builder.addMethod(indexRequest());
		return JavaFile.builder(classImplName.packageName(), builder.build()).build();
	}
}
