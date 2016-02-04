package com.iokays.build.domain.builder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class IceDocBuilder {

	private static final Logger logger = LoggerFactory.getLogger(IceDocBuilder.class);

	private final String iceParams;
	private static final String struct = "struct";

	public IceDocBuilder(final String iceParams) {
		this.iceParams = iceParams;
	}

	public List<StructParam> build() throws IOException {
		final List<StructParam>  structs = Lists.newArrayList();
		
		final List<String> list = Files.readAllLines(Paths.get(iceParams));
		final String content = Joiner.on("").join(list);
		int mark = content.indexOf(struct);
		while (mark != -1) {
			final int form = content.indexOf("{", mark);
			final int end = content.indexOf("}", mark);
			
			final StructParam structParam = new StructParam();
			structParam.setName(content.substring(mark, form).trim());
			
			final String str = content.substring(form + 1, end).trim();
			
			final List<ObjectParam> iceParams = Lists.newArrayList();
			if (StringUtils.isNotBlank(str)) {
				String[] params = str.split(";");
				if (null != params && 0 != params.length) {
					for (String param : params) {
						param = param.trim();
						final ObjectParam ice = new ObjectParam();
						final int fromDoc = param.indexOf("/**");
						int endDoc = 0;
						if (-1 != fromDoc) {
							endDoc = param.indexOf("*/");
							if (-1 != endDoc) {
								ice.setJavadoc(param.substring(fromDoc + 3, endDoc).trim());
							}
						}
						param = param.substring(-1 != endDoc ? endDoc + 2 : 0).trim();
						final int endType = param.indexOf(" ");
						if (-1 != endType) {
							ice.setType(param.substring(0, endType).trim());
						}
						
						param = param.substring(-1 != endType ? endType : 0).trim();
						ice.setName(param);
						
						
//						final int fromName = param.lastIndexOf(" ");
//						if (-1 != fromName) {
//							ice.setName(param.substring(fromName, param.length()).trim());
//
//							final String typeStr = param.substring(0, fromName);
//							final int fromType = typeStr.lastIndexOf("*/");
//							ice.setType(
//									typeStr.substring(fromType != -1 ? fromType + 2 : 0, typeStr.length()).trim());
//
//						}
						if (StringUtils.isNotBlank(ice.getName())) {
							iceParams.add(ice);
						}

					}
				}
			}
			structParam.setParams(iceParams);
			structs.add(structParam);
			
			mark = content.indexOf(struct, end);
		}
		logger.debug("structs: {}", structs);

		return structs;
	}

}
