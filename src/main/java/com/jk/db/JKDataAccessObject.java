package com.jk.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.sql.rowset.CachedRowSet;

import com.jk.db.exception.JKDaoException;
import com.jk.db.exception.JKRecordNotFoundException;
import com.jk.util.StringUtil;
import com.sun.rowset.CachedRowSetImpl;

/**
 * 
 * @author Jalal
 *
 */
public interface JKDataAccessObject {

	Object findRecord(Finder finder);

	Object exeuteSingleOutputQuery(String query, Object... params) throws JKDaoException;

	int getRowsCount(String query) throws NumberFormatException, JKDaoException;

	int getNextId(String tableName, String fieldName) throws JKDaoException;

	Object[] exeuteQueryAsArray(String query) throws JKDaoException;

	int executeUpdate(String sql) throws JKDaoException;

	List createRecordsFromSQL(String sql) throws JKDaoException;

	CachedRowSet executeQuery(String query) throws JKDaoException;

	Object exeuteSingleOutputQuery(String query) throws JKDaoException;

	int executeUpdate(Updater updater) throws JKDaoException;

	List lstRecords(Finder finder) throws JKDaoException;

}
