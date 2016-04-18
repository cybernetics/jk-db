package com.jk.db.exception;

public class JKException extends RuntimeException {

	public JKException() {
		super();
	}

	public JKException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public JKException(String message, Throwable cause) {
		super(message, cause);
	}

	public JKException(String message) {
		super(message);
	}

	public JKException(Throwable cause) {
		super(cause);
	}

}
