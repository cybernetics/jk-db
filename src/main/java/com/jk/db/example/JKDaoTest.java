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
package com.jk.db.example;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.jk.db.Finder;
import com.jk.db.JKDefaultDao;
import com.jk.db.Updater;
import com.jk.db.exception.JKDaoException;
import com.jk.db.exception.JKRecordNotFoundException;
import com.jk.util.StringUtil;


/**
 * The Class JKDaoTest.
 *
 * @author Jalal Kiswani
 */
public class JKDaoTest {

	/**
	 * Find employee.
	 *
	 * @return the employee
	 */
	private static Employee findEmployee() {
		final JKDefaultDao dao = new JKDefaultDao();
		final Employee emp = (Employee) dao.findRecord(new Finder() {

			@Override
			public String getFinderSql() {
				return "SELECT * FROM employees WHERE id=?";
			}

			@Override
			public Employee populate(final ResultSet rs)
					throws SQLException, JKRecordNotFoundException, JKDaoException {
				final Employee emp = new Employee();
				emp.setId(rs.getInt("id"));
				emp.setName(rs.getString("name"));
				emp.setSalary(rs.getDouble("salary"));
				return emp;
			}

			@Override
			public void setParamters(final PreparedStatement ps) throws SQLException {
				ps.setInt(1, 2);
			}
		});
		return emp;
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {

	}

	/**
	 * Prints the table rows.
	 */
	private static void printTableRows() {
		final JKDefaultDao dao = new JKDefaultDao();
		final String rows = dao.executeOutputQuery("SELECT * FROM employees", ",", "\n");
		System.out.println(rows);
	}

	/**
	 * Tes lst.
	 */
	private static void tesLst() {
		final List list = JKDaoTest.testGetAll();
		System.out.println(StringUtil.toString(list));
	}

	/**
	 * Test execute query.
	 */
	private static void testExecuteQuery() {
		final JKDefaultDao dao = new JKDefaultDao();
		dao.executeUpdate("UPDATE employees SET salary=salary+5");
	}

	/**
	 * Test get all.
	 *
	 * @return the list
	 */
	private static List testGetAll() {
		final JKDefaultDao dao = new JKDefaultDao();
		return dao.lstRecords(new Finder() {

			@Override
			public String getFinderSql() {
				return "SELECT * FROM employees";
			}

			@Override
			public Employee populate(final ResultSet rs)
					throws SQLException, JKRecordNotFoundException, JKDaoException {
				final Employee emp = new Employee();
				emp.setId(rs.getInt("id"));
				emp.setName(rs.getString("name"));
				emp.setSalary(rs.getDouble("salary"));
				return emp;
			}

			@Override
			public void setParamters(final PreparedStatement ps) throws SQLException {
			}
		});
	}

	/**
	 * Test get system date.
	 */
	private static void testGetSystemDate() {
		final JKDefaultDao dao = new JKDefaultDao();
		System.out.println(dao.getSystemDate());
	}

	/**
	 * Test insert.
	 *
	 * @param emp
	 *            the emp
	 * @return the int
	 */
	private static int testInsert(final Employee emp) {
		final JKDefaultDao dao = new JKDefaultDao();
		return dao.executeUpdate(new Updater() {

			@Override
			public String getUpdateSql() {
				return "INSERT INTO employees (name,salary) VALUES(?,?)";
			}

			@Override
			public void setParamters(final PreparedStatement ps) throws SQLException {
				int counter = 1;
				ps.setString(counter++, emp.getName());
				ps.setDouble(counter++, emp.getSalary());
			}
		});
	}

	/**
	 * Test query results as array.
	 */
	private static void testQueryResultsAsArray() {
		final JKDefaultDao dao = new JKDefaultDao();
		final Object[] allRows = dao.exeuteQueryAsArray("SELECT * FROM employees");
		for (final Object record : allRows) {
			final Object[] row = (Object[]) record;
			System.out.println(String.format("Id: %s , Name: %s, Salary: %s", row[0], row[1], row[2]));
		}
	}

	/**
	 * Test single ouput query.
	 */
	private static void testSingleOuputQuery() {
		final JKDefaultDao dao = new JKDefaultDao();
		final Object max = dao.exeuteSingleOutputQuery("SELECT MAX(salary) FROM employees");
		System.out.println(max);
	}
}
