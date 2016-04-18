package com.jk.db;

public class JKDbConstants {

	public static final String DEFAULT_CONFIG_FILE_NAME = "jk-db.properties";
	public static final String DEFAULT_CONFIG_FILE_NAME_KEY = "jk.db.config.file";

	/**
	 * 
	 */
	public static final String PROPERTY_DRIVER_NAME = "db-driver-name";
	public static final String PROPERTY_DB_URL = "db-url";
	public static final String PROPERTY_DB_USER = "db-user";
	public static final String PROPERTY_DB_PASSWORD = "db-password";

	public static final String PROPERTY_QUERY_LIMIT = "query-limit";
	public static final String PROPERTY_INITIAL_POOL_SIZE_KEY = "db-initial-size";
	public static final String PROPERTY_MAX_POOL_SIZE_KEY = "db-max-total";
	
	/**
	 * 
	 */
	public static final int DEFAULT_LIMIT = 100;
	public static final String DEFAULT_POOL_INITIAL_SIZE = "2";
	public static final String DEFAULT_POOL_MAX_SIZE = "10";
	
	/**
	 * 
	 */
	public static final String DEFAULT_DB_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/app";
	public static final String DEFAULT_DB_USER = "root";
	public static final String DEFAULT_DB_PASSWORD = "123456";
	


}
