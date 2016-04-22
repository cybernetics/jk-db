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
package com.jk.db.dataaccess.orm.eclipselink;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

/**
 * Credit to eclipselink.PersistenceUnitProperties
 * @author Jalal Kiswani
 * Apr 22, 2016
 */
public class JKPersistenceUnitProperties {

	/**
	 * The <code>javax.persistence.transactionType"</code> property specifies
	 * the transaction type for the persistence unit. This property overrides
	 * the value specified in the persistence.xml.
	 * <p>
	 * Values: A string value of "JTA" or "RESOURCE_LOCAL"
	 */
	public static final String TRANSACTION_TYPE = "javax.persistence.transactionType";

	/**
	 * The <code>javax.persistence.jtaDataSource"</code> property specifies the
	 * JTA data source name that will look up a valid
	 * {@link javax.sql.DataSource}. This property is used to override the value
	 * specified in the persistence.xml.
	 * <p>
	 * Values: A well formed JNDI resource name that can locate the data source
	 * in the target container or an instance of {@link javax.sql.DataSource}
	 */
	public static final String JTA_DATASOURCE = "javax.persistence.jtaDataSource";

	/**
	 * The <code>javax.persistence.nonJtaDataSource"</code> property specifies
	 * the non-JTA data source name that will look up a valid
	 * {@link javax.sql.DataSource}. This can be used to override the value
	 * specified in the persistence.xml.
	 * <p>
	 * Values: A well formed JNDI resource name that can locate the data source
	 * in the target container or an instance of {@link javax.sql.DataSource}
	 */
	public static final String NON_JTA_DATASOURCE = "javax.persistence.nonJtaDataSource";

	// JDBC Properties for internal connection pooling

	/**
	 * The <code>javax.persistence.jdbc.driver"</code> property specifies the
	 * JDBC DriverManager class name used for internal connection pooling when a
	 * data source is not being used. The value must be a string which is the
	 * qualified class name for a valid class that implements
	 * <code>java.sql.Driver</code>.
	 * <p>
	 * <b>Persistence XML example:</b> <code>
	 * <property name="javax.persistence.jdbc.driver" value="com.mysql.jdbc.Driver" />
	 * </code>
	 * <p>
	 * The previous value for this property
	 * <code>"eclipselink.jdbc.driver</code> is now deprecated and should be
	 * replaced with this new name.
	 */
	public static final String JDBC_DRIVER = "javax.persistence.jdbc.driver";

	/**
	 * The <code>javax.persistence.jdbc.url"</code> property specifies the JDBC
	 * URL used for internal connection pooling when a data source is not being
	 * used. The value must be a string which represents a valid URL for the
	 * specified JDBC driver.
	 * <p>
	 * <b>Persistence XML example:</b> <code>
	 * <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost/mysql" />
	 * </code>
	 * <p>
	 * The previous value for this property <code>"eclipselink.jdbc.url</code>
	 * is now deprecated and should be replaced with this new name.
	 */
	public static final String JDBC_URL = "javax.persistence.jdbc.url";

	/**
	 * The <code>javax.persistence.jdbc.user"</code> property specifies the data
	 * source or JDBC user name.
	 * <p>
	 * <b>Persistence XML example:</b> <code>
	 * <property name="javax.persistence.jdbc.user" value="user-name" />
	 * </code>
	 * <p>
	 * The previous value for this property <code>"eclipselink.jdbc.user</code>
	 * is now deprecated and should be replaced with this new name.
	 */
	public static final String JDBC_USER = "javax.persistence.jdbc.user";

	/**
	 * The <code>javax.persistence.jdbc.password"</code> property specifies the
	 * data source or JDBC password.
	 * <p>
	 * <b>Persistence XML example:</b> <code>
	 * <property name="javax.persistence.jdbc.password" value="password" />
	 * </code> The previous value for this property
	 * <code>"eclipselink.jdbc.password</code> is now deprecated and should be
	 * replaced with this new name.
	 */
	public static final String JDBC_PASSWORD = "javax.persistence.jdbc.password";

