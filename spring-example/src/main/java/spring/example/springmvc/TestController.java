package spring.example.springmvc;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@RequestMapping("test.do")
	public String test(String name) throws Exception {
		System.out.println("test spring-mvc success ");
		return "Spring-MVC";
	}
}
