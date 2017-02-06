package com.sibu.chat.common.exception;

import com.sibu.chat.common.constant.Code;

/**
 * 自定义业务层异常
 * @author caishiyu
 */
public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = -6421035720932214340L;
	private Code errorCode;

	/**
	 * 自定义错误信息
	 */
	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(String message, Code errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public Code getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Code errorCode) {
		this.errorCode = errorCode;
	}
}
