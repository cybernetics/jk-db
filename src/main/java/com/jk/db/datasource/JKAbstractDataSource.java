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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import com.jk.db.dataaccess.exception.JKDaoException;
import com.jk.exceptions.JKException;

/**
 * The Class JKAbstractDataSource.
 *
 * @author Jalal Kiswani
 */
public abstract class JKAbstractDataSource implements JKDataSource {

	/** The connections count. */
	protected static int connectionsCount;

	/** The logger. */
	protected Logger logger = Logger.getLogger(getClass().getName());

	/** The parent session. */
	private JKSession parentSession;

	/** The query connection. */
	private Connection queryConnection;

	/** The driver class loaded. */
	private boolean driverClassLoaded;

	/**
	 * Instantiates a new JK abstract data source.
	 */
	public JKAbstractDataSource() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#close(java.sql.Connection)
	 */
	@Override
	public void close(final Connection con) {
		try {
			if (con == null || con.isClosed()) {
				return;
			}
			if (con == this.queryConnection) {
				this.logger.info("connection is not closed because it is query connection");
			} else {
				this.logger.info("closing connection : Current connection " + --JKAbstractDataSource.connectionsCount);
				con.close();
			}
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#close(java.sql.Connection,
	 * boolean)
	 */
	@Override
	public void close(final Connection connection, final boolean commit)  {
		try {
			if (commit) {
				this.logger.info("commit transaction");
				connection.commit();
			} else {
				this.logger.info("rollback transaction");
				connection.rollback();
			}
			close(connection);
		} catch (final SQLException e) {
			throw new JKDaoException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#close(java.sql.ResultSet)
	 */
	@Override
	public void close(final ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (final Exception e) {
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#close(java.sql.Statement)
	 */
	@Override
	public void close(final Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (final Exception e) {

			}
		}
	}

	/**
	 * Connect.
	 *
	 * @return the connection
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected Connection connect() throws SQLException {
		return DriverManager.getConnection(getDatabaseUrl(), getUsername(), getPassword());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#createSession()
	 */
	@Override
	public JKSession createSession() {
		this.logger.info("Create database session(transaction)");
		if (this.parentSession == null || this.parentSession.isClosed()) {
			this.parentSession = new JKSession(this);
			return this.parentSession;
		}
		return new JKSession(this.parentSession);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#getConnection()
	 */
	@Override
	public Connection getConnection() {
		if (!this.driverClassLoaded) {
			loadDriverClass();
			this.driverClassLoaded = true;
		}
		this.logger.info(
				"Creating connection , current opened connections : " + (++JKAbstractDataSource.connectionsCount));
		try {
			return connect();
		} catch (final SQLException e) {
			throw new JKDaoException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#getQueryConnection()
	 */
	@Override
	public Connection getQueryConnection() throws JKDaoException {
		this.logger.info("get query connection");
		if (this.queryConnection == null) {
			this.logger.info("queryConnection is not available , creating new one");
			this.queryConnection = getConnection();
		}
		return this.queryConnection;
	}

	/**
	 * Load driver class.
	 */
	private void loadDriverClass() {
		try {
			Class.forName(getDriverName());
		} catch (final ClassNotFoundException e) {
			throw new JKException(e);
		}
	}

}