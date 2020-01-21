package spring.example.dao;

public abstract class RoleDao {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private UserDao userDao;

	public RoleDao(UserDao userDao) {
		//不能通过带参构造处理循环依赖
		this.userDao = userDao;
	}

	public void role() {
		System.out.println("roleDao");
	}

}
