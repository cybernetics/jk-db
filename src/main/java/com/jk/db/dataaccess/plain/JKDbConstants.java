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

/**
 * The Class JKDbConstants.
 *
 * @author Jalal Kiswani
 */
public interface JKDbConstants {

	/** The Constant DEFAULT_CONFIG_FILE_NAME. */
	public static final String DEFAULT_CONFIG_FILE_NAME = "jk-db.properties";

	/** The Constant DEFAULT_CONFIG_FILE_NAME_KEY. */
	public static final String DEFAULT_CONFIG_FILE_NAME_KEY = "jk.db.config.file";

	/** The Constant PROPERTY_DRIVER_NAME. */
	public static final String PROPERTY_DRIVER_NAME = "db-driver-name";

	/** The Constant PROPERTY_DB_URL. */
	public static final String PROPERTY_DB_URL = "db-url";

	/** The Constant PROPERTY_DB_USER. */
	public static final String PROPERTY_DB_USER = "db-user";

	/** The Constant PROPERTY_DB_PASSWORD. */
	public static final String PROPERTY_DB_PASSWORD = "db-password";

	/** The Constant PROPERTY_QUERY_LIMIT. */
	public static final String PROPERTY_QUERY_LIMIT = "query-limit";

	/** The Constant PROPERTY_INITIAL_POOL_SIZE_KEY. */
	public static final String PROPERTY_INITIAL_POOL_SIZE_KEY = "db-initial-size";

	/** The Constant PROPERTY_MAX_POOL_SIZE_KEY. */
	public static final String PROPERTY_MAX_POOL_SIZE_KEY = "db-max-total";

	/** The Constant DEFAULT_LIMIT. */
	public static final int DEFAULT_LIMIT = 100;

	/** The Constant DEFAULT_POOL_INITIAL_SIZE. */
	public static final String DEFAULT_POOL_INITIAL_SIZE = "2";

	/** The Constant DEFAULT_POOL_MAX_SIZE. */
	public static final String DEFAULT_POOL_MAX_SIZE = "10";

	/** The Constant DEFAULT_DB_DRIVER. */
	public static final String DEFAULT_DB_DRIVER = "com.mysql.jdbc.Driver";

	/** The Constant DEFAULT_DB_URL. */
	public static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/app";

	/** The Constant DEFAULT_DB_USER. */
	public static final String DEFAULT_DB_USER = "root";

	/** The Constant DEFAULT_DB_PASSWORD. */
	public static final String DEFAULT_DB_PASSWORD = "123456";

	public static final String DEFAULT_PERSISINCE_UNIT_NAME = "JK-DB";

	public static final String PROPERTY_DB_ENTITY_PACKAGES = "db-entities-packages";

	public static final String DEFAULT_DB_ENTITY_PACKAGES = "com.app";

}
