
import java.io.IOException;

import org.junit.Test;

import com.iokays.build.domain.builder.ClazzBuilderBuilder;
import com.iokays.build.domain.builder.ClazzFieldsBuilder;
import com.iokays.tools.test.domain.TestDomain;

public class DomainBuilderBuilderTest {

	@Test
	public void testBuild() throws IOException {
		final Class<?> clazz = TestDomain.class;

		final ClazzBuilderBuilder clazzBuilder = new ClazzBuilderBuilder(clazz).hasUpdate(clazz);
		clazzBuilder.build().writeTo(System.out);

		final ClazzFieldsBuilder clazzFieldsBuilder = new ClazzFieldsBuilder(clazz);
		clazzFieldsBuilder.build().writeTo(System.out);
	}
}
