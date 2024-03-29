/*
 * Copyright 2002-2016 Jalal Kiswani.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jk.db.dataaccess.exception;

/**
 * The Class JKDaoValidationException.
 *
 * @author Jalal Kiswani
 */
public class JKDataAccessValidationException extends JKDataAccessException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2450150009026093119L;

	/**
	 * Instantiates a new JK dao validation exception.
	 */
	public JKDataAccessValidationException() {
		super();
	}

	/**
	 * Instantiates a new JK dao validation exception.
	 *
	 * @param message
	 *            the message
	 */
	public JKDataAccessValidationException(final String message) {
		super(message);
	}

	/**
	 * Instantiates a new JK dao validation exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public JKDataAccessValidationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new JK dao validation exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public JKDataAccessValidationException(final Throwable cause) {
		super(cause);
	}

}
