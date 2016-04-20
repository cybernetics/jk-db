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
