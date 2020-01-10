package spring.example.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring.example.dao.MyMapper;
import spring.example.springmvc.AppConfig;

public class UserTest {


	/**
	 * ClassPathXmlApplicationContext:入口
	 * <p>
	 * <p>
	 * System.setProperty("spring", "classpath");
	 * ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("${spring}:config.xml");
	 * <p>
	 * 配置文件的四种写法 ：
	 * spring.xml
	 * classpath*:spring.xml
	 * System.setProperty("spring","classpath")
	 * ${spring}:spring.xml
	 * 可以看出配置文件路径是支持ant风格的，也就是可以这么写:
	 * new ClassPathXmlApplicationContext("con*.xml");
	 * <p>
	 * <p>
	 * new RuntimeBeanReference(propertyValue)
	 *
	 * 任何实现java.beans.PropertyEditor接口的类都是属性编辑器。
	 * 属性编辑器的主要功能就是将外部的设置值转换为JVM内部的对应类型，所以属性编辑器其实就是一个类型转换器
	 *
	 * @Lookup replaceMethod 处理原型的循环依赖问题
	 *
	 * ========================================================================
	 * bean生命周期：
	 * new AnnotationConfigApplicationContext(AppConfig.class)
	 *
	 *
	 *
	 */

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
//		context.register(UserDao.class);
//		context.refresh();
//		UserDao userDao = (UserDao) context.getBean("userDao");
//		System.out.println(userDao.getAll());
		//ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring.xml");
		MyMapper userDao = (MyMapper) context.getBean("myMapper");
		System.out.println(userDao.getAll());

//		Integer i1 = 100;
//		Integer i2 = 200;
//		Long l3 = 300L;
//		System.out.println(l3 == (i1 + i2));
//		System.out.println(l3.equals(i1 + i2));
	}
}
