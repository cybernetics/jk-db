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
package com.jk.db.dataaccess;

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

import com.jk.db.dataaccess.exception.JKDaoException;
import com.jk.db.dataaccess.exception.JKRecordNotFoundException;
import com.jk.db.datasource.JKDataSource;
import com.jk.db.datasource.JKDataSourceFactory;
import com.jk.db.datasource.JKDataSourceUtil;
import com.jk.db.datasource.JKSession;
import com.jk.util.ConversionUtil;
import com.sun.rowset.CachedRowSetImpl;

/**
 * The Class JKAbstractDao.
 *
 * @author Jalal Kiswani
 */
public abstract class JKAbstractDao implements JKDataAccessObject {

	/** cache for single objects results */
	private static Map<String, Hashtable<Object, Object>> objectsCache = new Hashtable<String, Hashtable<Object, Object>>();

	/** cache for the lists results. */
	private static Map<String, List<? extends Object>> listsCache = new Hashtable<>();

	/** The logger. */
	Logger logger = Logger.getLogger(getClass().getName());

	/** The connection manager. */
	private JKDataSource dataSource;

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
	 * @param dataSource
	 *            the connection manager
	 */
	public JKAbstractDao(final JKDataSource dataSource) {
		this.dataSource = dataSource;
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
	 * Execute query.
	 *
	 * @param query
	 *            the query
	 * @param params
	 *            the params
	 * @return the cached row set
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	@Override
	public CachedRowSet executeQueryAsCachedRowSet(final String query, final Object... params) {
		Statement ps = null;
		Connection con = null;
		ResultSet rs = null;
		// System.out.println(query);
		try {
			con = getConnection();
			ps = prepareStatement(con, query, params);
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
	 * Creates the records from sql.
	 *
	 * @param sql
	 *            the sql
	 * @param params
	 *            the params
	 * @return the list
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	@Override
	public List<JKDbIdValue> executeQueryAsIdValue(final String sql, final Object... params) {
		if (JKAbstractDao.listsCache.get(sql) == null) {
			Connection con = null;
			ResultSet rs = null;
			PreparedStatement ps = null;
			try {
				con = getConnection(true);
				ps = prepareStatement(con, sql, params);
				rs = ps.executeQuery();
				final Vector<JKDbIdValue> results = new Vector<JKDbIdValue>();
				final ResultSetMetaData metaData = rs.getMetaData();
				while (rs.next()) {
					final JKDbIdValue combo = new JKDbIdValue();
					combo.setId(rs.getString(1));

					if (metaData.getColumnCount() >= 2) {
						if (rs.getString(2) == null) {
							this.logger.fine(sql.concat(" generating null values"));
						} else {
							combo.setValue(rs.getString(2));
						}
					} else {
						this.logger.fine(sql.concat(" generating single column only"));
					}
					results.add(combo);
				}
				JKAbstractDao.listsCache.put(sql, results);
			} catch (final JKDaoException e) {
				throw e;
			} catch (final SQLException e) {
				throw new JKDaoException(e);
			} finally {
				close(rs, ps, con);
			}
		}
		return (List<JKDbIdValue>) JKAbstractDao.listsCache.get(sql);
	}

	/**
	 * Execute query as string.
	 *
	 * @param query
	 *            the query
	 * @return the string
	 */
	public String executeQueryAsString(final String query) {
		return executeQueryAsString(query, ",", System.getProperty("line.separator"));
	}

	/**
	 * Execute query as string.
	 *
	 * @param query
	 *            the query
	 * @param fieldSeparator
	 *            the field separator
	 * @param recordsSepartor
	 *            the records separtor
	 * @return the string
	 */
	public String executeQueryAsString(final String query, final String fieldSeparator, final String recordsSepartor) {
		try {
			final CachedRowSet rs = executeQueryAsCachedRowSet(query);
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
	 * Execute update.
	 *
	 * @param updater
	 *            the updater
	 * @return the int
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	@Override
	public int executeUpdate(final JKUpdater updater) throws JKDaoException {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			this.logger.info("get connection");
			connection = getConnection();
			ps = connection.prepareStatement(updater.getQuery(), Statement.RETURN_GENERATED_KEYS);
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
	public int executeUpdate(final JKUpdater updater, final boolean ignoreRecordNotFoundException)
			throws JKDaoException {
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
	 * Execute update.
	 *
	 * @param sql
	 *            the sql
	 * @param params
	 *            the params
	 * @return the int
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	@Override
	public int executeUpdate(final String sql, final Object... params) throws JKDaoException {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = getConnection();
			ps = prepareStatement(connection, sql, params);
			this.logger.info("exceuting sql : ".concat(sql));
			final int count = ps.executeUpdate();
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
			final CachedRowSet rs = executeQueryAsCachedRowSet(query);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jk.db.dataaccess.JKDataAccessObject#exeuteQueryAsList(java.lang.
	 * String, java.lang.Object[])
	 */
	@Override
	public List exeuteQueryAsList(final String query, final Object... params) throws JKDaoException {
		try {
			final CachedRowSet rs = executeQueryAsCachedRowSet(query);
			final ResultSetMetaData meta = rs.getMetaData();
			final List<List<Object>> rows = new Vector<>();
			while (rs.next()) {
				final List<Object> row = new Vector<>();
				for (int i = 0; i < meta.getColumnCount(); i++) {
					row.add(rs.getObject(i + 1));
				}
				rows.add(row);
			}
			return rows;
		} catch (final SQLException e) {
			throw new JKDaoException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jk.db.dataaccess.JKDataAccessObject#exeuteSingleOutputQuery(java.lang
	 * .String, java.lang.Object[])
	 */
	@Override
	public Object exeuteSingleOutputQuery(final String query, final Object... params) throws JKDaoException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = getConnection(true);
			ps = prepareStatement(con, query, params);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getObject(1);
			}
			throw new JKRecordNotFoundException("No value available for query :".concat(query));
		} catch (final SQLException ex) {
			throw new JKDaoException(ex.getMessage().concat(",".concat(query)), ex);
		} finally {
			close(rs, ps, con);
		}
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
	public Object findRecord(final JKFinder finder) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection(true);
			ps = prepareStatement(connection, finder.getQuery());
			finder.setParamters(ps);
			rs = ps.executeQuery();
			if (rs.next()) {
				return finder.populate(rs);
			}
			throw new JKRecordNotFoundException("REOCRD_NOT_FOUND");
		} catch (final SQLException e) {
			throw new JKDaoException(
					"Error while executing the following select statement : \n".concat(finder.getQuery()), e);
		} finally {
			close(rs, ps, connection);
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
	public Object findRecord(final JKFinder finder, final String tableName, final Object recordId)
			throws JKRecordNotFoundException, JKDaoException {
		if (JKAbstractDao.objectsCache.get(tableName) == null) {
			JKAbstractDao.objectsCache.put(tableName, new Hashtable<Object, Object>());
		}
		try {
			final Hashtable<Object, Object> tableCache = JKAbstractDao.objectsCache.get(tableName);
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
					"RECORD_NOT_FOUND_FOR_TABLE (".concat(tableName).concat(") for ID (" + recordId + ")"));
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
		if (this.dataSource != null) {
			return this.dataSource;
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
	public int getNextId(final Connection con, final String tableName, final String fieldName, final String condition) {
		PreparedStatement ps = null;
		int ser;
		try {
			String sql = "SELECT MAX(".concat(fieldName.concat(")+1 FROM ".concat(tableName)));
			if (condition != null && !condition.trim().equals("")) {
				sql += " WHERE " + condition;
			}
			ps = prepareStatement(con, sql);

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
		return (Date) exeuteSingleOutputQuery("SELECT SYSDATE()");
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
	public List lstRecords(final JKFinder finder) throws JKDaoException {
		// System.out.println("Executing : "+finder.getFinderSql());
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection(true);
			ps = prepareStatement(connection, finder.getQuery());
			finder.setParamters(ps);
			rs = ps.executeQuery();
			this.logger.fine(ps.toString().substring(ps.toString().toUpperCase().indexOf("SELECT")));
			final List list = new ArrayList();
			while (rs.next()) {
				list.add(finder.populate(rs));
			}
			return list;
		} catch (final SQLException e) {
			if (ps != null) {
				this.logger.severe(ps.toString());
			}
			throw new JKDaoException(e);
		} finally {
			close(rs, ps, connection);
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
	public List lstRecords(final JKFinder finder, final String key) throws JKDaoException {
		if (JKAbstractDao.listsCache.get(key) == null) {
			JKAbstractDao.listsCache.put(key, lstRecords(finder));
		}
		return JKAbstractDao.listsCache.get(key);
	}

	/**
	 * Prepare statement.
	 *
	 * @param connection
	 *            the connection
	 * @param sql
	 *            the sql
	 * @param params
	 *            the params
	 * @return the prepared statement
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected PreparedStatement prepareStatement(final Connection connection, final String sql, final Object... params)
			throws SQLException {
		final PreparedStatement prepareStatement = connection.prepareStatement(sql);
		for (int i = 0; i < params.length; i++) {
			prepareStatement.setObject(i + 1, params[i]);
		}
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
	protected void printRecordResultSet(final ResultSet rs) {
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
	protected void printRecordResultSet(final ResultSet rs, final boolean all) {
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

	/**
	 * Removes the list cache.
	 *
	 * @param query
	 *            the query
	 */
	public static void removeListCache(final String query) {
		JKAbstractDao.listsCache.remove(query);
	}

	/**
	 * Reset cache.
	 */
	public synchronized static void resetCache() {
		JKAbstractDao.objectsCache.clear();
		JKAbstractDao.listsCache.clear();
	}
}
