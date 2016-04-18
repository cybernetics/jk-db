package com.jk.db.exception;


public class JKRecordNotFoundException extends JKDaoException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * 
	 */
	public JKRecordNotFoundException() {
		
	}

	/**
	 * 
	 * @param str
	 */
	public JKRecordNotFoundException(String str) {
		super(str);
	}

	/**
	 * 
	 * @param str
	 */
	public JKRecordNotFoundException(Throwable e) {
		super(e);
	}

	/**
	 * 
	 * @param str
	 * @param cause
	 */
	public JKRecordNotFoundException(String str, Throwable cause) {
		super(str, cause);
	}
}
