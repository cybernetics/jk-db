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

import com.jk.db.dataaccess.JKDefaultDataAccess;
import com.jk.db.dataaccess.exception.JKDataAccessException;
import com.jk.db.dataaccess.exception.JKRecordNotFoundException;
import com.jk.db.dataaccess.plain.JKFinder;
import com.jk.db.dataaccess.plain.JKUpdater;
import com.jk.util.CollectionsUtil;

/**
 * The Class JKDaoTest.
 *
 * @author Jalal Kiswani
 */
public class JKDataAccessTest {

	/**
	 * Find employee.
	 *
	 * @return the employee
	 */
	private static Employee findEmployee() {
		final JKDefaultDataAccess dao = new JKDefaultDataAccess();
		final Employee emp = (Employee) dao.findRecord(new JKFinder() {

			@Override
			public String getQuery() {
				return "SELECT * FROM employees WHERE id=?";
			}

			@Override
			public Employee populate(final ResultSet rs) throws SQLException, JKRecordNotFoundException, JKDataAccessException {
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
		// printTableRows();
		insertObject();
	}

	private static void insertObject() {
		JKDefaultDataAccess dataAccess = new JKDefaultDataAccess();
		Employee emp = new Employee();
		emp.setId(50);
		emp.setName("Jalal Kiswani");
		emp.setSalary(1110);
		dataAccess.insert(emp);
	}

	/**
	 * Prints the table rows.
	 */
	private static void printTableRows() {
		final JKDefaultDataAccess dao = new JKDefaultDataAccess();
		final String rows = dao.executeQueryAsString("SELECT * FROM employees");
		System.out.println(rows);
	}

	/**
	 * Tes lst.
	 */
	private static void tesLst() {
		final List list = JKDataAccessTest.testGetAll();
		System.out.println(CollectionsUtil.toString(list));
	}

	/**
	 * Test execute query.
	 */
	private static void testExecuteQuery() {
		final JKDefaultDataAccess dao = new JKDefaultDataAccess();
		dao.executeUpdate("UPDATE employees SET salary=salary+5");
	}

	/**
	 * Test get all.
	 *
	 * @return the list
	 */
	private static List testGetAll() {
		final JKDefaultDataAccess dao = new JKDefaultDataAccess();
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
		final JKDefaultDataAccess dao = new JKDefaultDataAccess();
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
		final JKDefaultDataAccess dao = new JKDefaultDataAccess();
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
		final JKDefaultDataAccess dao = new JKDefaultDataAccess();
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
		final JKDefaultDataAccess dao = new JKDefaultDataAccess();
		final Object max = dao.exeuteSingleOutputQuery("SELECT MAX(salary) FROM employees");
		System.out.println(max);
	}
}
