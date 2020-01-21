package spring.example.dao;

public class Outer {
	/** 姓名 **/
	private String name;

	/** 年龄 **/
	private int age;

	/** 内部bean **/
	private Inner inner;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Inner getInner() {
		return inner;
	}

	public void setInner(Inner inner) {
		this.inner = inner;
	}
}
