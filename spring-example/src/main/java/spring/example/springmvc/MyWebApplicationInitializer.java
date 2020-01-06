package spring.example.springmvc;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;


/**
 * Tomcat -> servletContainerInitializer(servlet3.0规范) -> WebApplicationInitializer
 */
public class MyWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[0];
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[]{TestController.class};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}
}
