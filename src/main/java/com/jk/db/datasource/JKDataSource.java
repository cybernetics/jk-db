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
package com.jk.db.datasource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import com.jk.db.exception.JKDaoException;

/**
 * The Interface JKDataSource.
 *
 * @author Jalal Kiswani
 */
public interface JKDataSource {

	/**
	 * Close.
	 *
	 * @param con
	 *            the con
	 */
	public void close(Connection con);

	/**
	 * Close.
	 *
	 * @param connection
	 *            the connection
	 * @param commit
	 *            the commit
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	public void close(Connection connection, boolean commit) ;

	/**
	 * Close.
	 *
	 * @param rs
	 *            the rs
	 */
	public void close(ResultSet rs);

	/**
	 * Close.
	 *
	 * @param stmt
	 *            the stmt
	 */
	public void close(Statement stmt);

	/**
	 * Creates the session.
	 *
	 * @return the JK session
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	public JKSession createSession() throws JKDaoException;

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	public Connection getConnection() throws JKDaoException;

	/**
	 * Gets the database url.
	 *
	 * @return the database url
	 */
	public String getDatabaseUrl();

	/**
	 * Gets the driver name.
	 *
	 * @return the driver name
	 */
	public String getDriverName();

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword();

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public Properties getProperties();

	/**
	 * Gets the property.
	 *
	 * @param property
	 *            the property
	 * @param defaultValue
	 *            the default value
	 * @return the property
	 */
	public String getProperty(String property, String defaultValue);

	/**
	 * Gets the query connection.
	 *
	 * @return the query connection
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	public Connection getQueryConnection() throws JKDaoException;

	/**
	 * Gets the test query.
	 *
	 * @return the test query
	 */
	public String getTestQuery();

	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername();
}
