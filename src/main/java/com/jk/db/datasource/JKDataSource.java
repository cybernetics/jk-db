/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [2016] [Jalal Kiswani]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
	public void close(Connection connection, boolean commit) throws JKDaoException;

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
