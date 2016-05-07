package com.jk.db.dataaccess.exception;

public class JKDataAccessManyRowsException extends JKDataAccessException {

	private String queryString;
	private Object[] paramters;

	public JKDataAccessManyRowsException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public JKDataAccessManyRowsException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public JKDataAccessManyRowsException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public JKDataAccessManyRowsException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public JKDataAccessManyRowsException(String queryString, Object[] paramters) {
		this.queryString = queryString;
		this.paramters = paramters;
	}

}
