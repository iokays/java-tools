package com.iokays.build.repository.builder;

import java.io.IOException;

import com.iokays.tools.test.domain.TestDomain;

public class MybatisRepositoryBuilderTest {
	public static void main(String[] args) throws IOException {
		final Class<?> clazz = TestDomain.class;
		final MybatisRepositoryBuilder clazzBuilder = new MybatisRepositoryBuilder(clazz, "id", "pageId");
		clazzBuilder.build().writeTo(System.out);
		clazzBuilder.buildXML();
	}
}
