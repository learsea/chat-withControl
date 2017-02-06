package com.sibu.chat.node.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.sibu.chat.common.bean.vo.http.HttpErrorResult;
import com.sibu.chat.common.constant.Code;
import com.sibu.chat.common.exception.BusinessException;

/**
 * 控制层切面,在控制层添加错误处理 
 * @author caishiyu
 */
@Aspect
@Component
public class HttpControllerAspect {
	private static final Logger logger = Logger.getLogger(HttpControllerAspect.class);

	@Pointcut("execution(* com.sibu.chat.node.controller.http.*.*(..))")
	public void controller() {
	}

	/**
	 * 处理整个control层异常
	 */
	@Around("controller()")
	public String aroundCtrl(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			return (String) joinPoint.proceed();
		} catch (BusinessException e) {
			String errMsg = e.getMessage();
			logger.warn(errMsg);
			return HttpErrorResult.getResult(e.getErrorCode(), errMsg).toJSONString();
		} catch (DataAccessException e) {
			String errMsg = e.getMessage();
			logger.warn(errMsg);
			return HttpErrorResult.getResult(Code._401, errMsg).toJSONString();
		} catch (Exception e) {
			String errMsg = e.getMessage();
			logger.warn(errMsg);
			return HttpErrorResult.getResult(Code._400, errMsg).toJSONString();
		}
	}
}
