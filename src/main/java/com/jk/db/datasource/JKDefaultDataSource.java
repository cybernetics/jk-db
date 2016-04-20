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

import java.util.Properties;

import com.jk.db.JKDbConstants;

/**
 * The Class JKDefaultDataSource.
 *
 * @author Jalal Kiswani
 */
public class JKDefaultDataSource extends JKAbstractDataSource {

	/** The config. */
	private final Properties config;

	/** The driver name. */
	private String driverName;

	/** The db url. */
	private String dbUrl;

	/** The user name. */
	private String userName;

	/** The password. */
	private String password;

	/**
	 * Instantiates a new JK default data source.
	 *
	 * @param config
	 *            the config
	 */
	public JKDefaultDataSource(final Properties config) {
		this.config = config;
		init();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#getDatabaseUrl()
	 */
	@Override
	public String getDatabaseUrl() {
		return this.dbUrl;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#getDriverName()
	 */
	@Override
	public String getDriverName() {
		return this.driverName;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#getPassword()
	 */
	@Override
	public String getPassword() {
		return this.password;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#getProperties()
	 */
	@Override
	public Properties getProperties() {
		return this.config;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#getProperty(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String getProperty(final String name, final String defaultValue) {
		return this.config.getProperty(name, defaultValue);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#getTestQuery()
	 */
	@Override
	public String getTestQuery() {
		return this.config.getProperty("DB_TEST_QUERY", "SELECT version()");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#getUsername()
	 */
	@Override
	public String getUsername() {
		return this.userName;
	}

	/**
	 * Inits the.
	 */
	protected void init() {
		this.driverName = this.config.getProperty(JKDbConstants.PROPERTY_DRIVER_NAME, JKDbConstants.DEFAULT_DB_DRIVER);
		this.dbUrl = this.config.getProperty(JKDbConstants.PROPERTY_DB_URL, JKDbConstants.DEFAULT_DB_URL);
		this.userName = this.config.getProperty(JKDbConstants.PROPERTY_DB_USER, JKDbConstants.DEFAULT_DB_USER);
		this.password = this.config.getProperty(JKDbConstants.PROPERTY_DB_PASSWORD, JKDbConstants.DEFAULT_DB_PASSWORD);
	}
}
