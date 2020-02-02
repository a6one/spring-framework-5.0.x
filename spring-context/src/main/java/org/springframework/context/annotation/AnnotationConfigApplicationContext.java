/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.annotation;

import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.function.Supplier;

/**
 * Standalone application context, accepting annotated classes as input - in particular
 * {@link Configuration @Configuration}-annotated classes, but also plain
 * {@link org.springframework.stereotype.Component @Component} types and JSR-330 compliant
 * classes using {@code javax.inject} annotations. Allows for registering classes one by
 * one using {@link #register(Class...)} as well as for classpath scanning using
 * {@link #scan(String...)}.
 *
 * <p>In case of multiple {@code @Configuration} classes, @{@link Bean} methods defined in
 * later classes will override those defined in earlier classes. This can be leveraged to
 * deliberately override certain bean definitions via an extra {@code @Configuration}
 * class.
 *
 * <p>See @{@link Configuration}'s javadoc for usage examples.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 3.0
 * @see #register
 * @see #scan
 * @see AnnotatedBeanDefinitionReader
 * @see ClassPathBeanDefinitionScanner
 * @see org.springframework.context.support.GenericXmlApplicationContext
 */
public class AnnotationConfigApplicationContext extends GenericApplicationContext implements AnnotationConfigRegistry {

	private final AnnotatedBeanDefinitionReader reader;

	private final ClassPathBeanDefinitionScanner scanner;


	/**
	 * Create a new AnnotationConfigApplicationContext that needs to be populated
	 * through {@link #register} calls and then manually {@linkplain #refresh refreshed}.
	 */
	public AnnotationConfigApplicationContext() {
		this.reader = new AnnotatedBeanDefinitionReader(this);
		this.scanner = new ClassPathBeanDefinitionScanner(this);
	}

	/**
	 * Create a new AnnotationConfigApplicationContext with the given DefaultListableBeanFactory.
	 * @param beanFactory the DefaultListableBeanFactory instance to use for this context
	 */
	public AnnotationConfigApplicationContext(DefaultListableBeanFactory beanFactory) {
		super(beanFactory);
		this.reader = new AnnotatedBeanDefinitionReader(this);
		this.scanner = new ClassPathBeanDefinitionScanner(this);
	}

