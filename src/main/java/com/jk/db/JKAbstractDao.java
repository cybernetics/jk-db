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

public abstract class JKAbstractDao implements JKDataAccessObject {
	Logger logger = Logger.getLogger(getClass().getName());
	// TODO : add support for max cache size
	private static Map<String, Hashtable<Object, Object>> cache = new Hashtable<String, Hashtable<Object, Object>>();
	private static Map<String, List<? extends Object>> listCache = new Hashtable<>();

	private JKDataSource connectionManager;
	private JKSession session;

	/**
	 * 
	 */
	public JKAbstractDao() {
	}

	/**
	 * 
	 * @param connectionManager
	 */
	public JKAbstractDao(JKDataSource connectionManager) {
		this.connectionManager = connectionManager;
	}

	/**
	 * 
	 * @param session
	 */
	public JKAbstractDao(JKSession session) {
		setSession(session);
	}

	/**
	 * 
	 * @return
	 */
	protected JKDataSource getDataSource() {
		if (this.connectionManager != null) {
			return connectionManager;
		}
		return JKDataSourceFactory.getDefaultDataSource();
	}

	/**
	 * 
	 * @return
	 * @throws JKDaoException
	 */
	protected Connection getConnection() {
		return getConnection(false);
	}

	/**
	 * 
	 * @param query
	 * @return
	 * @throws JKDaoException
	 */
	protected Connection getConnection(boolean query) {
		if (session != null && !session.isClosed()) {
			return session.getConnection();
		}
		if (query) {
			return getDataSource().getQueryConnection();
		} else {
			return getDataSource().getConnection();
		}
	}

	/**
	 * 
	 * @param connection
	 */
	protected void close(Connection connection) {
		if (connection == null) {
			return;
		}
		if (session == null) {
			logger.info("closing connection by the datasource");
			getDataSource().close(connection);
		} else {
			logger.info("connection not closed since its part of session (trx)");
			// System.err.println("keeping class connection open");
			// this connection is part of transaction , dont close it , it
			// should be closed by the object
			// who passed the connection to this object
		}
	}

	/**
	 * 
	 * @param session
	 */
	public void setSession(JKSession session) {
		this.session = session;
	}

