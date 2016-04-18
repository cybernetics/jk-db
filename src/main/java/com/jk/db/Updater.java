/**
 * 
 */
package com.jk.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.jk.db.exception.JKDaoException;

/**
 * @author u087
 * 
 */
public interface Updater {
	public String getUpdateSql();

	public void setParamters(PreparedStatement ps) throws SQLException, JKDaoException;
}
