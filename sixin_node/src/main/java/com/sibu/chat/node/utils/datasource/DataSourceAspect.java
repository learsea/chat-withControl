package com.sibu.chat.node.utils.datasource;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 切面，读取dao中的datasource注解
 * @author caishiyu
 *
 */
@Aspect
@Component
public class DataSourceAspect {

	@Pointcut("execution(* com.sibu.chat.node.dao.*.*(..))")
	public void daoOperation() {
	}

	@Before("daoOperation()")
	public void setDataSourceFlag(JoinPoint point) {
		Object target = point.getTarget();
		String method = point.getSignature().getName();

		// dao层是以接口方式编程
		Class<?>[] classz = target.getClass().getInterfaces();

		Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
		Method m;
		try {
			m = classz[0].getMethod(method, parameterTypes);
			if (m != null && m.isAnnotationPresent(DataSource.class)) {
				DataSource data = m.getAnnotation(DataSource.class);
				// 设置datasource（这里设置的仅仅是一个标志，并不是真正的数据源，选择哪个数据源取决于DynamicDataSource的getDataSource方法是否被调用
				DynamicDataSource.putDataSource(data.value().toString());
			}
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}