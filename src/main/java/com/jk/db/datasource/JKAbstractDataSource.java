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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import com.jk.db.exception.JKDaoException;
import com.jk.util.exceptions.JKException;

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
	public void close(final Connection connection, final boolean commit) throws JKDaoException {
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