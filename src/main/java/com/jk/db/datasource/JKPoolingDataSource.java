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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;

import com.jk.db.JKDbConstants;
import com.jk.db.exception.JKDaoException;
import com.jk.util.StringUtil;

/**
 * The Class JKPoolingDataSource.
 *
 * @author Jalal Kiswani
 */
public class JKPoolingDataSource extends JKDefaultDataSource {

	/** The datasource. */
	private BasicDataSource datasource;

	/**
	 * Instantiates a new JK pooling data source.
	 *
	 * @param prop
	 *            the prop
	 */
	public JKPoolingDataSource(final Properties prop) {
		super(prop);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKAbstractDataSource#connect()
	 */
	@Override
	protected Connection connect() throws SQLException {
		return this.datasource.getConnection();
	}

	/**
	 * Gets the initial pool size.
	 *
	 * @return the initial pool size
	 */
	private int getInitialPoolSize() {
		return Integer.parseInt(
				getProperty(JKDbConstants.PROPERTY_INITIAL_POOL_SIZE_KEY, JKDbConstants.DEFAULT_POOL_INITIAL_SIZE));
	}

	/**
	 * Gets the max pool size.
	 *
	 * @return the max pool size
	 */
	private int getMaxPoolSize() {
		return Integer
				.parseInt(getProperty(JKDbConstants.PROPERTY_MAX_POOL_SIZE_KEY, JKDbConstants.DEFAULT_POOL_MAX_SIZE));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKAbstractDataSource#getQueryConnection()
	 */
	@Override
	public Connection getQueryConnection() throws JKDaoException {
		Connection queryConnection = super.getQueryConnection();
		try {
			testConnection(queryConnection);
		} catch (final SQLException e) {
			queryConnection = null;
			throw new JKDaoException("DATABASE_DOWN_ERROR", e);
		}
		return queryConnection;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDefaultDataSource#init()
	 */
	@Override
	protected void init() {
		super.init();
		this.logger.info("Constructing database pool with settings :".concat(StringUtil.toString(getProperties())));
		this.datasource = new BasicDataSource();
		this.datasource.setDriverClassName(super.getDriverName());
		this.datasource.setUrl(getDatabaseUrl());
		this.datasource.setUsername(getUsername());
		this.datasource.setPassword(getPassword());
		this.datasource.setInitialSize(getInitialPoolSize());
		this.datasource.setMaxTotal(getMaxPoolSize());
	}

	/**
	 * Test connection.
	 *
	 * @param queryConnection
	 *            the query connection
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void testConnection(final Connection queryConnection) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = queryConnection.prepareStatement(getTestQuery());
			ps.executeQuery();
		} finally {
			close(ps);
		}
	}
}
