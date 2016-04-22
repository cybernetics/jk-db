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
import java.sql.SQLException;

import com.jk.db.dataaccess.exception.JKDataAccessException;

/**
 * Manage all related trx managed in non-trx container.
 *
 * @author Jalal Kiswani
 */
public class JKSession {

	/** The connection. */
	private Connection connection;

	/** The commit. */
	private boolean commit;

	/** The roll back only. */
	private boolean rollBackOnly;

	/** The closed. */
	boolean closed;

	/** The parent session. */
	private JKSession parentSession;

	/** The connection manager. */
	private JKDataSource connectionManager;

	/**
	 * Instantiates a new JK session.
	 *
	 * @param connection
	 *            the connection
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	private JKSession(final Connection connection) throws JKDataAccessException {
		this.connection = connection;
		try {
			connection.setAutoCommit(false);
		} catch (final SQLException e) {
			throw new JKDataAccessException(e);
		}
	}

	/**
	 * Instantiates a new JK session.
	 *
	 * @param connectionManager
	 *            the connection manager
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	public JKSession(final JKDataSource connectionManager) throws JKDataAccessException {
		this(connectionManager.getConnection());
		this.connectionManager = connectionManager;
	}

	/**
	 * Instantiates a new JK session.
	 *
	 * @param parentSession
	 *            the parent session
	 */
	public JKSession(final JKSession parentSession) {
		this.parentSession = parentSession;
	}

	/**
	 * Close.
	 *
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	public void close() throws JKDataAccessException {
		if (this.parentSession != null) {
			// Just return , because this would be called from method that
			// doesn't know it has been
			// called from outer session, so we just wait until the parent
			// session close it self
			return;
		}
		try {
			if (this.connection == null || this.connection.isClosed()) {
				throw new IllegalStateException("Invalid call to Session.close on closed session");
			}
			if (isCommit() && !isRollbackOnly()) {
				this.connection.commit();
			} else {
				this.connection.rollback();
			}
		} catch (final SQLException e) {
			throw new JKDataAccessException(e);
		} finally {
			// the abstract resource manage will check internally on the
			// nullable and isClosed properties for the connection
			// GeneralUtility.printStackTrace();
			// System.err.println("Closing connection from session");
			this.connectionManager.close(this.connection);
			setClosed(true);
		}
	}

	/**
	 * Close.
	 *
	 * @param commit
	 *            the commit
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	public void close(final boolean commit) throws JKDataAccessException {
		commit(commit);
		close();
	}

	/**
	 * The commit value could be overriden , but the rollbackOnly , if set to
	 * true ,it cannot be changed.
	 *
	 * @param commit
	 *            the commit to set
	 */
	protected void commit(final boolean commit) {
		if (this.parentSession != null) {
			this.parentSession.commit(commit);
		} else {
			if (!commit) {
				this.rollBackOnly = true;
			} else {
				this.commit = true;
			}
		}
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public Connection getConnection() {
		return this.parentSession != null ? this.parentSession.getConnection() : this.connection;
	}

	/**
	 * Gets the connection manager.
	 *
	 * @return the connection manager
	 */
	public JKDataSource getConnectionManager() {
		final JKDataSource manager = this.parentSession != null ? this.parentSession.getConnectionManager()
				: this.connectionManager;
		if (manager == null) {
			throw new IllegalStateException("Connectino manager connot be null");
		}
		return manager;
	}

	/**
	 * Gets the parent session.
	 *
	 * @return the parent session
	 */
	public JKSession getParentSession() {
		return this.parentSession;
	}

	/**
	 * Checks if is closed.
	 *
	 * @return true, if is closed
	 */
	public boolean isClosed() {
		return this.parentSession != null ? this.parentSession.isClosed() : this.closed;
	}

	/**
	 * Checks if is commit.
	 *
	 * @return the commit
	 */
	public boolean isCommit() {
		return this.parentSession != null ? this.parentSession.isCommit() : this.commit;
	}

	/**
	 * Checks if is rollback only.
	 *
	 * @return true, if is rollback only
	 */
	private boolean isRollbackOnly() {
		return this.rollBackOnly;
	}

	/**
	 * Sets the closed.
	 *
	 * @param closed
	 *            the closed to set
	 */
	private void setClosed(final boolean closed) {
		this.closed = closed;
	}
}
