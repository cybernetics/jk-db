package com.jk.db.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import com.jk.db.exception.JKDaoException;

/**
 * Manage all related trx managed in non-trx container
 * 
 * @author jalal
 * 
 */
public class JKSession {
	private Connection connection;
	private boolean commit;
	private boolean rollBackOnly;
	boolean closed;
	private JKSession parentSession;
	private JKDataSource connectionManager;

	/**
	 * 
	 * @param connection
	 * @throws JKDaoException
	 */
	private JKSession(Connection connection) throws JKDaoException {
		this.connection = connection;
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new JKDaoException(e);
		}
	}

	/**
	 * 
	 * @param parentSession
	 */
	public JKSession(JKSession parentSession) {
		this.parentSession = parentSession;
	}

	public JKSession(JKDataSource connectionManager) throws JKDaoException {
		this(connectionManager.getConnection());
		this.connectionManager = connectionManager;
	}

	/**
	 * @return the commit
	 */
	public boolean isCommit() {
		return parentSession != null ? parentSession.isCommit() : commit;
	}

	/**
	 * The commit value could be overriden , but the rollbackOnly , if set to
	 * true ,it cannot be changed
	 * 
	 * @param commit
	 *            the commit to set
	 */
	protected void commit(boolean commit) {
		if (parentSession != null) {
			parentSession.commit(commit);
		} else {
			if (!commit) {
				this.rollBackOnly = true;
			} else {
				this.commit = true;
			}
		}
	}

	/**
	 * 
	 * @param commit
	 * @throws JKDaoException
	 */
	public void close(boolean commit) throws JKDaoException {
		commit(commit);
		close();
	}

	/**
	 * 
	 * @throws JKDaoException
	 */
	public void close() throws JKDaoException {
		if (parentSession != null) {
			// Just return , because this would be called from method that
			// doesn't know it has been
			// called from outer session, so we just wait until the parent
			// session close it self
			return;
		}
		try {
			if (connection == null || connection.isClosed()) {
				throw new IllegalStateException("Invalid call to Session.close on closed session");
			}
			if (isCommit() && !isRollbackOnly()) {
				connection.commit();
			} else {
				connection.rollback();
			}
		} catch (SQLException e) {
			throw new JKDaoException(e);
		} finally {
			// the abstract resource manage will check internally on the
			// nullable and isClosed properties for the connection
			//GeneralUtility.printStackTrace();
			//System.err.println("Closing connection from session");
			connectionManager.close(connection);
			setClosed(true);
		}
	}

	/**
	 * 
	 * @return
	 */
	private boolean isRollbackOnly() {
		return rollBackOnly;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isClosed() {
		return parentSession != null ? parentSession.isClosed() : closed;
	}

	/**
	 * @param closed
	 *            the closed to set
	 */
	private void setClosed(boolean closed) {
		this.closed = closed;
	}

	public JKSession getParentSession() {
		return parentSession;
	}

	public JKDataSource getConnectionManager() {
		JKDataSource manager= parentSession != null ? parentSession.getConnectionManager() : connectionManager;
		if(manager==null){
			throw new IllegalStateException("Connectino manager connot be null");
		}
		return manager;
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return parentSession != null ? parentSession.getConnection() : connection;
	}
}
