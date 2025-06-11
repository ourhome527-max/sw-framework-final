package com.swfinal.exception;

public class LoginException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final String REPL_CD;
	private final String REPL_MSG;
	private final String REPL_PAGE_MSG;

	public LoginException(String replCd, String replMsg, String replPageMsg) {
		super(replMsg);
		this.REPL_CD = replCd;
		this.REPL_MSG = replMsg;
		this.REPL_PAGE_MSG = replPageMsg;
	}

	public String getReplCd() {
		return REPL_CD;
	}

	public String getReplMsg() {
		return REPL_MSG;
	}

	public String getReplPageMsg() {
		return REPL_PAGE_MSG;
	}
}