	/**
	 * 
	 * @param finder
	 * @return
	 * @throws JKRecordNotFoundException
	 * @throws JKDaoException
	 */
	@Override
	public Object findRecord(Finder finder) {
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
		} catch (SQLException e) {
			throw new JKDaoException(
					"Error while executing the following select statement : \n" + finder.getFinderSql(), e);
		} finally {
			close(rs);
			close(ps);
			close(connection);
		}
	}

	/**
	 * 
	 * @param connection
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	protected PreparedStatement prepareStatement(Connection connection, String sql) throws SQLException {
		PreparedStatement prepareStatement = connection.prepareStatement(sql);
		return prepareStatement;
	}

	/**
	 * 
	 * @param finder
	 * @param tableName
	 * @param recordId
	 * @return
	 * @throws JKRecordNotFoundException
	 * @throws JKDaoException
	 */
	public Object findRecord(Finder finder, String tableName, Object recordId)
			throws JKRecordNotFoundException, JKDaoException {
		if (JKAbstractDao.cache.get(tableName) == null) {
			JKAbstractDao.cache.put(tableName, new Hashtable<Object, Object>());
		}
		try {
			Hashtable<Object, Object> tableCache = JKAbstractDao.cache.get(tableName);
			if (tableCache.get(recordId) == null) {
				// System.out.println("loading record "+tableName);
				Object record = findRecord(finder);
				// if the size exceeded the max cache size , don't cache
				if (tableCache.size() > Integer.parseInt(getDataSource().getProperty("max-cache-size", "1000"))) {
					return record;
				}
				tableCache.put(recordId, record);
			} else {
				// System.out.println("return "+tableName+" info from cache");
			}
			return tableCache.get(recordId);
		} catch (JKRecordNotFoundException e) {
			throw new JKRecordNotFoundException(
					"RECORD_NOT_FOUND_FOR_TABLE (" + tableName + ") for ID (" + recordId + ")");
		}
	}

	/**
	 * 
	 * @param finder
	 * @param key
	 * @return
	 * @throws JKDaoException
	 */
	public List lstRecords(Finder finder, String key) throws JKDaoException {
		if (listCache.get(key) == null) {
			listCache.put(key, lstRecords(finder));
		}
		return listCache.get(key);
	}

	/**
	 * 
	 * @param finder
	 * @return
	 * @throws JKDaoException
	 */
	@Override
	public List lstRecords(Finder finder) throws JKDaoException {
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
			ArrayList list = new ArrayList();
			while (rs.next()) {
				list.add(finder.populate(rs));
			}
			return list;
		} catch (SQLException e) {
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
	 * 
	 * @param updater
	 * @return
	 * @throws JKDaoException
	 */
	public int executeUpdate(Updater updater, boolean ignoreRecordNotFoundException) throws JKDaoException {
		try {
			return executeUpdate(updater);
		} catch (JKRecordNotFoundException e) {
			if (!ignoreRecordNotFoundException) {
				throw e;
			}
			return 0;
		}
	}

	/**
	 * 
	 * @param updater
	 * @return
	 * @throws JKDaoException
	 */
	@Override
	public int executeUpdate(Updater updater) throws JKDaoException {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			logger.info("get connection");
			connection = getConnection();
			ps = connection.prepareStatement(updater.getUpdateSql(), Statement.RETURN_GENERATED_KEYS);
			updater.setParamters(ps);
			logger.info("Exectuing statment : ".concat(ps.toString()));
			int count = ps.executeUpdate();
			logger.info("executed succ...");
			if (count == 0) {
				throw new JKRecordNotFoundException("RECORD_NOT_FOUND");
			}
			return getGeneratedKeys(ps);
			// no auto increment fields
		} catch (SQLException e) {
			throw new JKDaoException(e);
		} finally {
			close(ps,connection);
		}
	}

	/**
	 * 
	 * @param ps
	 * @return
	 * @throws SQLException
	 */
	protected int getGeneratedKeys(PreparedStatement ps) throws SQLException {
		if (JKDataSourceUtil.isOracle(getDataSource())) {
			throw new IllegalStateException("This method is not avilable on oracle db");
		}
		ResultSet idRs = ps.getGeneratedKeys();
		if (idRs.next()) {
			Object object = idRs.getObject(1);
			return ConversionUtil.toInteger(object);
		}
		return 0;
	}

	/**
	 * 
	 * @param ps
	 */
	protected void close(java.sql.PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 
	 * @param rs
	 */
	protected void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
			}
		}
	}

	/*
	 * 
	 */
	public void printRecordResultSet(ResultSet rs) {
		printRecordResultSet(rs, true);
	}

	/**
	 * 
	 * @return
	 * @throws JKRecordNotFoundException
	 * @throws JKDaoException
	 */
	public Date getSystemDate() throws JKRecordNotFoundException, JKDaoException {
		Finder finder = new Finder() {
			@Override
			public String getFinderSql() {
				return "SELECT SYSDATE()";
			}

			@Override
			public Date populate(ResultSet rs) throws SQLException, JKRecordNotFoundException, JKDaoException {
				return rs.getTimestamp(1);
			}

			@Override
			public void setParamters(PreparedStatement ps) {
			}
		};
		return (Date) findRecord(finder);
	}

	/**
	 * 
	 * @param query
	 * @return
	 * @throws JKDaoException
	 */
	@Override
	public Object exeuteSingleOutputQuery(String query) throws JKDaoException {
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
		} catch (SQLException ex) {
			throw new JKDaoException(ex.getMessage() + "\n" + query, ex);
		} finally {
			close(rs, ps, con);
		}
	}

	/**
	 * 
	 * @param query
	 * @return
	 * @throws JKDaoException
	 */
	@Override
	public CachedRowSet executeQuery(String query) throws JKDaoException {
		Statement ps = null;
		Connection con = null;
		ResultSet rs = null;
		// System.out.println(query);
		try {
			con = getConnection();
			ps = createStatement(con);
			rs = ps.executeQuery(query);
			CachedRowSet impl = new CachedRowSetImpl();
			impl.populate(rs);
			return impl;
		} catch (SQLException ex) {
			throw new JKDaoException(ex.getMessage(), ex);
		} finally {
			close(rs);
			close(con);
		}
	}

	/**
	 * 
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	protected Statement createStatement(Connection con) throws SQLException {
		Statement createStatement = con.createStatement();
		// if (getDataSource().getDriverName().toLowerCase().contains("mysql"))
		// {
		// createStatement.setFetchSize(Integer.MIN_VALUE);
		// }
		return createStatement;
	}

	/**
	 * 
	 * @param sql
	 * @return
	 * @throws JKDaoException
	 */
	@Override
	public List createRecordsFromSQL(String sql) throws JKDaoException {
		if (listCache.get(sql) == null) {
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
				Vector<JKDbIdValue> results = new Vector<JKDbIdValue>();
				ResultSetMetaData metaData = rs.getMetaData();
				while (rs.next()) {
					JKDbIdValue combo = new JKDbIdValue();
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
				listCache.put(sql, results);
			} catch (JKDaoException e) {
				throw e;
			} catch (SQLException e) {
				throw new JKDaoException(e);
			} finally {
				close(rs, ps, con);
			}
		}
		return listCache.get(sql);
	}

	/**
	 * 
	 * @param sql
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	protected PreparedStatement createStatement(String sql, Connection con) throws SQLException {
		return con.prepareStatement(sql);
	}

	/**
	 * 
	 * @param sql
	 * @return
	 * @throws JKDaoException
	 */
	@Override
	public int executeUpdate(String sql) throws JKDaoException {
		Connection connection = null;
		Statement ps = null;
		try {
			connection = getConnection();
			ps = createStatement(connection);
			System.err.println("Executeing : " + sql);
			int count = ps.executeUpdate(sql);
			System.err.println("Affeted Rows : " + count);
			// no auto increment fields
			return 0;
		} catch (SQLException e) {
			throw new JKDaoException(e);
		} finally {
			close(ps);
			close(connection);
		}
	}

	/**
	 * 
	 * @param ps
	 */
	public void close(Statement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
			}
		}
	}

	/**
	 * 
	 * @param string
	 */
	private void debug(String string) {
		if (System.getProperty("jk.sql.debug", "false").equals("true")) {
			DebugUtil.printCurrentTime(string);
		}
	}

	/**
	 * 
	 */
	public synchronized static void resetCache() {
		cache.clear();
		listCache.clear();
	}

	/**
	 * 
	 * @param query
	 */
	public static void removeListCache(String query) {
		listCache.remove(query);
	}

	/**
	 * 
	 * @param query
	 * @param fieldSeparator
	 * @param recordsSepartor
	 * @return
	 * @throws JKDaoException
	 */
	public String executeOutputQuery(String query, String fieldSeparator, String recordsSepartor)
			throws JKDaoException {
		try {
			CachedRowSet rs = executeQuery(query);
			ResultSetMetaData meta = rs.getMetaData();
			StringBuffer buf = new StringBuffer();
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
		} catch (SQLException e) {
			throw new JKDaoException(e);
		}
	}

	/**
	 * 
	 * @param query
	 * @return
	 * @throws JKDaoException
	 */
	@Override
	public Object[] exeuteQueryAsArray(String query) throws JKDaoException {
		try {
			CachedRowSet rs = executeQuery(query);
			ResultSetMetaData meta = rs.getMetaData();
			ArrayList<Object[]> rows = new ArrayList<Object[]>();
			while (rs.next()) {
				Object[] row = new Object[meta.getColumnCount()];
				for (int i = 0; i < meta.getColumnCount(); i++) {
					row[i] = (rs.getObject(i + 1));
				}
				rows.add(row);
			}
			return (Object[]) rows.toArray();
		} catch (SQLException e) {
			throw new JKDaoException(e);
		}
	}

	/**
	 * 
	 * @param rs
	 * @param ps
	 * @param c
	 */
	public void close(ResultSet rs, PreparedStatement ps, Connection c) {
		close(rs);
		close(ps);
		close(c);
	}

	/**
	 * 
	 * @param ps
	 * @param c
	 */
	public void close(PreparedStatement ps, Connection c) {
		close(ps);
		close(c);
	}

	/**
	 * 
	 * @param tableName
	 * @param fieldName
	 * @return
	 * @throws JKDaoException
	 */
	@Override
	public int getNextId(String tableName, String fieldName) throws JKDaoException {
		Connection connection = getConnection(true);
		try {
			return getNextId(connection, tableName, fieldName);
		} finally {
			close(connection);
		}
	}

	/**
	 * 
	 * @param connectoin
	 * @param tableName
	 * @param fieldName
	 * @return
	 * @throws JKDaoException
	 */
	public int getNextId(Connection connectoin, String tableName, String fieldName) throws JKDaoException {
		return getNextId(connectoin, tableName, fieldName, null);
	}

	/**
	 * 
	 * @param tableName
	 * @param fieldName
	 * @param condition
	 * @return
	 * @throws JKDaoException
	 */
	public int getNextId(String tableName, String fieldName, String condition) throws JKDaoException {
		Connection connection = getConnection(true);
		try {
			return getNextId(connection, tableName, fieldName, condition);
		} finally {
			close(connection);
		}
	}

	/**
	 * 
	 * @param con
	 * @param tableName
	 * @param fieldName
	 * @param condition
	 * @return
	 * @throws JKDaoException
	 */
	public int getNextId(Connection con, String tableName, String fieldName, String condition) throws JKDaoException {
		PreparedStatement ps = null;
		int ser;
		try {
			String sql = "SELECT MAX(" + fieldName + ")+1 FROM " + tableName;
			if (condition != null && !condition.trim().equals("")) {
				sql += " WHERE " + condition;
			}
			ps = createStatement(sql, con);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ser = rs.getInt(1);
				if (rs.wasNull()) {
					return 1;
				}
			} else {
				ser = 1;
			}
			return (ser);
		} catch (SQLException e) {
			throw new JKDaoException(e);
		} finally {
			close(ps);
		}
	}

	/**
	 * 
	 * @param rs
	 * @param all
	 */
	public void printRecordResultSet(ResultSet rs, boolean all) {
		try {
			java.sql.ResultSetMetaData meta = rs.getMetaData();
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
		} catch (SQLException e) {
			throw new JKDaoException(e);
		}

	}

	/**
	 * 
	 */
	@Override
	public int getRowsCount(String query) throws NumberFormatException, JKDaoException {
		String sql = "SELECT COUNT(*) FROM (" + query + ") ";
		// System.out.println(sql);
		return new Integer(exeuteSingleOutputQuery(sql).toString());
	}

	/**
	 * 
	 * @param query
	 * @param fromRowIndex
	 * @param toRowIndex
	 * @return
	 * @throws JKDaoException
	 */
	public CachedRowSet executeQuery(String query, int fromRowIndex, int toRowIndex) throws JKDaoException {
		throw new IllegalStateException("this method should be abstract to support other DBMS other than Oracle");
		// String sql =
		// "select * from ( select a.*, rownum r from ( "+query+") a where
		// rownum <= "
		// + (toRowIndex) + " ) where r > " + fromRowIndex;
		// return executeQuery(sql);
	}

	/**
	 * 
	 * @param query
	 * @param params
	 * @return
	 * @throws JKDaoException
	 */
	@Override
	public Object exeuteSingleOutputQuery(String query, Object... params) throws JKDaoException {
		return exeuteSingleOutputQuery(StringUtil.compile(query, params));
	}

}
