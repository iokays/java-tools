package com.iokays.build.repository.builder;

import java.io.IOException;

import com.iokays.tools.test.domain.TestDomain;

public class ElasticRepositoryBuilderTest {
	public static void main(String[] args) throws IOException {
		final Class<?> clazz = TestDomain.class;
		final ElasticRepositoryBuilder clazzBuilder = new ElasticRepositoryBuilder(clazz);
		clazzBuilder.build().writeTo(System.out);
		clazzBuilder.buildImpl().writeTo(System.out);
	}
}