	/**
	 * The <code>"javax.persistence.validation.factory"</code> property
	 * specifies an instance of <a href =
	 * http://download.oracle.com/javaee/6/docs/api/javax/validation/
	 * ValidatorFactory.html>javax.validation.ValidatorFactory</a> used by
	 * EclipseLink to perform Automatic Validation upon Lifecycle Events. If the
	 * property is not specified, and if Bean Validation API is visible to
	 * Eclipselink, it will try to instantiate an instance of
	 * <code>javax.validation.ValidationFactory</code> by calling
	 * <code>Validation.buildDefaultValidatorFactory()</code>
	 */
	public static final String VALIDATOR_FACTORY = "javax.persistence.validation.factory";

	/**
	 * The <code>"javax.persistence.validation.mode"</code> property specifies
	 * whether the automatic lifecycle event validation is in effect.
	 * <p>
	 * Valid values for this property are "AUTO", "CALLBACK" or "NONE".
	 */
	public static final String VALIDATION_MODE = "javax.persistence.validation.mode";

	/**
	 * The <code>"javax.persistence.validation.group.pre-persist"</code>
	 * property specifies the name of the validator groups to execute for
	 * preUpdate event. The value should be a string with fully qualified
	 * classnames separated by a comma (','). If this value is not specified in
	 * both persistence.xml or using this property, the default Bean Validation
	 * group (the group Default) will be validated
	 */
	public static final String VALIDATION_GROUP_PRE_PERSIST = "javax.persistence.validation.group.pre-persist";

	/**
	 * The <code>"javax.persistence.validation.group.pre-update"</code> property
	 * specifies the name of the validator groups to execute for preUpdate
	 * event. The value should be a string with fully qualified classnames
	 * separated by a comma (','). If this value is not specified in both
	 * persistence.xml or using this property, the default Bean Validation group
	 * (the group Default) will be validated
	 */
	public static final String VALIDATION_GROUP_PRE_UPDATE = "javax.persistence.validation.group.pre-update";

	/**
	 * The <code>"javax.persistence.validation.group.pre-remove"</code> property
	 * specifies the name of the validator groups to execute for preRemove
	 * event. The value should be a string with fully qualified classnames
	 * separated by a comma (','). If this value is not specified in both
	 * persistence.xml or using this property, no validation will occur on
	 * remove.
	 */
	public static final String VALIDATION_GROUP_PRE_REMOVE = "javax.persistence.validation.group.pre-remove";

	// Caching Properties

	/**
	 * <p>
	 * If scripts are to be generated by the persistence provider and a
	 * connection to the target database is not supplied, the
	 * <code>"javax.persistence.database-product-name"</code> property must be
	 * specified.
	 * </p>
	 * 
	 * <p>
	 * The value of this property should be the value returned for the target
	 * database by the JDBC DatabaseMetaData method getDatabaseProductName.
	 * </p>
	 * 
	 * <p>
	 * If sufficient database version information is not included in the result
	 * of this method, the
	 * <code>"javax.persistence.database-major-version"</code> and
	 * <code>"javax.persistence.database-minor-version"</code> properties should
	 * be specified as needed. These should contain the values returned by the
	 * JDBC getDatabaseMajorVersion and getDatabaseMinor-Version methods
	 * respectively.
	 * </p>
	 */
	public static final String SCHEMA_DATABASE_PRODUCT_NAME = "javax.persistence.database-product-name";

	/**
	 * <p>
	 * If sufficient database version information is not included from the JDBC
	 * DatabaseMetaData method getDatabaseProductName, the
	 * <code>"javax.persistence.database-major-version"</code> property should
	 * be specified as needed. This should contain the value returned by the
	 * JDBC getDatabaseMajor-Version method.
	 * </p>
	 */
	public static final String SCHEMA_DATABASE_MAJOR_VERSION = "javax.persistence.database-major-version";

