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
package com.jk.db.example;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.jk.db.dataaccess.JKDefaultDao;
import com.jk.db.dataaccess.JKFinder;
import com.jk.db.dataaccess.JKUpdater;
import com.jk.db.dataaccess.exception.JKDaoException;
import com.jk.db.dataaccess.exception.JKRecordNotFoundException;
import com.jk.util.CollectionsUtil;

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
		final Employee emp = (Employee) dao.findRecord(new JKFinder() {

			@Override
			public String getQuery() {
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
		printTableRows();
	}

	/**
	 * Prints the table rows.
	 */
	private static void printTableRows() {
		final JKDefaultDao dao = new JKDefaultDao();
		final String rows = dao.executeQueryAsString("SELECT * FROM employees");
		System.out.println(rows);
	}

	/**
	 * Tes lst.
	 */
	private static void tesLst() {
		final List list = JKDaoTest.testGetAll();
		System.out.println(CollectionsUtil.toString(list));
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
		return dao.lstRecords(new JKFinder() {

			@Override
			public String getQuery() {
				return "SELECT * FROM employees";
			}

			@Override
			public Employee populate(final ResultSet rs) throws SQLException {
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
		return dao.executeUpdate(new JKUpdater() {

			@Override
			public String getQuery() {
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