	/**
	 * Create a new AnnotationConfigApplicationContext, deriving bean definitions
	 * from the given annotated classes and automatically refreshing the context.
	 * @param annotatedClasses one or more annotated classes,
	 * e.g. {@link Configuration @Configuration} classes

	 @Configuration
	 @ComponeScan("com.exmaple")
	   public a {

	}
	 */
	public AnnotationConfigApplicationContext(Class<?>... annotatedClasses) {
		/**
		 * AnnotatedBeanDefinitionReader：基于注解的reader
		 * ClassPathBeanDefinitionScanner：扫描路径的，指定的注解
		 */
		this();
		/**
		 * 注册自身的配置类：
		 * @Configuration
		 * @ComponeScan("com.example")
		 * public A{}
		 */
		register(annotatedClasses);
		/**
		 * 269,6， 5，3 ，3，4
		 *
		 * AbstractApplicationContext-->refresh()
		 * AbstractRefreshableApplicationContext->refreshBeanFactory()
		 * 			 DefaultListableBeanFactory<-->createBeanFactory()
		 * 			 						    ->loadBeanDefinitions()
		 *BeanDefinitionValueResolver : 形参 + 早期对象的属性
		 *=================================================
		 * 1.单例循环依赖  3点
		 * 构造方法无法处理循环依赖（待）
		 * 2.原型不能解决循环依赖
		 * 	原因：没有使用三级缓存，每次创建都是直接创建
		 *
		 * org.springframework.beans.factory.support.AbstractBeanFactory#getBean(java.lang.String)
		 * org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean(java.lang.String, java.lang.Class, java.lang.Object[], boolean)
		 * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
		 * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
		 * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBeanInstance(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
		 *
		 *
		 * populateBean-> 3点
		 *
		 * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyPropertyValues(java.lang.String, org.springframework.beans.factory.config.BeanDefinition, org.springframework.beans.BeanWrapper, org.springframework.beans.PropertyValues)
		 * org.springframework.beans.factory.support.BeanDefinitionValueResolver#resolveValueIfNecessary(java.lang.Object, java.lang.Object)
		 * org.springframework.beans.factory.support.BeanDefinitionValueResolver#resolveReference(java.lang.Object, org.springframework.beans.factory.config.RuntimeBeanReference)
		 * //获取ref-bean对象
		 * 	bean = this.beanFactory.getBean(refName)--->AbstractBeanFactory#getBean
		 *
		 * BeanWrapper ->放入三级缓存中 ->exposedObject
		 * populateBean->填充，包括循环依赖
		 * initializeBean-->初始化的方法
		 *
		 * 案例解读：A->B
		 * 	1.A实例化的时候-->早期对象-->存储到三级缓冲中的
		 * 	2.populateBean时候-->B->早期对象-->存储到三级缓冲中的
		 * 	3.B->populateBean时候，就直接从，三级缓冲中获取A,
		 * 	4.B实例后，A在进行实例化
		 *
		 *
		 *=================================================
		 *
		 *
		 *
		 * ======================================================
		 * BeanFactoryPostProcessor:beanFactory的后置处理器
		 * BeanDefinitionRegistryPostProcessor:beanFactory的后置处理器
		 * PropertyEditorRegistrar:自定义属性编辑器
		 * singletonObjects：单例池
		 * //实例化bean时候的后置处理器bean
		 * InstantiationAwareBeanPostProcessor
		 * MergedBeanDefinitionPostProcessor
		 * SmartInstantiationAwareBeanPostProcessor
		 * DestructionAwareBeanPostProcessor
		 *
		 *
		 * AbstractAutoProxyCreator
		 * AnnotationAwareAspectJAutoProxyCreator
		 * //AOP的入口： 2 ,4 3
		 *
		 * AbstractAutoProxyCreator --> proxyFactory.getProxy(getProxyClassLoader());
		 * 	org.springframework.aop.framework.ProxyFactory#getProxy(java.lang.ClassLoader)
		 * 	  org.springframework.aop.framework.AopProxy#getProxy(java.lang.ClassLoader)
		 * 		org.springframework.aop.framework.CglibAopProxy#getProxy(java.lang.ClassLoader)-->org.springframework.aop.framework.CglibAopProxy#getCallbacks(java.lang.Class)
		 *
		 * 				org.springframework.aop.framework.AdvisedSupport#getInterceptorsAndDynamicInterceptionAdvice(java.lang.reflect.Method, java.lang.Class)
		 * 					org.springframework.aop.framework.AdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice(org.springframework.aop.framework.Advised, java.lang.reflect.Method, java.lang.Class)
		 *
		 * 						org.springframework.aop.framework.DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice(org.springframework.aop.framework.Advised, java.lang.reflect.Method, java.lang.Class)
		 * 							org.springframework.aop.framework.InterceptorAndDynamicMethodMatcher#InterceptorAndDynamicMethodMatcher(org.aopalliance.intercept.MethodInterceptor, org.springframework.aop.MethodMatcher)
		 *
		 * org.springframework.aop.framework.ReflectiveMethodInvocation#proceed()
		 *
		 *
		 *
		 * DefaultAdvisorAdapterRegistry
		 * AdvisorAdapter
		 * =============================================================
		 * TransactionDefinition
		 * PlatformTransactionManager
		 * TransactionStatus
		 *
		 *
		 * org.springframework.aop.framework.ReflectiveMethodInvocation#proceed()
		 * 	org.springframework.transaction.interceptor.TransactionInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
		 * 		org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction(java.lang.reflect.Method, java.lang.Class, org.springframework.transaction.interceptor.TransactionAspectSupport.InvocationCallback)
		 *===============================================================
		 *
		 * 事务总结：2（传播行为 + 隔离级别）,4  | 2,3 【4】-->jdbc依赖（事务的开启）
		 * AutoProxyRegistrar-->
		 * 		InfrastructureAdvisorAutoProxyCreator | AbstractAutoProxyCreator（注册一个后置处理器）
		 *
		 * ProxyTransactionManagementConfiguration
		 * 	BeanFactoryTransactionAttributeSourceAdvisor
		 * 	TransactionAttributeSource
		 * 	TransactionInterceptor
		 *
		 * org.springframework.aop.framework.ReflectiveMethodInvocation#proceed()
		 *
		 * //声明式事务
		 * spring-声明式事务，只支持RuntimeException和Exception 不支持Exception
		 * 		 * 1.声明式事务：依赖于AOP
		 * 		 * 2.声明式事务：支持public方法
		 *
		 *	//编程事务
		 * TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
		 *                        @Override
		 * 			public void beforeCommit(boolean readOnly) {
		 * 				System.out.println("==回调,事物提交之前");
		 * 				super.beforeCommit(readOnly);
		 * 			}
		 *
		 * 			@Override
		 * 			public void afterCommit() {
		 * 				System.out.println("==回调,事物提交之后");
		 * 				super.afterCommit();
		 * 			}
		 *
		 * 			@Override
		 * 			public void beforeCompletion() {
		 * 				super.beforeCompletion();
		 * 				System.out.println("==回调,事物完成之前");
		 * 			}
		 *
		 * 			@Override
		 * 			public void afterCompletion(int status) {
		 * 				super.afterCompletion(status);
		 * 				System.out.println("==回调,事物完成之后");
		 * 			}
		 * 		});
		 *
		 * 事务的问题：
		 * 	1.声明式事务嵌套是基于aop代理对象-->exposeProxy=true | aopContext.getXxx
		 * 	2.嵌套事务
		 *	新创建的事物就持有了被挂起事物的的属性，就会形成一个事物链。而且新创建的事物transaction参数为null，所以PROPAGATION_NOT_SUPPORTED特性是不会真正开启事物的。
		 *	情况1：A，B都使用事务注解：@Transactional(rollbackFor = Exception.class)，
		 *		 a:catch异常
		 *		 		throw  		A回滚，B回滚
		 *		 		no throw	A回滚，B回滚
		 *		 a:未catch a | b一样 A回滚，B回滚
		 *	情况2：A使用事务注解：@Transactional(rollbackFor = Exception.class)，B不使用事务；
		 *		a:catch异常
		 *			throw    A回滚，B回滚
		 *			no throw A，B都不回滚
		 *		a:未catch 	A回滚，B回滚
		 *  情况3：A不使用事务，B使用事务注解：@Transactional(rollbackFor = Exception.class)
		 *  	只对b事务有效
		 *  情况4：A使用默认事务注解：@Transactional(rollbackFor = Exception.class)
		 * 		  B使用新事务注解：@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
		 * 		  B发生异常，	A未catch	A不回滚，B回滚
		 *		  B发生异常，	A catch了异常，记录日志，未抛出异常	A不回滚，B回滚
		 * 		  B发生异常，	A catch了异常，记录日志，抛出异常	A回滚，B回滚
		 * 		  B执行成功后，	A发生了异常	A回滚，B不回滚
		 * 	注：事务的隔离性
		 *
		 *
		 */
		refresh();
	}

