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

	public List<ObjectParam> build() throws IOException {
		final List<String> list = Files.readAllLines(Paths.get(iceParams));
		final String content = Joiner.on("").join(list);
		final List<String> structs = Lists.newArrayList();
		int mark = content.indexOf(struct);
		while (mark != -1) {
			final int form = content.indexOf("{", mark);
			final int end = content.indexOf("}", mark);
			structs.add(content.substring(form + 1, end).trim());
			mark = content.indexOf(struct, end);
		}
		logger.debug("structs: {}", structs);

		final List<ObjectParam> iceParams = Lists.newArrayList();

		if (CollectionUtils.isNotEmpty(structs)) {
			for (String str : structs) {
				if (StringUtils.isNotBlank(str)) {
					String[] params = str.split(";");
					if (null != params && 0 != params.length) {
						for (String param : params) {
							param = param.trim();
							final ObjectParam ice = new ObjectParam();
							final int fromDoc = param.indexOf("/**");
							if (-1 != fromDoc) {
								final int endDoc = param.indexOf("*/");
								if (-1 != endDoc) {
									ice.setJavadoc(param.substring(fromDoc + 3, endDoc).trim());
								}
							}
							final int fromName = param.lastIndexOf(" ");
							if (-1 != fromName) {
								ice.setName(param.substring(fromName, param.length()).trim());

								final String typeStr = param.substring(0, fromName);
								final int fromType = typeStr.lastIndexOf("*/");
								ice.setType(
										typeStr.substring(fromType != -1 ? fromType + 2 : 0, typeStr.length()).trim());

							}
							if (StringUtils.isNotBlank(ice.getName())) {
								iceParams.add(ice);
							}

						}
					}
				}
			}
		}

		return iceParams;
	}

}
