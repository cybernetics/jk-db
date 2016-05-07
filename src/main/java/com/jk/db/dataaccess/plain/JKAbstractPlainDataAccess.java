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
package com.jk.db.dataaccess.plain;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.sql.rowset.CachedRowSet;

import com.jk.db.dataaccess.exception.JKDataAccessException;
import com.jk.db.dataaccess.exception.JKRecordNotFoundException;
import com.jk.db.datasource.JKDataSource;
import com.jk.db.datasource.JKDataSourceFactory;
import com.jk.db.datasource.JKSession;
import com.jk.util.ConversionUtil;
import com.jk.util.ObjectUtil;
import com.sun.rowset.CachedRowSetImpl;

/**
 * The Class JKAbstractDao.
 *
 * @author Jalal Kiswani
 */
public abstract class JKAbstractPlainDataAccess implements JKPlainDataAccess {
	/** cache for single objects results */
	private static Map<String, Hashtable<Object, Object>> objectsCache = new Hashtable<String, Hashtable<Object, Object>>();

	/** cache for the lists results. */
	private static Map<String, List<? extends Object>> listsCache = new Hashtable<>();

	/** The logger. */
	Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Instantiates a new JK abstract dao.
	 */
	public JKAbstractPlainDataAccess() {
	}

	protected void close(final Connection connection) {
		this.logger.info("closing connection");
		if (connection == null) {
			return;
		}
		getDataSource().close(connection);
	}

