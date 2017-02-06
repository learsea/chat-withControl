package com.sibu.chat.node.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.sibu.chat.common.bean.vo.tcp.TcpErrorResult;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.common.exception.BusinessException;
import com.sibu.chat.common.utils.JSONUtils;

/**
 * 控制层切面 1、在控制层添加错误处理 2、记录日志
 * @author caishiyu
 */
@Aspect
@Component
public class TcpControllerAspect {
	private static final Logger logger = Logger.getLogger(TcpControllerAspect.class);

	@Pointcut("execution(* com.sibu.chat.node.controller.tcp.*Controller.*(..))")
	public void controller() {
	}

	/**
	 * 1、在control层方法执行前后记录日志。 2、处理整个control层异常
	 */
	@Around("controller()")
	public Object aroundCtrl(ProceedingJoinPoint joinPoint) throws Throwable {
		String method = joinPoint.getSignature().getName();
		Operation op = (Operation) joinPoint.getArgs()[0];
		logger.info("control begin ------------：" + method);
		try {
			return joinPoint.proceed();
		} catch (BusinessException e) {
			String errMsg = JSONUtils.parse(TcpErrorResult.getResult(op, "客户端错误。原因：" + e.getMessage()));
			logger.warn(errMsg);
			return errMsg;
		} catch (DataAccessException e) {
			e.printStackTrace();
			String errMsg = JSONUtils.parse(TcpErrorResult.getResult(op, "数据库异常。原因：" + e.getMessage()));
			logger.warn(errMsg);
			return errMsg;
		} catch (Exception e) {
			e.printStackTrace();
			String errMsg = JSONUtils.parse(TcpErrorResult.getResult(op, "服务器异常。原因：" + e.getMessage()));
			logger.warn(errMsg);
			return errMsg;
		} finally {
			logger.info("control end ------------：" + method + "\n");
		}
	}
}