	/**
	 * Create a new AnnotationConfigApplicationContext, scanning for bean definitions
	 * in the given packages and automatically refreshing the context.
	 * @param basePackages the packages to check for annotated classes
	 */
	public AnnotationConfigApplicationContext(String... basePackages) {
		this();
		scan(basePackages);
		refresh();
	}


	/**
	 * Propagates the given custom {@code Environment} to the underlying
	 * {@link AnnotatedBeanDefinitionReader} and {@link ClassPathBeanDefinitionScanner}.
	 */
	@Override
	public void setEnvironment(ConfigurableEnvironment environment) {
		super.setEnvironment(environment);
		this.reader.setEnvironment(environment);
		this.scanner.setEnvironment(environment);
	}

	/**
	 * Provide a custom {@link BeanNameGenerator} for use with {@link AnnotatedBeanDefinitionReader}
	 * and/or {@link ClassPathBeanDefinitionScanner}, if any.
	 * <p>Default is {@link org.springframework.context.annotation.AnnotationBeanNameGenerator}.
	 * <p>Any call to this method must occur prior to calls to {@link #register(Class...)}
	 * and/or {@link #scan(String...)}.
	 * @see AnnotatedBeanDefinitionReader#setBeanNameGenerator
	 * @see ClassPathBeanDefinitionScanner#setBeanNameGenerator
	 */
	public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
		this.reader.setBeanNameGenerator(beanNameGenerator);
		this.scanner.setBeanNameGenerator(beanNameGenerator);
		getBeanFactory().registerSingleton(
				AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, beanNameGenerator);
	}

	/**
	 * Set the {@link ScopeMetadataResolver} to use for detected bean classes.
	 * <p>The default is an {@link AnnotationScopeMetadataResolver}.
	 * <p>Any call to this method must occur prior to calls to {@link #register(Class...)}
	 * and/or {@link #scan(String...)}.
	 */
	public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
		this.reader.setScopeMetadataResolver(scopeMetadataResolver);
		this.scanner.setScopeMetadataResolver(scopeMetadataResolver);
	}


	//---------------------------------------------------------------------
	// Implementation of AnnotationConfigRegistry
	//---------------------------------------------------------------------

	/**
	 * Register one or more annotated classes to be processed.
	 * <p>Note that {@link #refresh()} must be called in order for the context
	 * to fully process the new classes.
	 * @param annotatedClasses one or more annotated classes,
	 * e.g. {@link Configuration @Configuration} classes
	 * @see #scan(String...)
	 * @see #refresh()
	 */
	public void register(Class<?>... annotatedClasses) {
		Assert.notEmpty(annotatedClasses, "At least one annotated class must be specified");
		this.reader.register(annotatedClasses);
	}

	/**
	 * Perform a scan within the specified base packages.
	 * <p>Note that {@link #refresh()} must be called in order for the context
	 * to fully process the new classes.
	 * @param basePackages the packages to check for annotated classes
	 * @see #register(Class...)
	 * @see #refresh()
	 */
	public void scan(String... basePackages) {
		Assert.notEmpty(basePackages, "At least one base package must be specified");
		this.scanner.scan(basePackages);
	}


	//---------------------------------------------------------------------
	// Convenient methods for registering individual beans
	//---------------------------------------------------------------------

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations, and optionally providing explicit constructor
	 * arguments for consideration in the autowiring process.
	 * <p>The bean name will be generated according to annotated component rules.
	 * @param annotatedClass the class of the bean
	 * @param constructorArguments argument values to be fed into Spring's
	 * constructor resolution algorithm, resolving either all arguments or just
	 * specific ones, with the rest to be resolved through regular autowiring
	 * (may be {@code null} or empty)
	 * @since 5.0
	 */
	public <T> void registerBean(Class<T> annotatedClass, Object... constructorArguments) {
		registerBean(null, annotatedClass, constructorArguments);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations, and optionally providing explicit constructor
	 * arguments for consideration in the autowiring process.
	 * @param beanName the name of the bean (may be {@code null})
	 * @param annotatedClass the class of the bean
	 * @param constructorArguments argument values to be fed into Spring's
	 * constructor resolution algorithm, resolving either all arguments or just
	 * specific ones, with the rest to be resolved through regular autowiring
	 * (may be {@code null} or empty)
	 * @since 5.0
	 */
	public <T> void registerBean(@Nullable String beanName, Class<T> annotatedClass, Object... constructorArguments) {
		this.reader.doRegisterBean(annotatedClass, null, beanName, null,
				bd -> {
					for (Object arg : constructorArguments) {
						bd.getConstructorArgumentValues().addGenericArgumentValue(arg);
					}
				});
	}

	@Override
	public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, @Nullable Supplier<T> supplier,
			BeanDefinitionCustomizer... customizers) {

		this.reader.doRegisterBean(beanClass, supplier, beanName, null, customizers);
	}

}
