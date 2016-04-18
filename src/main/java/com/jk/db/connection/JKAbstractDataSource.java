package com.jk.db.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import com.jk.db.JKSession;
import com.jk.db.exception.JKDaoException;
import com.jk.db.exception.JKException;
import static com.jk.db.JKDbConstants.*;

public abstract class JKAbstractDataSource implements JKDataSource {
	protected Logger logger = Logger.getLogger(getClass().getName());

	protected static int connectionsCount;
	private JKSession parentSession;
	private Connection queryConnection;
	private boolean driverClassLoaded;

	/**
	 * 
	 */
	public JKAbstractDataSource() {
	}

	/**
	 * 
	 */
	public void close(Connection con) {
		try {
			if (con == null || con.isClosed()) {
				return;
			}
			if (con == queryConnection) {
				logger.info("connection is not closed because it is query connection");
			} else {
				logger.info("closing connection : Current connection " + (--connectionsCount));
				con.close();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 */
	public void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {

			}
		}
	}

	/**
	 * 
	 */
	public void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 
	 */
	public Connection getConnection() {
		if (!driverClassLoaded) {
			loadDriverClass();
			driverClassLoaded = true;
		}
		logger.info("Creating connection , current opened connections : " + (++connectionsCount));
		try {
			return connect();
		} catch (SQLException e) {
			throw new JKDaoException(e);
		}
	}

	/**
	 * 
	 */
	private void loadDriverClass() {
		try {
			Class.forName(getDriverName());
		} catch (ClassNotFoundException e) {
			throw new JKException(e);
		}
	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	protected Connection connect() throws SQLException {
		return DriverManager.getConnection(getDatabaseUrl(), getUsername(), getPassword());
	}

	/**
	 * 
	 */
	public void close(Connection connection, boolean commit) throws JKDaoException {
		try {
			if (commit) {
				logger.info("commit transaction");
				connection.commit();
			} else {
				logger.info("rollback transaction");
				connection.rollback();
			}
			close(connection);
		} catch (SQLException e) {
			throw new JKDaoException(e);
		}
	}

	/**
	 * 
	 */
	public JKSession createSession() {
		logger.info("Create database session(transaction)");
		if (parentSession == null || parentSession.isClosed()) {
			parentSession = new JKSession(this);
			return parentSession;
		}
		return new JKSession(parentSession);
	}

	/**
	 * 
	 */
	@Override
	public Connection getQueryConnection() throws JKDaoException {
		logger.info("get query connection");
		if (queryConnection == null) {
			logger.info("queryConnection is not available , creating new one");
			queryConnection = getConnection();
		}
		return queryConnection;
	}

}