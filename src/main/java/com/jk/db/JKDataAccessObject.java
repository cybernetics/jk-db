/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [2016] [Jalal Kiswani]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.jk.db;

import java.util.List;

import javax.sql.rowset.CachedRowSet;

import com.jk.db.exception.JKDaoException;

/**
 * The Interface JKDataAccessObject.
 *
 * @author Jalal Kiswani
 */
public interface JKDataAccessObject {

	/**
	 * Creates the records from sql.
	 *
	 * @param sql
	 *            the sql
	 * @return the list
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	List createRecordsFromSQL(String sql) throws JKDaoException;

	/**
	 * Execute query.
	 *
	 * @param query
	 *            the query
	 * @return the cached row set
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	CachedRowSet executeQuery(String query) throws JKDaoException;

	/**
	 * Execute update.
	 *
	 * @param sql
	 *            the sql
	 * @return the int
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	int executeUpdate(String sql) throws JKDaoException;

	/**
	 * Execute update.
	 *
	 * @param updater
	 *            the updater
	 * @return the int
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	int executeUpdate(Updater updater) throws JKDaoException;

	/**
	 * Exeute query as array.
	 *
	 * @param query
	 *            the query
	 * @return the object[]
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	Object[] exeuteQueryAsArray(String query) throws JKDaoException;

	/**
	 * Exeute single output query.
	 *
	 * @param query
	 *            the query
	 * @return the object
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	Object exeuteSingleOutputQuery(String query) throws JKDaoException;

	/**
	 * Exeute single output query.
	 *
	 * @param query
	 *            the query
	 * @param params
	 *            the params
	 * @return the object
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	Object exeuteSingleOutputQuery(String query, Object... params) throws JKDaoException;

	/**
	 * Find record.
	 *
	 * @param finder
	 *            the finder
	 * @return the object
	 */
	Object findRecord(Finder finder);

	/**
	 * Gets the next id.
	 *
	 * @param tableName
	 *            the table name
	 * @param fieldName
	 *            the field name
	 * @return the next id
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	int getNextId(String tableName, String fieldName) throws JKDaoException;

	/**
	 * Gets the rows count.
	 *
	 * @param query
	 *            the query
	 * @return the rows count
	 * @throws NumberFormatException
	 *             the number format exception
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	int getRowsCount(String query) throws NumberFormatException, JKDaoException;

	/**
	 * Lst records.
	 *
	 * @param finder
	 *            the finder
	 * @return the list
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	List lstRecords(Finder finder) throws JKDaoException;

}
