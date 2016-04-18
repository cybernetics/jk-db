package com.jk.db.exception;

import java.sql.SQLException;
import java.util.ArrayList;

public class JKDaoException extends JKException {
	ArrayList<JKDaoException> exception = new ArrayList<JKDaoException>();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * 
	 */
	public JKDaoException() {
		super();
	}

	/**
	 * Method 'DaoException'
	 * 
	 * @param message
	 *            String
	 */
	public JKDaoException(String message) {
		super(message);
	}

	/**
	 * Method 'DaoException'
	 * 
	 * @param message
	 *            String
	 * @param throwable
	 *            Throwable
	 */
	public JKDaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public JKDaoException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 * @param e
	 */
	public void add(JKDaoException e) {
		exception.add(e);
	}

}
