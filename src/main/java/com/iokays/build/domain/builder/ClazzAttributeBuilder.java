package com.iokays.build.domain.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ClazzAttributeBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(ClazzAttributeBuilder.class);
	
	public static List<String> prFields(final Class<?> entity) {
		final List<String> names = Lists.newArrayList();
		final Field[] fields = entity.getDeclaredFields();
		if (null != fields && 0 != fields.length) {
			for (Field field : fields) {
				logger.debug("field: {}", field);
				final String name = field.getName();
				final int mod = field.getModifiers();	//修饰符
				if ((java.lang.reflect.Modifier.isPrivate(mod) || java.lang.reflect.Modifier.isProtected(mod))
						&& !java.lang.reflect.Modifier.isFinal(mod)
						&& !java.lang.reflect.Modifier.isStatic(mod)) {
					names.add(name);
				}
			}
		}
		return names;
	}
	
	public static List<String> build(final String sourceName, Class<?> source, final String targetName, Class<?> target) {
		final List<String> codes = Lists.newArrayList();
		
		final Map<String, Method> methodMap = Maps.newHashMap();
		final Method[] sourceMethods = source.getMethods();
		if (0 != sourceMethods.length) {
			for (Method method : sourceMethods) {
				if (0 == method.getParameterTypes().length && method.getReturnType() != void.class) {
					String name = method.getName().toLowerCase();
					if (name.length() >= 3 && name.substring(0, 3).equals("get")) {
						name = name.substring(3, name.length());
					}
					methodMap.put(name, method);
				}
			}
		}
		logger.info("methodMap: {}", methodMap);
		
		final Method[] targetMethods = target.getMethods();
		if (0 != targetMethods.length) {
			for (Method setMethod : targetMethods) {
				if (1 == setMethod.getParameterTypes().length) {
					logger.info("name: {}, parameterTypes: {}", setMethod.getName(), setMethod.getParameterTypes().length);
					String name = setMethod.getName().toLowerCase();
					if (name.length() >= 3 && name.substring(0, 3).equals("set")) {
						name = name.substring(3, name.length());
					}
					if (methodMap.containsKey(name)) {
						final Class<?> setParemType = setMethod.getParameterTypes()[0];
						final Method getMethod = methodMap.get(name);
						final Class<?> getReturnType = getMethod.getReturnType();
						if (setParemType == getReturnType) {
							final String code = targetName + "." + setMethod.getName() + "(" + sourceName +"." + getMethod.getName() + "())";
							codes.add(code);
						} else if ((getReturnType == boolean.class && setParemType == Boolean.class)
								|| (getReturnType == short.class && setParemType == Short.class)
								|| (getReturnType == byte.class && setParemType == Byte.class)
								|| (getReturnType == short.class && setParemType == Short.class)
								|| (getReturnType == int.class && setParemType == Integer.class)
								|| (getReturnType == float.class && setParemType == Float.class)
								|| (getReturnType == double.class && setParemType == Double.class)
								) {
							final String code = targetName + "." + setMethod.getName() + "(" + sourceName +"." + getMethod.getName() + "())";
							codes.add(code);
						} else if ((setParemType == boolean.class && getReturnType == Boolean.class)
								|| (setParemType == short.class && getReturnType == Short.class)
								|| (setParemType == byte.class && getReturnType == Byte.class)
								|| (setParemType == short.class && getReturnType == Short.class)
								|| (setParemType == int.class && getReturnType == Integer.class)
								|| (setParemType == float.class && getReturnType == Float.class)
								|| (setParemType == double.class && getReturnType == Double.class)
								) {
							final String code = "if (null != " + sourceName +"." + getMethod.getName() +  "()) {" + targetName + "." + setMethod.getName() + "(" + sourceName +"." + getMethod.getName() + "()); }";
							codes.add(code);
						}
						
					}
				}
			}
		}
		logger.info("codes: {}", codes);
		return codes;
	}

}
