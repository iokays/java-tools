package com.iokays.build.repository.builder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.lang.model.element.Modifier;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.iokays.build.domain.builder.ClazzFieldsBuilder;
import com.iokays.tools.test.domain.TestDomain;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public class ElasticRepositoryBuilder {

	public static final ClassName UNDEFINED = ClassName.get("com.iokays.undefined", "Undefined");
	
	public static final ClassName SEARCH_ENTITY_PAGE = UNDEFINED;
	public static final ClassName ELASTIC_ROUTE_CONSTANT = UNDEFINED;
	
	public static final ClassName ELASTICSEARCH_EXCEPTION = ClassName.get("org.elasticsearch", "ElasticsearchException");
	public static final ClassName DELETE_REQUEST = ClassName.get("org.elasticsearch.action", "DeleteRequest");
	public static final ClassName INDEX_REQUEST = ClassName.get("org.elasticsearch.action.index", "IndexRequest");
	public static final ClassName OP_TYPE = ClassName.get("org.elasticsearch.action.index.IndexRequest", "OpType");
	public static final ClassName INDEX_RESPONSE = ClassName.get("org.elasticsearch.action.index", "IndexResponse");
	public static final ClassName SEARCH_REQUEST_BUILDER = ClassName.get("org.elasticsearch.action.search", "SearchRequestBuilder");
	public static final ClassName SEARCH_RESPONSE = ClassName.get("org.elasticsearch.action.search", "SearchResponse");
	public static final ClassName SEARCH_SCROLL_REQUEST = ClassName.get("org.elasticsearch.action.search", "SearchScrollRequest");
	public static final ClassName SEARCH_TYPE = ClassName.get("org.elasticsearch.action.search", "SearchType");
	public static final ClassName UPDATE_REQUEST_BUILDER = ClassName.get("org.elasticsearch.action.update", "UpdateRequestBuilder");
	public static final ClassName CLIENT = ClassName.get("org.elasticsearch.client", "Client");
	public static final ClassName QUERY_BUILDER = ClassName.get("org.elasticsearch.index.query", "QueryBuilder");
	public static final ClassName SEARCH_HIT = ClassName.get("org.elasticsearch.search", "SearchHit");
	public static final ClassName ABSTRACT_AGGREGATION_BUILDER = ClassName.get("org.elasticsearch.search.aggregations", "AbstractAggregationBuilder");
	public static final ClassName HIGHLIGHT_BUILDER = ClassName.get("org.elasticsearch.search.highlight", "HighlightBuilder");
	public static final ClassName SORT_BUILDER = ClassName.get("org.elasticsearch.search.sort", "SortBuilder");
	
	private final Class<?> clazz;
	
	private final ClassName className;
	private final ClassName classImplName;
	
	public ElasticRepositoryBuilder(Class<?> clazz) {
		
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
				.addAnnotation(Override.class)
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
				.addAnnotation(Override.class)
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
				.addAnnotation(Override.class)
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
	
	private MethodSpec searchFacet() {
		final MethodSpec method = MethodSpec.methodBuilder("search")
				.returns(ParameterizedTypeName.get(SEARCH_ENTITY_PAGE, ClassName.get(clazz)))
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(QUERY_BUILDER, "query", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(List.class, String.class), "routing", Modifier.FINAL)
				.build();
		return method;
	}
	
	private MethodSpec searchFacetImpl() {
		final MethodSpec method = MethodSpec.methodBuilder("search")
				.addAnnotation(Override.class)
				.returns(ParameterizedTypeName.get(SEARCH_ENTITY_PAGE, ClassName.get(clazz)))
				.addModifiers(Modifier.PUBLIC)
				.addParameter(QUERY_BUILDER, "query", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(List.class, String.class), "routing", Modifier.FINAL)
				.addStatement("return search(query, null, null, 0, 0, null)")
				.build();
		return method;
	}
	
	private MethodSpec simpleSearch() {
		final MethodSpec method = MethodSpec.methodBuilder("search")
				.returns(ParameterizedTypeName.get(SEARCH_ENTITY_PAGE, ClassName.get(clazz)))
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(QUERY_BUILDER, "query", Modifier.FINAL)
				.addParameter(SORT_BUILDER, "sort", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(List.class, String.class), "routing", Modifier.FINAL)
				.addParameter(int.class, "from", Modifier.FINAL)
				.addParameter(int.class, "size", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(List.class, String.class), "brightFields", Modifier.FINAL)
				.build();
		return method;
	}
	
	private MethodSpec simpleSearchImpl() {
		final MethodSpec method = MethodSpec.methodBuilder("search")
				.addAnnotation(Override.class)
				.returns(ParameterizedTypeName.get(SEARCH_ENTITY_PAGE, ClassName.get(clazz)))
				.addModifiers(Modifier.PUBLIC)
				.addParameter(QUERY_BUILDER, "query", Modifier.FINAL)
				.addParameter(SORT_BUILDER, "sort", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(List.class, String.class), "routing", Modifier.FINAL)
				.addParameter(int.class, "from", Modifier.FINAL)
				.addParameter(int.class, "size", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(List.class, String.class), "brightFields", Modifier.FINAL)
				.addStatement("return search(query, sort, routing, from, size, null, brightFields)")
				.build();
		return method;
	}
	
	private MethodSpec search() {
		final MethodSpec method = MethodSpec.methodBuilder("search")
				.returns(ParameterizedTypeName.get(SEARCH_ENTITY_PAGE, ClassName.get(clazz)))
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(QUERY_BUILDER, "query", Modifier.FINAL)
				.addParameter(SORT_BUILDER, "sort", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(List.class, String.class), "routing", Modifier.FINAL)
				.addParameter(int.class, "from", Modifier.FINAL)
				.addParameter(int.class, "size", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), ABSTRACT_AGGREGATION_BUILDER), "aggregations", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(List.class, String.class), "brightFields", Modifier.FINAL)
				.build();
		return method;
	}
	
	private MethodSpec searchImpl() {
		final MethodSpec method = MethodSpec.methodBuilder("search")
				.addAnnotation(Override.class)
				.returns(ParameterizedTypeName.get(SEARCH_ENTITY_PAGE, ClassName.get(clazz)))
				.addModifiers(Modifier.PUBLIC)
				.addParameter(QUERY_BUILDER, "query", Modifier.FINAL)
				.addParameter(SORT_BUILDER, "sort", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(List.class, String.class), "routing", Modifier.FINAL)
				.addParameter(int.class, "from", Modifier.FINAL)
				.addParameter(int.class, "size", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), ABSTRACT_AGGREGATION_BUILDER), "aggregations", Modifier.FINAL)
				.addParameter(ParameterizedTypeName.get(List.class, String.class), "brightFields", Modifier.FINAL)
				.addStatement("$T.checkArgument(from >= 0, \"偏移量 from 必须大于等于 0\")", Preconditions.class)
				.addStatement("$T.checkArgument(size >= 0, \"请求长度 size 必须大于等于 0\")", Preconditions.class)
				.addStatement("logger.debug(\"---  search --- query: {}, sort: {}, from: {}, size: {}\", query, sort, from, size)")
				.addStatement("final $T builder = client.prepareSearch(index).setTypes(type)", SEARCH_REQUEST_BUILDER)
				.addStatement("builder.setQuery(query)")
				.beginControlFlow("if ($T.isNotEmpty(routing))", CollectionUtils.class)
				.addStatement("builder.setRouting(routing.toArray(new String[routing.size()]))")
				.endControlFlow()
				.beginControlFlow("if (null != sort)")
				.addStatement("builder.addSort(sort)")
				.endControlFlow()
				.beginControlFlow("if (brightFields != null && brightFields.length > 0")
				.beginControlFlow("for (int i = 0; i < brightFields.length; i++)")
				.addStatement("logger.debug(\"---  search --- fields[{}]: {}\", i, brightFields[i])")
				.addStatement("$T.Field highlightFiled = new HighlightBuilder.Field(brightFields[i])", HIGHLIGHT_BUILDER)
				.addStatement("highlightFiled.preTags($T.sKeywordStart)", ELASTIC_ROUTE_CONSTANT)
				.addStatement("highlightFiled.postTags($T.sKeywordEnd)", ELASTIC_ROUTE_CONSTANT)
				.addStatement("builder.addHighlightedField(highlightFiled)")
				.endControlFlow()
				.endControlFlow()
				.beginControlFlow("if (0 != size)")
				.addStatement("builder.setFrom(from)")
				.addStatement("builder.setSize(size)")
				.endControlFlow()
				.beginControlFlow("else")
				.addStatement("builder.setSearchType($T.COUNT)", SEARCH_TYPE)
				.endControlFlow()
				.beginControlFlow("if ($T.isNotEmpty(aggregations))", CollectionUtils.class)
				.beginControlFlow("for ($T aggregation : aggregations)", ABSTRACT_AGGREGATION_BUILDER)
				.addStatement("builder.addAggregation(aggregation)")
				.endControlFlow()
				.endControlFlow()
				.addStatement("final $T response = builder.get()", SEARCH_RESPONSE)
				.addStatement("$T<$T> page = resultsMapper.mapResults(response, clazz)", SEARCH_ENTITY_PAGE, clazz)
				.addStatement("page.setAggregations(response.getAggregations())")
				.addStatement("return page")
				.build();
		return method;
	}
	
	private MethodSpec scroll() {
		final MethodSpec method = MethodSpec.methodBuilder("scroll")
				.returns(String.class)
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(QUERY_BUILDER, "query", Modifier.FINAL)
				.addParameter(String.class, "scroll", Modifier.FINAL)
				.addParameter(int.class, "size", Modifier.FINAL)
				.addParameter(boolean.class, "noFields", Modifier.FINAL)
				.build();
		return method;
	}
	
	private MethodSpec scrollImpl() {
		final MethodSpec method = MethodSpec.methodBuilder("scroll")
				.addAnnotation(Override.class)
				.returns(String.class)
				.addModifiers(Modifier.PUBLIC)
				.addParameter(QUERY_BUILDER, "query", Modifier.FINAL)
				.addParameter(String.class, "scroll", Modifier.FINAL)
				.addParameter(int.class, "size", Modifier.FINAL)
				.addParameter(boolean.class, "noFields", Modifier.FINAL)
				.addStatement("final $T builder = client.prepareSearch(index).setTypes(type)", SEARCH_REQUEST_BUILDER)
				.addStatement("builder.setSize(size)")
				.addStatement("builder.setSearchType($T.SCAN)", SEARCH_TYPE)
				.addStatement("builder.setScroll(scroll)")
				.addStatement("builder.setQuery(queryBuilder)")
				.beginControlFlow("if (noFields)")
				.addStatement("builder.setNoFields()")
				.endControlFlow()
				.addStatement("final $T response = builder.get()", SEARCH_RESPONSE)
				.addStatement("return response.getScrollId()")
				.build();
		return method;
	}
	
	private MethodSpec scrollIds() {
		final MethodSpec method = MethodSpec.methodBuilder("scroll")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.returns(ParameterizedTypeName.get(List.class, String.class))
				.addParameter(String.class, "scrollId", Modifier.FINAL)
				.addParameter(String.class, "scroll", Modifier.FINAL)
				.build();
		return method;
	}
	
	private MethodSpec scrollIdsImpl() {
		final MethodSpec method = MethodSpec.methodBuilder("scroll")
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC)
				.returns(ParameterizedTypeName.get(List.class, String.class))
				.addParameter(String.class, "scrollId", Modifier.FINAL)
				.addParameter(String.class, "scroll", Modifier.FINAL)
				.addStatement("$T searchScrollRequest = new SearchScrollRequest(scrollId).scroll(\"1m\")", SEARCH_SCROLL_REQUEST)
				.beginControlFlow("try")
				.addStatement("$T response = client.searchScroll(searchScrollRequest).get()", SEARCH_RESPONSE)
				.addStatement("return searchIds(response)")
				.endControlFlow()
				.beginControlFlow("catch ($T e)", InterruptedException.class)
				.addStatement("e.printStackTrace()")
				.addStatement("throw new $T(\"failed to searchIds the document\", e)", ELASTICSEARCH_EXCEPTION)
				.endControlFlow()
				.beginControlFlow("$T e", ExecutionException.class)
				.addStatement("e.printStackTrace()")
				.addStatement("throw new $T(\"failed to searchIds the document\", e)", ELASTICSEARCH_EXCEPTION)
				.endControlFlow()
				.build();
		return method;
	}
	
	private MethodSpec searchIds() {
		final MethodSpec method = MethodSpec.methodBuilder("searchIds")
				.addModifiers(Modifier.PRIVATE)
				.returns(ParameterizedTypeName.get(List.class, String.class))
				.addParameter(SEARCH_RESPONSE, "response", Modifier.FINAL)
				.addStatement("final List<String> ids = $T.newArrayList()", Lists.class)
				.addStatement("final $T[] searchHits  = response.getHits().getHits()", SEARCH_HIT)
				.beginControlFlow("if (null != searchHits && 0 != searchHits.length)")
				.beginControlFlow("$T searchHit : searchHits", SEARCH_HIT)
				.addStatement("ids.add(searchHit.getId())")
				.endControlFlow()
				.endControlFlow()
				.addStatement("return ids")
				.build();
		return method;
	}
	
	public JavaFile build() {
		final Builder builder = TypeSpec.interfaceBuilder(className.simpleName()).addModifiers(Modifier.PUBLIC);
		builder.addMethod(simpleSearch());
		builder.addMethod(search());
		builder.addMethod(searchFacet());
		builder.addMethod(scroll());
		builder.addMethod(scrollIds());
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
		builder.addMethod(simpleSearchImpl());
		builder.addMethod(searchImpl());
		builder.addMethod(searchFacetImpl());
		builder.addMethod(scrollImpl());
		builder.addMethod(scrollIdsImpl());
		builder.addMethod(searchIds());
		builder.addMethod(saveImpl());
		builder.addMethod(updateImpl());
		builder.addMethod(deleteImpl());
		builder.addMethod(indexRequest());
		return JavaFile.builder(classImplName.packageName(), builder.build()).build();
	}
	
	public static void main(String[] args) throws IOException {
		final Class<?> clazz = TestDomain.class;
		final ElasticRepositoryBuilder clazzBuilder = new ElasticRepositoryBuilder(clazz);
		clazzBuilder.build().writeTo(System.out);
		clazzBuilder.buildImpl().writeTo(System.out);
	}
}
