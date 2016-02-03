package com.iokays.build.domain.builder;

import java.lang.reflect.Field;
import java.util.HashMap;

import javax.lang.model.element.Modifier;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public class ClazzBuilderBuilder {

	protected final Class<?> clazz;
	protected final String clazzName;
	protected final String packageName;
	protected boolean hasUpdate = false;
	protected Class<?> filedClazz;
	
	public ClazzBuilderBuilder(Class<?> clazz) {
		this.clazz = clazz;
		final int lastIndex = clazz.getName().lastIndexOf(".") + 1;
		packageName = clazz.getName().substring(0, lastIndex) +"builder";
		clazzName = this.clazz.getSimpleName() + "Builder";
	}

	public ClazzBuilderBuilder hasUpdate(Class<?> filedClazz) {
		this.hasUpdate = true;
		this.filedClazz = filedClazz;
		return this;
	}

	public JavaFile build() {
		final Builder builder = TypeSpec.classBuilder(clazzName).addModifiers(Modifier.PUBLIC);
		final Field[] fields = clazz.getDeclaredFields();
		
		//entity
		FieldSpec entity = FieldSpec.builder(clazz, "entity")
			    .addModifiers(Modifier.PRIVATE, Modifier.FINAL).initializer("new $T()", clazz)
			    .build();
		builder.addField(entity);
		
		final TypeName mapType = ParameterizedTypeName.get(ClassName.get("java.util", "Map"), ClassName.get("java.lang", "String"), ClassName.get("java.lang", "Object"));
		//updateMap
		if (hasUpdate) {
			FieldSpec updateMap = FieldSpec.builder(mapType, "map")
					.addModifiers(Modifier.PRIVATE, Modifier.FINAL)
					.initializer("new $T<>()", HashMap.class)
					.build();
			builder.addField(updateMap);
		}
		
		//get, set 方法
		if (null != fields && 0 != fields.length) {
			for (Field field : fields) {
				final String name = field.getName();
				final int mod = field.getModifiers();	//修饰符
				if ((java.lang.reflect.Modifier.isPrivate(mod) || java.lang.reflect.Modifier.isProtected(mod))
						&& !java.lang.reflect.Modifier.isFinal(mod)
						&& !java.lang.reflect.Modifier.isStatic(mod)) {
					MethodSpec.Builder setMethodBuilder = MethodSpec.methodBuilder(name).addModifiers(Modifier.PUBLIC)
							.returns(ClassName.get(packageName, clazzName))
							.addParameter(field.getType(), name)
							.addStatement("entity.set$L($L)", name.substring(0, 1).toUpperCase() + name.substring(1, name.length()), name);
					if (hasUpdate) {
						setMethodBuilder.addStatement("map.put($L.$L, $L)", filedClazz.getSimpleName(), CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, field.getName()), field.getName());
					}
					setMethodBuilder.addStatement("return this");
					setMethodBuilder.addJavadoc("set $L value\n", field.getName());
					setMethodBuilder.addJavadoc("@param $L XXXXXXX\n", field.getName());
					setMethodBuilder.addJavadoc("@return this\n");
					builder.addMethod(setMethodBuilder.build());
					
					MethodSpec.Builder getMethodBuilder = MethodSpec.methodBuilder(name).addModifiers(Modifier.PUBLIC)
							.returns(field.getType())
							.addStatement("return entity.get$L()", name.substring(0, 1).toUpperCase() + name.substring(1, name.length()));
					getMethodBuilder.addJavadoc("get $L value\n", field.getName());
					setMethodBuilder.addJavadoc("@return XXXXXXX\n");
					builder.addMethod(getMethodBuilder.build());
				}
			}
		}
		
		final MethodSpec entityMethod = MethodSpec.methodBuilder("entity").addModifiers(Modifier.PUBLIC).returns(clazz).addStatement("return entity").build();
		builder.addMethod(entityMethod);
		
		final MethodSpec mapMethod = MethodSpec.methodBuilder("map").addModifiers(Modifier.PUBLIC).returns(mapType).addStatement("return map").build();
		builder.addMethod(mapMethod);
		
		return JavaFile.builder(packageName, builder.build()).build();
	}
}
