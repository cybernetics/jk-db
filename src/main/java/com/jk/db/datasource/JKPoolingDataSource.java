package com.jk.db.datasource;

import java.io.IOException;
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
 * 
 * @author Jalal
 *
 */
public class JKPoolingDataSource extends JKDefaultDataSource {

	private BasicDataSource datasource;

	/**
	 * 
	 * @param prop
	 */
	public JKPoolingDataSource(Properties prop) {
		super(prop);
	}

	/**
	 * 
	 */
	protected void init() {
		super.init();
		logger.info("Constructing database pool with settings :".concat(StringUtil.toString(getProperties())));
		datasource = new BasicDataSource();
		datasource.setDriverClassName(super.getDriverName());
		datasource.setUrl(getDatabaseUrl());
		datasource.setUsername(getUsername());
		datasource.setPassword(getPassword());
		datasource.setInitialSize(getInitialPoolSize());
		datasource.setMaxTotal(getMaxPoolSize());
	}

	/**
	 * 
	 * @return
	 */
	private int getInitialPoolSize() {
		return Integer.parseInt(
				getProperty(JKDbConstants.PROPERTY_INITIAL_POOL_SIZE_KEY, JKDbConstants.DEFAULT_POOL_INITIAL_SIZE));
	}

	/**
	 * 
	 * @return
	 */
	private int getMaxPoolSize() {
		return Integer
				.parseInt(getProperty(JKDbConstants.PROPERTY_MAX_POOL_SIZE_KEY, JKDbConstants.DEFAULT_POOL_MAX_SIZE));
	}

	/**
	 * 
	 */
	@Override
	protected Connection connect() throws SQLException {
		return datasource.getConnection();
	}

	/**
	 * 
	 */
	@Override
	public Connection getQueryConnection() throws JKDaoException {
		Connection queryConnection = super.getQueryConnection();
		try {
			testConnection(queryConnection);
		} catch (SQLException e) {
			queryConnection = null;
			throw new JKDaoException("DATABASE_DOWN_ERROR", e);
		}
		return queryConnection;
	}

	/**
	 * 
	 * @param queryConnection
	 * @throws SQLException
	 */
	private void testConnection(Connection queryConnection) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = queryConnection.prepareStatement(getTestQuery());
			ps.executeQuery();
		} finally {
			close(ps);
		}
	}
}
