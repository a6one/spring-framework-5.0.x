package spring.example.dao;

public class personStaticFactory {

	public static Person newInstance(String name, Integer age, UserDao userDao) {
		return new Person(name, age, userDao);
	}
}
