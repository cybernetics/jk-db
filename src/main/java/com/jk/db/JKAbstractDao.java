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
package com.jk.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.sql.rowset.CachedRowSet;

import com.jk.db.datasource.JKDataSource;
import com.jk.db.datasource.JKDataSourceFactory;
import com.jk.db.datasource.JKDataSourceUtil;
import com.jk.db.datasource.JKSession;
import com.jk.db.exception.JKDaoException;
import com.jk.db.exception.JKRecordNotFoundException;
import com.jk.util.ConversionUtil;
import com.jk.util.DebugUtil;
import com.jk.util.StringUtil;
import com.sun.rowset.CachedRowSetImpl;

/**
 * The Class JKAbstractDao.
 *
 * @author Jalal Kiswani
 */
public abstract class JKAbstractDao implements JKDataAccessObject {

	/** The cache. */
	// TODO : add support for max cache size
	private static Map<String, Hashtable<Object, Object>> cache = new Hashtable<String, Hashtable<Object, Object>>();

	/** The list cache. */
	private static Map<String, List<? extends Object>> listCache = new Hashtable<>();

	/**
	 * Removes the list cache.
	 *
	 * @param query
	 *            the query
	 */
	public static void removeListCache(final String query) {
		JKAbstractDao.listCache.remove(query);
	}

	/**
	 * Reset cache.
	 */
	public synchronized static void resetCache() {
		JKAbstractDao.cache.clear();
		JKAbstractDao.listCache.clear();
	}

	/** The logger. */
	Logger logger = Logger.getLogger(getClass().getName());

	/** The connection manager. */
	private JKDataSource connectionManager;

	/** The session. */
	private JKSession session;

	/**
	 * Instantiates a new JK abstract dao.
	 */
	public JKAbstractDao() {
	}

	/**
	 * Instantiates a new JK abstract dao.
	 *
	 * @param connectionManager
	 *            the connection manager
	 */
	public JKAbstractDao(final JKDataSource connectionManager) {
		this.connectionManager = connectionManager;
	}

	/**
	 * Instantiates a new JK abstract dao.
	 *
	 * @param session
	 *            the session
	 */
	public JKAbstractDao(final JKSession session) {
		setSession(session);
	}

	/**
	 * Close.
	 *
	 * @param connection
	 *            the connection
	 */
	protected void close(final Connection connection) {
		if (connection == null) {
			return;
		}
		if (this.session == null) {
			this.logger.info("closing connection by the datasource");
			getDataSource().close(connection);
		} else {
			this.logger.info("connection not closed since its part of session (trx)");
			// System.err.println("keeping class connection open");
			// this connection is part of transaction , dont close it , it
			// should be closed by the object
			// who passed the connection to this object
		}
	}

