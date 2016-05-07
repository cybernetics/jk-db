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

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import com.jk.db.dataaccess.JKDataAccess;
import com.jk.db.dataaccess.exception.JKDataAccessException;
import com.jk.db.example.Employee;

/**
 * The Interface JKDataAccessObject.
 *
 * @author Jalal Kiswani
 */
public interface JKPlainDataAccess extends JKDataAccess {

	/**
	 * Execute query.
	 *
	 * @param query
	 *            the query
	 * @param params
	 *            the params
	 * @return the cached row set
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	CachedRowSet executeQueryAsCachedRowSet(String query, Object... params);

	/**
	 * Creates the records from sql.
	 *
	 * @param sql
	 *            the sql
	 * @param params
	 *            the params
	 * @return the list
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	List<JKDbIdValue> executeQueryAsIdValue(String sql, Object... params);

	/**
	 * Execute update.
	 *
	 * @param updater
	 *            the updater
	 * @return the int
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	int executeUpdate(JKUpdater updater);

	/**
	 * Execute update.
	 *
	 * @param sql
	 *            the sql
	 * @param params
	 *            the params
	 * @return the int
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	int execute(String sql, Object... params);

	/**
	 * Exeute single output query.
	 *
	 * @param query
	 *            the query
	 * @param params
	 *            the params
	 * @return the object
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	Object exeuteSingleOutputQuery(String query, Object... params);

	/**
	 * Gets the next id.
	 *
	 * @param tableName
	 *            the table name
	 * @param fieldName
	 *            the field name
	 * @return the next id
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	Long getNextId(String tableName, String fieldName);

	/**
	 * Gets the rows count.
	 *
	 * @param query
	 *            the query
	 * @return the rows count
	 * @throws NumberFormatException
	 *             the number format exception
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	int getRowsCount(String query);

	String executeQueryAsString(String query, Object... params);

	String executeQueryAsString(String query, String fieldSeparator, String recordsSepartor, Object... params);

	int executeUpdate(JKUpdater updater, boolean ignoreRecordNotFoundException);

	List<List<Object>> executeQueryAsList(String query, Object... params);

	public <T> T findRecord(final JKFinder finder, final String tableName, final Object recordId);

	<T> T findRecord(JKFinder finder);

	Date getSystemDate();

	<T> List<T> getList(JKFinder finder);

	<T> List<T> getList(JKFinder finder, String key);

	Object[] executeQueryAsArray(String query, Object... params);

	<T> List<T> executeQueryAsObjectList(Class<T> clas,String instanceProperyNames, String query, Object... params);

	<T> T executeQueryAsSingleObject(Class<T> class1, String instanceProperyNames,String query, Object... params);


}