	/**
	 * <p>
	 * If sufficient database version information is not included from the JDBC
	 * DatabaseMetaData method getDatabaseProductName, the
	 * <code>"javax.persistence.database-minor-version"</code> property should
	 * be specified as needed. This should contain the value returned by the
	 * JDBC getDatabaseMinor-Version method.
	 * </p>
	 */
	public static final String SCHEMA_DATABASE_MINOR_VERSION = "javax.persistence.database-minor-version";

	/**
	 * <p>
	 * The
	 * <code>"javax.persistence.schema-generation.create-script-source"</code>
	 * is used for script execution.
	 * </p>
	 * 
	 * <p>
	 * In Java EE container environments, it is generally expected that the
	 * container will be responsible for executing DDL scripts, although the
	 * container is permitted to delegate this task to the persistence provider.
	 * </p>
	 * 
	 * <p>
	 * If DDL scripts are to be used in Java SE environments or if the Java EE
	 * container delegates the execution of scripts to the persistence provider,
	 * this property must be specified.
	 * </p>
	 * 
	 * <p>
	 * The
	 * <code>"javax.persistence.schema-generation.create-script-source"</code>
	 * property specifies a java.IO.Reader configured for reading of the DDL
	 * script or a string designating a file URL for the DDL script.
	 * </p>
	 */
	public static final String SCHEMA_GENERATION_CREATE_SCRIPT_SOURCE = "javax.persistence.schema-generation.create-script-source";

	/**
	 * <p>
	 * The <code>"javax.persistence.schema-generation.drop-script-source"</code>
	 * is used for script execution.
	 * </p>
	 * 
	 * <p>
	 * In Java EE container environments, it is generally expected that the
	 * container will be responsible for executing DDL scripts, although the
	 * container is permitted to delegate this task to the persistence provider.
	 * </p>
	 * 
	 * <p>
	 * If DDL scripts are to be used in Java SE environments or if the Java EE
	 * container delegates the execution of scripts to the persistence provider,
	 * this property must be specified.
	 * </p>
	 * 
	 * <p>
	 * The <code>"javax.persistence.schema-generation.drop-script-source"</code>
	 * property specifies a java.IO.Reader configured for reading of the DDL
	 * script or a string designating a file URL for the DDL script.
	 * </p>
	 */
	public static final String SCHEMA_GENERATION_DROP_SCRIPT_SOURCE = "javax.persistence.schema-generation.drop-script-source";

	/**
	 * <p>
	 * The <code>"javax.persistence.schema-generation.connection"</code>
	 * property specifies the JDBC connection to be used for schema generation.
	 * This is intended for use in Java EE environments, where the platform
	 * provider may want to control the database privileges that are available
	 * to the persistence provider.
	 * </p>
	 * 
	 * <p>
	 * This connection is provided by the container, and should be closed by the
	 * container when the schema generation request or entity manager factory
	 * creation completes.
	 * </p>
	 * 
	 * <p>
	 * The connection provided must have credentials sufficient for the
	 * persistence provider to carry out the requested actions.
	 * </p>
	 * 
	 * <p>
	 * If this property is not specified, the persistence provider should use
	 * the DataSource that has otherwise been provided.
	 * </p>
	 */
	public static final String SCHEMA_GENERATION_CONNECTION = "javax.persistence.schema-generation.connection";

	/**
	 * <p>
	 * In Java EE container environments, it is generally expected that the
	 * container will be responsible for executing data load scripts, although
	 * the container is permitted to delegate this task to the persistence
	 * provider. If a load script is to be used in Java SE environments or if
	 * the Java EE container delegates the execution of the load script to the
	 * persistence provider, this property must be specified.
	 * </p>
	 * 
	 * <p>
	 * The <code>"javax.persistence.sql-load-script-source"</code> property
	 * specifies a java.IO.Reader configured for reading of the SQL load script
	 * for database initialization or a string designating a file URL for the
	 * script.
	 * </p>
	 */
	public static final String SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE = "javax.persistence.sql-load-script-source";

	/**
	 * The <code>"javax.persistence.query.timeout"</code> property configures
	 * the default query timeout value.
	 * <p>
	 * Valid values are strings containing integers zero or greater.
	 */
	public static final String QUERY_TIMEOUT = "javax.persistence.query.timeout";

}
