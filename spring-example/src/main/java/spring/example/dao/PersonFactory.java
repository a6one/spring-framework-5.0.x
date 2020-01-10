package spring.example.dao;

public class PersonFactory {

	public Person newInstance(String name, Integer age, UserDao userDao) {
		return new Person(name, age, userDao);
	}

}
