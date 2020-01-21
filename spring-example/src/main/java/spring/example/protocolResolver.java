package spring.example;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * 协议转换器，将my转换为classpath
 */
public class protocolResolver implements ProtocolResolver {
	@Override
	public Resource resolve(String location, ResourceLoader resourceLoader) {
		if (location.startsWith("my")) {
			return resourceLoader.getResource(location.replace("my", "classpath"));
		}
		return null;
	}

	public void test(){

		DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
		resourceLoader.addProtocolResolver(new protocolResolver());
		Resource resource = resourceLoader.getResource("my:/v2/day01.xml");
	}


}
