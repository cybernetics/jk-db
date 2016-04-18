/**
 * 
 */
package com.jk.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jk.db.exception.JKDaoException;
import com.jk.db.exception.JKRecordNotFoundException;

/**
 * @author u087
 * 
 */
public interface Finder {
	public String getFinderSql();

	/**
	 * @param ps
	 */
	public void setParamters(PreparedStatement ps) throws SQLException;

	/**
	 * @param rs
	 * @throws SQLException
	 * @throws JKDaoException
	 * @throws JKRecordNotFoundException
	 */
	public Object populate(ResultSet rs) throws SQLException, JKRecordNotFoundException, JKDaoException;
}
