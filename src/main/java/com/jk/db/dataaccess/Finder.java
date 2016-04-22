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
package com.jk.db.dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jk.db.dataaccess.exception.JKDaoException;
import com.jk.db.dataaccess.exception.JKRecordNotFoundException;

/**
 * The Interface Finder.
 *
 * @author Jalal Kiswani
 */
public interface Finder extends JKDBOperation {

	/**
	 * Gets the finder sql.
	 *
	 * @return the finder sql
	 */
	public String getFinderSql();

	/**
	 * Populate.
	 *
	 * @param <T>
	 *            the generic type
	 * @param rs
	 *            the rs
	 * @return the t
	 * @throws SQLException
	 *             the SQL exception
	 * @throws JKRecordNotFoundException
	 *             the JK record not found exception
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	public <T> T populate(ResultSet rs) throws SQLException ;

	/**
	 * Sets the paramters.
	 *
	 * @param ps
	 *            the new paramters
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void setParamters(PreparedStatement ps) throws SQLException;
}
