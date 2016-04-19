package com.jk.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.jk.db.exception.JKDaoException;

/**
 * 
 * @author Jalal
 *
 */
public interface Updater {
	public String getUpdateSql();

	public void setParamters(PreparedStatement ps) throws SQLException, JKDaoException;
}
