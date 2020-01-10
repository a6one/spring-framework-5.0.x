package spring.example.dao;

public class Person {

	private String name;
	private Integer age;
	private UserDao userDao;

	public Person(String name, Integer age, UserDao userDao) {
		this.name = name;
		this.age = age;
		this.userDao = userDao;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
}
