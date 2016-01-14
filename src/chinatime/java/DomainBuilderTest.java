
import java.io.IOException;

import org.junit.Test;

import com.chinatime.datacenter.org.pojo.ProductColorSearch;
import com.iokays.tools.domain.builder.ClazzBuilderBuilder;
import com.iokays.tools.domain.builder.ClazzFieldsBuilder;

public class DomainBuilderTest {

	@Test
	public void testBuild() throws IOException {
		final Class<?> clazz = ProductColorSearch.class;

		final ClazzBuilderBuilder clazzBuilder = new ClazzBuilderBuilder(clazz).hasUpdate();
		clazzBuilder.build().writeTo(System.out);

//		final ClazzFieldsBuilder clazzFieldsBuilder = new ClazzFieldsBuilder(clazz);
//		clazzFieldsBuilder.build().writeTo(System.out);
	}
}
