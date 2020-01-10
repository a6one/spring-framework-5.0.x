package spring.example.beanpostprocesssor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;

public class MyDestructionAwareBeanPostProcessor implements DestructionAwareBeanPostProcessor {

	//DisposableBean->destory方法的时候调用
	@Override
	public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {

	}

	@Override
	public boolean requiresDestruction(Object bean) {
		return false;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return null;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return null;
	}
}
