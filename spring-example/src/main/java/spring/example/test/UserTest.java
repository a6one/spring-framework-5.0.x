package spring.example.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring.example.dao.UserDao;

public class UserTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(UserDao.class);
		context.refresh();
		UserDao userDao = (UserDao) context.getBean("userDao");
		System.out.println(userDao.getAll());
	}
}
