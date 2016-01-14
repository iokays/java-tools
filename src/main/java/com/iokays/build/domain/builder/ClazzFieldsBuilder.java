package com.iokays.build.domain.builder;

import java.lang.reflect.Field;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public class ClazzFieldsBuilder {
	
	private final ClassName className;
	private final Class<?> clazz;
	
	public ClazzFieldsBuilder(Class<?> clazz) {
		this.clazz = clazz;
		final int lastIndex = clazz.getName().lastIndexOf(".");
		className = ClassName.get(clazz.getName().substring(0, lastIndex), this.clazz.getSimpleName() + "Fields");
	}

	public JavaFile build() {
		final Builder builder = TypeSpec.interfaceBuilder(className.simpleName()).addModifiers(Modifier.PUBLIC);
		
		final Field[] fields = clazz.getDeclaredFields();

		//get, set 方法
		if (null != fields && 0 != fields.length) {
			for (Field field : fields) {
				final String name = field.getName();
				final int mod = field.getModifiers();	//修饰符
				if ((java.lang.reflect.Modifier.isPrivate(mod) || java.lang.reflect.Modifier.isProtected(mod))
						&& !java.lang.reflect.Modifier.isFinal(mod)
						&& !java.lang.reflect.Modifier.isStatic(mod)) {
					
					FieldSpec param = FieldSpec.builder(String.class, name)
						    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer("$S", name)
						    .build();
					builder.addField(param);
					
				}
			}
		}
		
		return JavaFile.builder(className.packageName(), builder.build()).build();
	}
	
	public ClassName className() {
		return className;
	}

}
