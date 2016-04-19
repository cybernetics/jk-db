package com.jk.db.datasource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.jk.db.exception.JKDaoException;

public interface JKDataSource {


	/**
	 * @return
	 * @throws JKDaoException
	 */
	public Connection getConnection() throws JKDaoException;

	/**
	 * @param con
	 */
	public void close(Connection con);

	/**
	 * 
	 * @param stmt
	 */
	public void close(Statement stmt);

	/**
	 * 
	 * @param rs
	 */
	public void close(ResultSet rs);

	/**
	 * 
	 * @param connection
	 * @param commit
	 * @throws JKDaoException
	 */
	public void close(Connection connection, boolean commit) throws JKDaoException;

	/**
	 * 
	 * @return
	 * @throws JKDaoException
	 */
	public JKSession createSession() throws JKDaoException;

	/**
	 * @return
	 */
	public String getPassword();

	/**
	 * @return
	 */
	public String getUsername();

	/**
	 * 
	 * @param property
	 * @param defaultValue
	 * @return
	 */
	public String getProperty(String property, String defaultValue);

	/**
	 * 
	 * @return
	 */
	public Properties getProperties();

	/**
	 * 
	 * @return
	 * @throws JKDaoException
	 */
	public Connection getQueryConnection() throws JKDaoException;

	/**
	 * 
	 * @return
	 */
	public String getDriverName();

	/**
	 * 
	 * @return
	 */
	public String getDatabaseUrl();
	
	
	/**
	 * 
	 * @return
	 */
	public String getTestQuery();
}
