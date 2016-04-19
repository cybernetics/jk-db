package com.jk.db.datasource;


import java.io.IOException;
import static com.jk.db.JKDbConstants.*;
import java.util.Properties;

import com.jk.db.JKDbConstants;

/**
 * 
 * @author Jalal
 *
 */
public class JKDefaultDataSource extends JKAbstractDataSource {

	private Properties config;
	private String driverName;
	private String dbUrl;
	private String userName;

	private String password;

	/**
	 * 
	 * @param config
	 * @throws IOException
	 */
	public JKDefaultDataSource(Properties config)  {
		this.config = config;
		init();
	}

	/**
	 * @param config
	 */
	protected void init() {
		driverName = config.getProperty(PROPERTY_DRIVER_NAME, JKDbConstants.DEFAULT_DB_DRIVER);
		dbUrl = config.getProperty(PROPERTY_DB_URL, JKDbConstants.DEFAULT_DB_URL);
		userName = config.getProperty(PROPERTY_DB_USER, JKDbConstants.DEFAULT_DB_USER);
		password = config.getProperty(PROPERTY_DB_PASSWORD, JKDbConstants.DEFAULT_DB_PASSWORD);
	}

	@Override
	public String getDriverName() {
		return driverName;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return userName;
	}

	@Override
	public String getProperty(String name, String defaultValue) {
		return config.getProperty(name, defaultValue);
	}

	@Override
	public String getTestQuery() {
		return config.getProperty("DB_TEST_QUERY", "SELECT version()");
	}

	@Override
	public String getDatabaseUrl() {
		return dbUrl;
	}

	@Override
	public Properties getProperties() {
		return config;
	}
}