	protected void close(final java.sql.PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (final Exception e) {
			}
		}
	}

	protected void close(final PreparedStatement ps, final Connection c) {
		close(ps);
		close(c);
	}

	protected void close(final ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (final Exception e) {
			}
		}
	}

	protected void close(final ResultSet rs, final PreparedStatement ps, final Connection c) {
		close(rs);
		close(ps);
		close(c);
	}

	protected void close(final Statement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (final SQLException e) {
			}
		}
	}

	@Override
	public CachedRowSet executeQueryAsCachedRowSet(final String query, final Object... params) {
		logger.info(String.format("executeQueryAsCachedRowSet , Query(%s) Params(%s)", query, Arrays.toString(params)));
		PreparedStatement ps = null;
		Connection con = null;
		ResultSet rs = null;
		try {
			con = getConnection();
			ps = prepareQueryStatement(con, query, params);
			rs = ps.executeQuery();
			final CachedRowSet impl = new CachedRowSetImpl();
			impl.populate(rs);
			return impl;
		} catch (final SQLException ex) {
			throw new JKDataAccessException(ex.getMessage(), ex);
		} finally {
			close(rs);
			close(con);
		}
	}

	@Override
	public List<JKDbIdValue> executeQueryAsIdValue(final String query, final Object... params) {
		logger.info(String.format("executeQueryAsIdValue , Query(%s) Params(%s)", query, Arrays.toString(params)));
		if (JKAbstractPlainDataAccess.listsCache.get(query) == null) {
			Connection con = null;
			ResultSet rs = null;
			PreparedStatement ps = null;
			try {
				con = getConnection(true);
				ps = prepareQueryStatement(con, query, params);
				rs = ps.executeQuery();
				final Vector<JKDbIdValue> results = new Vector<JKDbIdValue>();
				final ResultSetMetaData metaData = rs.getMetaData();
				while (rs.next()) {
					final JKDbIdValue combo = new JKDbIdValue();
					combo.setId(rs.getString(1));

					if (metaData.getColumnCount() >= 2) {
						if (rs.getString(2) == null) {
							this.logger.fine(query.concat(" generating null values"));
						} else {
							combo.setValue(rs.getString(2));
						}
					} else {
						this.logger.fine(query.concat(" generating single column only"));
					}
					results.add(combo);
				}
				JKAbstractPlainDataAccess.listsCache.put(query, results);
			} catch (final JKDataAccessException e) {
				throw e;
			} catch (final SQLException e) {
				throw new JKDataAccessException(e);
			} finally {
				close(rs, ps, con);
			}
		}
		return (List<JKDbIdValue>) JKAbstractPlainDataAccess.listsCache.get(query);
	}

	@Override
	public String executeQueryAsString(final String query, Object... params) {
		return executeQueryAsString(query, ",", System.getProperty("line.separator"), params);
	}

	@Override
	public String executeQueryAsString(final String query, final String fieldSeparator, final String recordsSepartor, Object... params) {
		logger.info(String.format("executeQueryAsString , Query(%s) Params(%s)", query, Arrays.toString(params)));
		try {
			final CachedRowSet rs = executeQueryAsCachedRowSet(query, params);
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
			throw new JKDataAccessException(e);
		}
	}

	@Override
	public int executeUpdate(final JKUpdater updater) throws JKDataAccessException {
		logger.info(String.format("executeUpdater, Query(%s)", updater.getQuery()));
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(updater.getQuery(), Statement.RETURN_GENERATED_KEYS);
			updater.setParamters(ps);
			final int count = ps.executeUpdate();
			if (count == 0) {
				throw new JKRecordNotFoundException("RECORD_NOT_FOUND");
			}
			return getGeneratedKeys(ps);
			// no auto increment fields
		} catch (final SQLException e) {
			throw new JKDataAccessException(e);
		} finally {
			close(ps, connection);
		}
	}

	/**
	 * 
	 * @param updater
	 * @param ignoreRecordNotFoundException
	 * @return
	 * @throws JKDataAccessException
	 */
	@Override
	public int executeUpdate(final JKUpdater updater, final boolean ignoreRecordNotFoundException) {
		try {
			return executeUpdate(updater);
		} catch (final JKRecordNotFoundException e) {
			if (!ignoreRecordNotFoundException) {
				throw e;
			}
			return 0;
		}
	}

	@Override
	public int execute(final String query, final Object... params) throws JKDataAccessException {
		logger.info(String.format("executeUpdat, Query(%s) , Params (%s)", query, Arrays.toString(params)));
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = getConnection();
			ps = prepareStatement(connection, query, params);
			final int count = ps.executeUpdate();
			// no auto increment fields
			return count;
		} catch (final SQLException e) {
			throw new JKDataAccessException(e);
		} finally {
			close(ps);
			close(connection);
		}
	}

	@Override
	public Object[] executeQueryAsArray(final String query, Object... params) {
		logger.info(String.format("executeQueryAsArray, Query(%s) , Params (%s)", query, Arrays.toString(params)));
		try {
			final CachedRowSet rs = executeQueryAsCachedRowSet(query, params);
			final ResultSetMetaData meta = rs.getMetaData();
			final List<Object[]> rows = new Vector<>();
			while (rs.next()) {
				final Object[] row = new Object[meta.getColumnCount()];
				for (int i = 0; i < meta.getColumnCount(); i++) {
					row[i] = rs.getObject(i + 1);
				}
				rows.add(row);
			}
			return rows.toArray();
		} catch (final SQLException e) {
			throw new JKDataAccessException(e);
		}
	}

	@Override
	public List<List<Object>> executeQueryAsList(final String query, final Object... params) {
		logger.info(String.format("executeQueryAsList, Query(%s) , Params (%s)", query, Arrays.toString(params)));
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
			throw new JKDataAccessException(e);
		}
	}

	@Override
	public Object exeuteSingleOutputQuery(final String query, final Object... params) {
		logger.info(String.format("executeQueryAsSingleOutput, Query(%s) , Params (%s)", query, Arrays.toString(params)));
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = getConnection(true);
			ps = prepareQueryStatement(con, query, params);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getObject(1);
			}
			throw new JKRecordNotFoundException("No value available for query :".concat(query));
		} catch (final SQLException ex) {
			throw new JKDataAccessException(ex.getMessage().concat(",".concat(query)), ex);
		} finally {
			close(rs, ps, con);
		}
	}

	@Override
	public <T> T findRecord(final JKFinder finder) {
		logger.info(String.format("findRecord, Query(%s)", finder.getQuery()));
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection(true);
			ps = prepareQueryStatement(connection, finder.getQuery());
			finder.setParamters(ps);
			rs = ps.executeQuery();
			if (rs.next()) {
				return finder.populate(rs);
			}
			throw new JKRecordNotFoundException("REOCRD_NOT_FOUND");
		} catch (final SQLException e) {
			throw new JKDataAccessException("Error while executing the following select statement : \n".concat(finder.getQuery()), e);
		} finally {
			close(rs, ps, connection);
		}
	}

	@Override
	public <T> T findRecord(final JKFinder finder, final String tableName, final Object recordId) {
		if (JKAbstractPlainDataAccess.objectsCache.get(tableName) == null) {
			JKAbstractPlainDataAccess.objectsCache.put(tableName, new Hashtable<Object, Object>());
		}
		try {
			final Hashtable<Object, Object> tableCache = JKAbstractPlainDataAccess.objectsCache.get(tableName);
			if (tableCache.get(recordId) == null) {
				final T record = findRecord(finder);
				// if the size exceeded the max cache size , don't cache
				if (tableCache.size() > Integer
						.parseInt(getDataSource().getProperty(JKDbConstants.DB_MAX_CACHE_SIZE, JKDbConstants.DB_MAX_CACHE_SIZE_DEFAULT))) {
					return record;
				}
				tableCache.put(recordId, record);
			} else {
				// System.out.println("return "+tableName+" info from cache");
			}
			return (T) tableCache.get(recordId);
		} catch (final JKRecordNotFoundException e) {
			throw new JKRecordNotFoundException("RECORD_NOT_FOUND_FOR_TABLE (".concat(tableName).concat(") for ID (" + recordId + ")"));
		}
	}

	protected Connection getConnection() {
		return getConnection(false);
	}

	protected Connection getConnection(final boolean query) {
		this.logger.info("get connection with query flag : " + query);
		if (query) {
			return getDataSource().getQueryConnection();
		} else {
			return getDataSource().getConnection();
		}
	}

	protected JKDataSource getDataSource() {
		return JKDataSourceFactory.getDataSource();
	}

	protected int getGeneratedKeys(final PreparedStatement ps) throws SQLException {
		final ResultSet idRs = ps.getGeneratedKeys();
		if (idRs.next()) {
			final Object object = idRs.getObject(1);
			return ConversionUtil.toInteger(object);
		}
		return 0;
	}

	protected Long getNextId(final Connection connectoin, final String tableName, final String fieldName) throws JKDataAccessException {
		return getNextId(connectoin, tableName, fieldName, null);
	}

	protected Long getNextId(final Connection con, final String tableName, final String fieldName, final String condition) {
		PreparedStatement ps = null;
		try {
			String sql = "SELECT MAX(".concat(fieldName.concat(")+1 FROM ".concat(tableName)));
			if (condition != null && !condition.trim().equals("")) {
				sql += " WHERE " + condition;
			}
			ps = prepareQueryStatement(con, sql);
			Long id = 1l;
			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				id = rs.getLong(1);
				if (rs.wasNull()) {
					id = 1l;
				}
			} else {
				id = 1l;
			}
			return id;
		} catch (final SQLException e) {
			throw new JKDataAccessException(e);
		} finally {
			close(ps);
		}
	}

	@Override
	public Long getNextId(final String tableName, final String fieldName) {
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
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	public Long getNextId(final String tableName, final String fieldName, final String condition) {
		final Connection connection = getConnection(true);
		try {
			return getNextId(connection, tableName, fieldName, condition);
		} finally {
			close(connection);
		}
	}

	@Override
	public int getRowsCount(final String query) {
		logger.info(String.format("getRowsCount, Query(%s) ", query));
		final String sql = "SELECT COUNT(*) FROM (" + query + ") ";
		return new Integer(exeuteSingleOutputQuery(sql).toString());
	}

	@Override
	public Date getSystemDate() {
		logger.info(String.format("getSystemDate())"));
		switch (getDataSource().getDatabaseType()) {
		case MYSQL:
			return (Date) exeuteSingleOutputQuery("SELECT SYSDATE()");
		case ORACLE:
			return (Date) exeuteSingleOutputQuery("SELECT SYSTIMESTAMP FROM DUAL");
		}
		throw new IllegalStateException("not implemented for database : ".concat(getDataSource().getDatabaseType().toString()));
	}

	@Override
	public <T> List<T> getList(final JKFinder finder) {
		logger.info(String.format("executeUpdat, Query(%s) ", finder.getQuery()));
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection(true);
			ps = prepareQueryStatement(connection, finder.getQuery());
			finder.setParamters(ps);
			rs = ps.executeQuery();
			this.logger.fine(ps.toString().substring(ps.toString().toUpperCase().indexOf("SELECT")));
			final List<T> list = new Vector<>();
			while (rs.next()) {
				T populate = finder.populate(rs);
				list.add(populate);
			}
			return list;
		} catch (final SQLException e) {
			if (ps != null) {
				this.logger.severe(ps.toString());
			}
			throw new JKDataAccessException(e);
		} finally {
			close(rs, ps, connection);
		}
	}

	@Override
	public <T> List<T> getList(final JKFinder finder, final String key) {
		if (JKAbstractPlainDataAccess.listsCache.get(key) == null) {
			JKAbstractPlainDataAccess.listsCache.put(key, getList(finder));
		}
		return (List<T>) JKAbstractPlainDataAccess.listsCache.get(key);
	}

	/**
	 * 
	 * @param connection
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	protected PreparedStatement prepareQueryStatement(final Connection connection, final String sql, final Object... params) throws SQLException {
		final PreparedStatement prepareStatement = connection.prepareStatement(sql,ResultSet.FETCH_FORWARD, ResultSet.CONCUR_READ_ONLY);
		return setParams(prepareStatement, params);
	}

	protected PreparedStatement prepareStatement(final Connection connection, final String sql, final Object... params) throws SQLException {
		final PreparedStatement prepareStatement = connection.prepareStatement(sql);
		return setParams(prepareStatement, params);
	}

	
	protected PreparedStatement setParams(final PreparedStatement prepareStatement, final Object... params) throws SQLException {
		for (int i = 0; i < params.length; i++) {
			prepareStatement.setObject(i + 1, params[i]);
		}
		return prepareStatement;
	}

	protected void printRecordResultSet(final ResultSet rs) {
		printRecordResultSet(rs, true, System.out);
	}

	protected void printRecordResultSet(final ResultSet rs, final boolean all, PrintStream out) {
		try {
			final java.sql.ResultSetMetaData meta = rs.getMetaData();
			out.println("At print result set");
			while (rs.next()) {
				out.println("------------------------------------------------------");
				for (int i = 0; i < meta.getColumnCount(); i++) {
					out.print(meta.getColumnName(i + 1) + " = " + rs.getObject(i + 1) + "\t");
				}
				out.println();
				if (!all) {
					return;
				}
			}
			out.println("///////////////////////");
		} catch (final SQLException e) {
			throw new JKDataAccessException(e);
		}

	}

	public static void removeListCache(final String query) {
		JKAbstractPlainDataAccess.listsCache.remove(query);
	}

	public synchronized static void resetCache() {
		JKDataSourceFactory.getDataSource().resetCache();
	}

	@Override
	public <T> List<T> executeQueryAsObjectList(Class<T> clas, String instanceProperyNames, String query, Object... params) {
		logger.info(String.format("executeQueryAsObjectList, Class(%s), Properties(%s), Query(%s) , Params (%s)", clas.getSimpleName(),
				instanceProperyNames, query, Arrays.toString(params)));
		String[] properties = instanceProperyNames.split(",");
		List<T> results = new Vector<>();
		Object[] rows = executeQueryAsArray(query, params);
		for (Object rowObject : rows) {
			Object[] row = (Object[]) rowObject;
			T instance = ObjectUtil.newInstance(clas);
			for (int i = 0; i < row.length; i++) {
				ObjectUtil.setPeopertyValue(instance, properties[i], row[i]);
			}
			results.add(instance);
		}
		return results;
	}

	@Override
	public <T> T executeQueryAsSingleObject(Class<T> clas, String instanceProperyNames, String query, Object... params) {
		List<T> list = executeQueryAsObjectList(clas, instanceProperyNames, query, params);
		if (list.size() == 0) {
			return null;
		}
		if (list.size() > 1) {
			throw new JKDataAccessException("results contains more than one row");
		}
		return list.get(0);
	}
}
