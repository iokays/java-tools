package com.iokays.build.service.builder;

import java.io.IOException;

import com.iokays.tools.test.domain.TestDomain;

public class ElasticServiceBuilderTest {
	public static void main(String[] args) throws IOException {
		final Class<?> clazz = TestDomain.class;
		final ElasticServiceBuilder clazzBuilder = new ElasticServiceBuilder(clazz);
		clazzBuilder.build().writeTo(System.out);
		clazzBuilder.buildImpl().writeTo(System.out);
	}
}
