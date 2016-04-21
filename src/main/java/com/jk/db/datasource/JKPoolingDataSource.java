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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;

import com.jk.db.JKDbConstants;
import com.jk.db.exception.JKDaoException;
import com.jk.util.CollectionsUtil;
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
		this.logger.info("Constructing database pool with settings :".concat(CollectionsUtil.toString(getProperties())));
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
