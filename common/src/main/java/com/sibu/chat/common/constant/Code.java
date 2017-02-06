package com.sibu.chat.common.constant;

public enum Code {
	// 1开头:成功，2开头:用户错误，3开头:客户端编码造成的参数错误，4开头:服务器错误
	/**
	 * 成功
	 */
	_100,

	/**
	 * 密码错误
	 */
	_210,

	/**
	 * 已经注册过
	 */
	_211,

	/**
	 *  客户端参数错误，该错误只能通过修改客户端代码解决
	 */
	_300,

	/**
	 *服务端处理错误 ，该错误只能通过修改服务端代码解决
	 */
	_400,

	/**
	 * 数据库错误，该错误一般需要修改服务端代码，或者更改数据库
	 */
	_401;

	private int code;

	private Code() {
		this.code = Integer.valueOf(this.toString().substring(1));
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
