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

import java.util.List;

import javax.sql.rowset.CachedRowSet;

import com.jk.db.dataaccess.JKDataAccess;
import com.jk.db.dataaccess.exception.JKDataAccessException;

/**
 * The Interface JKDataAccessObject.
 *
 * @author Jalal Kiswani
 */
public interface JKPlainDataAccess extends JKDataAccess{

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
	CachedRowSet executeQueryAsCachedRowSet(String query, Object... params) throws JKDataAccessException;

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
	List<JKDbIdValue> executeQueryAsIdValue(String sql, Object... params) throws JKDataAccessException;

	/**
	 * Execute update.
	 *
	 * @param updater
	 *            the updater
	 * @return the int
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	int executeUpdate(JKUpdater updater) throws JKDataAccessException;

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
	int executeUpdate(String sql, Object... params) throws JKDataAccessException;

	/**
	 * Exeute query as array.
	 *
	 * @param query
	 *            the query
	 * @return the object[]
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	Object[] exeuteQueryAsArray(String query) throws JKDataAccessException;

	/**
	 * Exeute query as list.
	 *
	 * @param query
	 *            the query
	 * @param params
	 *            the params
	 * @return the list
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	List exeuteQueryAsList(String query, Object... params) throws JKDataAccessException;

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
	Object exeuteSingleOutputQuery(String query, Object... params) throws JKDataAccessException;

	/**
	 * Find record.
	 *
	 * @param finder
	 *            the finder
	 * @return the object
	 */
	Object findRecord(JKFinder finder);

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
	int getNextId(String tableName, String fieldName) throws JKDataAccessException;

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
	int getRowsCount(String query) throws NumberFormatException, JKDataAccessException;

	/**
	 * Lst records.
	 *
	 * @param finder
	 *            the finder
	 * @return the list
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	List lstRecords(JKFinder finder) throws JKDataAccessException;

}