	/**
	 * Close.
	 *
	 * @param ps
	 *            the ps
	 */
	protected void close(final java.sql.PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (final Exception e) {
			}
		}
	}

	/**
	 * Close.
	 *
	 * @param ps
	 *            the ps
	 * @param c
	 *            the c
	 */
	public void close(final PreparedStatement ps, final Connection c) {
		close(ps);
		close(c);
	}

	/**
	 * Close.
	 *
	 * @param rs
	 *            the rs
	 */
	protected void close(final ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (final Exception e) {
			}
		}
	}

	/**
	 * Close.
	 *
	 * @param rs
	 *            the rs
	 * @param ps
	 *            the ps
	 * @param c
	 *            the c
	 */
	public void close(final ResultSet rs, final PreparedStatement ps, final Connection c) {
		close(rs);
		close(ps);
		close(c);
	}

	/**
	 * Close.
	 *
	 * @param ps
	 *            the ps
	 */
	public void close(final Statement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (final SQLException e) {
			}
		}
	}

	/**
	 * Creates the records from sql.
	 *
	 * @param sql
	 *            the sql
	 * @return the list
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	@Override
	public List createRecordsFromSQL(String sql) throws JKDaoException {
		if (JKAbstractDao.listCache.get(sql) == null) {
			Connection con = null;
			ResultSet rs = null;
			PreparedStatement ps = null;
			sql = sql.trim();
			// System.out.println("\n\n\n\nLoading list: " + sql);
			// GeneralUtility.printStackTrace();
			try {
				con = getConnection(true);
				ps = createStatement(sql, con);
				rs = ps.executeQuery();
				final Vector<JKDbIdValue> results = new Vector<JKDbIdValue>();
				final ResultSetMetaData metaData = rs.getMetaData();
				while (rs.next()) {
					final JKDbIdValue combo = new JKDbIdValue();
					combo.setId(rs.getString(1));

					if (metaData.getColumnCount() >= 2) {
						if (rs.getString(2) == null) {
							debug(sql + "\n generating null values");
						} else {
							combo.setValue(rs.getString(2));
						}
					} else {
						debug(sql + "\n generating single column only");
					}
					results.add(combo);
				}
				JKAbstractDao.listCache.put(sql, results);
			} catch (final JKDaoException e) {
				throw e;
			} catch (final SQLException e) {
				throw new JKDaoException(e);
			} finally {
				close(rs, ps, con);
			}
		}
		return JKAbstractDao.listCache.get(sql);
	}

	/**
	 * Creates the statement.
	 *
	 * @param con
	 *            the con
	 * @return the statement
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected Statement createStatement(final Connection con) throws SQLException {
		final Statement createStatement = con.createStatement();
		// if (getDataSource().getDriverName().toLowerCase().contains("mysql"))
		// {
		// createStatement.setFetchSize(Integer.MIN_VALUE);
		// }
		return createStatement;
	}

	/**
	 * Creates the statement.
	 *
	 * @param sql
	 *            the sql
	 * @param con
	 *            the con
	 * @return the prepared statement
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected PreparedStatement createStatement(final String sql, final Connection con) throws SQLException {
		return con.prepareStatement(sql);
	}

	/**
	 * Debug.
	 *
	 * @param string
	 *            the string
	 */
	private void debug(final String string) {
		if (System.getProperty("jk.sql.debug", "false").equals("true")) {
			DebugUtil.printCurrentTime(string);
		}
	}

	/**
	 * Execute output query.
	 *
	 * @param query
	 *            the query
	 * @param fieldSeparator
	 *            the field separator
	 * @param recordsSepartor
	 *            the records separtor
	 * @return the string
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	public String executeOutputQuery(final String query, final String fieldSeparator, final String recordsSepartor)
			throws JKDaoException {
		try {
			final CachedRowSet rs = executeQuery(query);
			final ResultSetMetaData meta = rs.getMetaData();
			final StringBuffer buf = new StringBuffer();
			while (rs.next()) {
				if (buf.length() > 0) {
					buf.append(recordsSepartor);
				}
				for (int i = 0; i < meta.getColumnCount(); i++) {
					buf.append(rs.getObject(i + 1));
					buf.append(fieldSeparator);
				}
			}
			return buf.toString();
		} catch (final SQLException e) {
			throw new JKDaoException(e);
		}
	}

	/**
	 * Execute query.
	 *
	 * @param query
	 *            the query
	 * @return the cached row set
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	@Override
	public CachedRowSet executeQuery(final String query) throws JKDaoException {
		Statement ps = null;
		Connection con = null;
		ResultSet rs = null;
		// System.out.println(query);
		try {
			con = getConnection();
			ps = createStatement(con);
			rs = ps.executeQuery(query);
			final CachedRowSet impl = new CachedRowSetImpl();
			impl.populate(rs);
			return impl;
		} catch (final SQLException ex) {
			throw new JKDaoException(ex.getMessage(), ex);
		} finally {
			close(rs);
			close(con);
		}
	}

	/**
	 * Execute query.
	 *
	 * @param query
	 *            the query
	 * @param fromRowIndex
	 *            the from row index
	 * @param toRowIndex
	 *            the to row index
	 * @return the cached row set
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	public CachedRowSet executeQuery(final String query, final int fromRowIndex, final int toRowIndex)
			throws JKDaoException {
		throw new IllegalStateException("this method should be abstract to support other DBMS other than Oracle");
		// String sql =
		// "select * from ( select a.*, rownum r from ( "+query+") a where
		// rownum <= "
		// + (toRowIndex) + " ) where r > " + fromRowIndex;
		// return executeQuery(sql);
	}

	/**
	 * Execute update.
	 *
	 * @param sql
	 *            the sql
	 * @return the int
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	@Override
	public int executeUpdate(final String sql) throws JKDaoException {
		Connection connection = null;
		Statement ps = null;
		try {
			connection = getConnection();
			ps = createStatement(connection);
			this.logger.info("exceuting sql : ".concat(sql));
			final int count = ps.executeUpdate(sql);
			this.logger.info("affected rows : " + count);
			// no auto increment fields
			return count;
		} catch (final SQLException e) {
			throw new JKDaoException(e);
		} finally {
			close(ps);
			close(connection);
		}
	}

	/**
	 * Execute update.
	 *
	 * @param updater
	 *            the updater
	 * @return the int
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	@Override
	public int executeUpdate(final Updater updater) throws JKDaoException {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			this.logger.info("get connection");
			connection = getConnection();
			ps = connection.prepareStatement(updater.getUpdateSql(), Statement.RETURN_GENERATED_KEYS);
			updater.setParamters(ps);
			this.logger.info("Exectuing statment : ".concat(ps.toString()));
			final int count = ps.executeUpdate();
			this.logger.info("executed succ...");
			if (count == 0) {
				throw new JKRecordNotFoundException("RECORD_NOT_FOUND");
			}
			return getGeneratedKeys(ps);
			// no auto increment fields
		} catch (final SQLException e) {
			throw new JKDaoException(e);
		} finally {
			close(ps, connection);
		}
	}

	/**
	 * Execute update.
	 *
	 * @param updater
	 *            the updater
	 * @param ignoreRecordNotFoundException
	 *            the ignore record not found exception
	 * @return the int
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	public int executeUpdate(final Updater updater, final boolean ignoreRecordNotFoundException) throws JKDaoException {
		try {
			return executeUpdate(updater);
		} catch (final JKRecordNotFoundException e) {
			if (!ignoreRecordNotFoundException) {
				throw e;
			}
			return 0;
		}
	}

	/**
	 * Exeute query as array.
	 *
	 * @param query
	 *            the query
	 * @return the object[]
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	@Override
	public Object[] exeuteQueryAsArray(final String query) throws JKDaoException {
		try {
			final CachedRowSet rs = executeQuery(query);
			final ResultSetMetaData meta = rs.getMetaData();
			final ArrayList<Object[]> rows = new ArrayList<Object[]>();
			while (rs.next()) {
				final Object[] row = new Object[meta.getColumnCount()];
				for (int i = 0; i < meta.getColumnCount(); i++) {
					row[i] = rs.getObject(i + 1);
				}
				rows.add(row);
			}
			return rows.toArray();
		} catch (final SQLException e) {
			throw new JKDaoException(e);
		}
	}

	/**
	 * Exeute single output query.
	 *
	 * @param query
	 *            the query
	 * @return the object
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	@Override
	public Object exeuteSingleOutputQuery(final String query) throws JKDaoException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = getConnection(true);
			ps = createStatement(query, con);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getObject(1);
			}
			throw new JKRecordNotFoundException("No value available for query :" + query);
		} catch (final SQLException ex) {
			throw new JKDaoException(ex.getMessage() + "\n" + query, ex);
		} finally {
			close(rs, ps, con);
		}
	}

	/**
	 * Exeute single output query.
	 *
	 * @param query
	 *            the query
	 * @param params
	 *            the params
	 * @return the object
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	@Override
	public Object exeuteSingleOutputQuery(final String query, final Object... params) throws JKDaoException {
		return exeuteSingleOutputQuery(StringUtil.compile(query, params));
	}

	/**
	 * Find record.
	 *
	 * @param finder
	 *            the finder
	 * @return the object
	 * @throws JKRecordNotFoundException
	 *             the JK record not found exception
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	@Override
	public Object findRecord(final Finder finder) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection(true);
			ps = prepareStatement(connection, finder.getFinderSql());
			finder.setParamters(ps);
			rs = ps.executeQuery();
			if (rs.next()) {
				return finder.populate(rs);
			}
			throw new JKRecordNotFoundException("REOCRD_NOT_FOUND");
		} catch (final SQLException e) {
			throw new JKDaoException(
					"Error while executing the following select statement : \n" + finder.getFinderSql(), e);
		} finally {
			close(rs);
			close(ps);
			close(connection);
		}
	}

	/**
	 * Find record.
	 *
	 * @param finder
	 *            the finder
	 * @param tableName
	 *            the table name
	 * @param recordId
	 *            the record id
	 * @return the object
	 * @throws JKRecordNotFoundException
	 *             the JK record not found exception
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	public Object findRecord(final Finder finder, final String tableName, final Object recordId)
			throws JKRecordNotFoundException, JKDaoException {
		if (JKAbstractDao.cache.get(tableName) == null) {
			JKAbstractDao.cache.put(tableName, new Hashtable<Object, Object>());
		}
		try {
			final Hashtable<Object, Object> tableCache = JKAbstractDao.cache.get(tableName);
			if (tableCache.get(recordId) == null) {
				// System.out.println("loading record "+tableName);
				final Object record = findRecord(finder);
				// if the size exceeded the max cache size , don't cache
				if (tableCache.size() > Integer.parseInt(getDataSource().getProperty("max-cache-size", "1000"))) {
					return record;
				}
				tableCache.put(recordId, record);
			} else {
				// System.out.println("return "+tableName+" info from cache");
			}
			return tableCache.get(recordId);
		} catch (final JKRecordNotFoundException e) {
			throw new JKRecordNotFoundException(
					"RECORD_NOT_FOUND_FOR_TABLE (" + tableName + ") for ID (" + recordId + ")");
		}
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	protected Connection getConnection() {
		return getConnection(false);
	}

	/**
	 * Gets the connection.
	 *
	 * @param query
	 *            the query
	 * @return the connection
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	protected Connection getConnection(final boolean query) {
		this.logger.info("get connection with query flag : " + query);
		if (this.session != null && !this.session.isClosed()) {
			return this.session.getConnection();
		}
		if (query) {
			return getDataSource().getQueryConnection();
		} else {
			return getDataSource().getConnection();
		}
	}

	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	protected JKDataSource getDataSource() {
		if (this.connectionManager != null) {
			return this.connectionManager;
		}
		return JKDataSourceFactory.getDefaultDataSource();
	}

	/**
	 * Gets the generated keys.
	 *
	 * @param ps
	 *            the ps
	 * @return the generated keys
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected int getGeneratedKeys(final PreparedStatement ps) throws SQLException {
		if (JKDataSourceUtil.isOracle(getDataSource())) {
			throw new IllegalStateException("This method is not avilable on oracle db");
		}
		final ResultSet idRs = ps.getGeneratedKeys();
		if (idRs.next()) {
			final Object object = idRs.getObject(1);
			return ConversionUtil.toInteger(object);
		}
		return 0;
	}

	/**
	 * Gets the next id.
	 *
	 * @param connectoin
	 *            the connectoin
	 * @param tableName
	 *            the table name
	 * @param fieldName
	 *            the field name
	 * @return the next id
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	public int getNextId(final Connection connectoin, final String tableName, final String fieldName)
			throws JKDaoException {
		return getNextId(connectoin, tableName, fieldName, null);
	}

	/**
	 * Gets the next id.
	 *
	 * @param con
	 *            the con
	 * @param tableName
	 *            the table name
	 * @param fieldName
	 *            the field name
	 * @param condition
	 *            the condition
	 * @return the next id
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	public int getNextId(final Connection con, final String tableName, final String fieldName, final String condition)
			throws JKDaoException {
		PreparedStatement ps = null;
		int ser;
		try {
			String sql = "SELECT MAX(" + fieldName + ")+1 FROM " + tableName;
			if (condition != null && !condition.trim().equals("")) {
				sql += " WHERE " + condition;
			}
			ps = createStatement(sql, con);

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ser = rs.getInt(1);
				if (rs.wasNull()) {
					return 1;
				}
			} else {
				ser = 1;
			}
			return ser;
		} catch (final SQLException e) {
			throw new JKDaoException(e);
		} finally {
			close(ps);
		}
	}

	/**
	 * Gets the next id.
	 *
	 * @param tableName
	 *            the table name
	 * @param fieldName
	 *            the field name
	 * @return the next id
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	@Override
	public int getNextId(final String tableName, final String fieldName) throws JKDaoException {
		final Connection connection = getConnection(true);
		try {
			return getNextId(connection, tableName, fieldName);
		} finally {
			close(connection);
		}
	}

	/**
	 * Gets the next id.
	 *
	 * @param tableName
	 *            the table name
	 * @param fieldName
	 *            the field name
	 * @param condition
	 *            the condition
	 * @return the next id
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	public int getNextId(final String tableName, final String fieldName, final String condition) throws JKDaoException {
		final Connection connection = getConnection(true);
		try {
			return getNextId(connection, tableName, fieldName, condition);
		} finally {
			close(connection);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.JKDataAccessObject#getRowsCount(java.lang.String)
	 */
	@Override
	public int getRowsCount(final String query) throws NumberFormatException, JKDaoException {
		final String sql = "SELECT COUNT(*) FROM (" + query + ") ";
		// System.out.println(sql);
		return new Integer(exeuteSingleOutputQuery(sql).toString());
	}

	/**
	 * Gets the system date.
	 *
	 * @return the system date
	 * @throws JKRecordNotFoundException
	 *             the JK record not found exception
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	public Date getSystemDate() throws JKRecordNotFoundException, JKDaoException {
		final Finder finder = new Finder() {
			@Override
			public String getFinderSql() {
				return "SELECT SYSDATE()";
			}

			@Override
			public Date populate(final ResultSet rs) throws SQLException, JKRecordNotFoundException, JKDaoException {
				return rs.getTimestamp(1);
			}

			@Override
			public void setParamters(final PreparedStatement ps) {
			}
		};
		return (Date) findRecord(finder);
	}

	/**
	 * Lst records.
	 *
	 * @param finder
	 *            the finder
	 * @return the list
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	@Override
	public List lstRecords(final Finder finder) throws JKDaoException {
		// System.out.println("Executing : "+finder.getFinderSql());
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection(true);
			ps = prepareStatement(connection, finder.getFinderSql());
			finder.setParamters(ps);
			rs = ps.executeQuery();
			debug(ps.toString().substring(ps.toString().toUpperCase().indexOf("SELECT")));
			final ArrayList list = new ArrayList();
			while (rs.next()) {
				list.add(finder.populate(rs));
			}
			return list;
		} catch (final SQLException e) {
			if (ps != null) {
				debug(ps.toString());
			}
			throw new JKDaoException(e);
		} finally {
			close(rs);
			close(ps);
			close(connection);
		}
	}

	/**
	 * Lst records.
	 *
	 * @param finder
	 *            the finder
	 * @param key
	 *            the key
	 * @return the list
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	public List lstRecords(final Finder finder, final String key) throws JKDaoException {
		if (JKAbstractDao.listCache.get(key) == null) {
			JKAbstractDao.listCache.put(key, lstRecords(finder));
		}
		return JKAbstractDao.listCache.get(key);
	}

	/**
	 * Prepare statement.
	 *
	 * @param connection
	 *            the connection
	 * @param sql
	 *            the sql
	 * @return the prepared statement
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected PreparedStatement prepareStatement(final Connection connection, final String sql) throws SQLException {
		final PreparedStatement prepareStatement = connection.prepareStatement(sql);
		return prepareStatement;
	}

	/**
	 * Prints the record result set.
	 *
	 * @param rs
	 *            the rs
	 */
	/*
	 *
	 */
	public void printRecordResultSet(final ResultSet rs) {
		printRecordResultSet(rs, true);
	}

	/**
	 * Prints the record result set.
	 *
	 * @param rs
	 *            the rs
	 * @param all
	 *            the all
	 */
	public void printRecordResultSet(final ResultSet rs, final boolean all) {
		try {
			final java.sql.ResultSetMetaData meta = rs.getMetaData();
			System.out.println("At print result set");
			while (rs.next()) {
				System.out.println("------------------------------------------------------");
				for (int i = 0; i < meta.getColumnCount(); i++) {
					System.out.print(meta.getColumnName(i + 1) + " = " + rs.getObject(i + 1) + "\t");
				}
				System.out.println();
				if (!all) {
					return;
				}
			}
			System.out.println("///////////////////////");
		} catch (final SQLException e) {
			throw new JKDaoException(e);
		}

	}

	/**
	 * Sets the session.
	 *
	 * @param session
	 *            the new session
	 */
	public void setSession(final JKSession session) {
		this.session = session;
	}

}
