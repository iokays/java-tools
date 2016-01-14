import java.io.IOException;

import org.junit.Test;

import com.iokays.build.repository.builder.ElasticRepositoryBuilder;
import com.iokays.tools.test.domain.TestDomain;

public class ElasticRepositoryBuilderTest {
	
	@Test
	public void build() throws IOException {
		final Class<?> clazz = TestDomain.class;
		final ElasticRepositoryBuilder clazzBuilder = new ElasticRepositoryBuilder(clazz);
		clazzBuilder.build().writeTo(System.out);
		clazzBuilder.buildImpl().writeTo(System.out);
	}

}
