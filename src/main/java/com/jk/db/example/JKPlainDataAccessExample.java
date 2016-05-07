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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.jk.db.dataaccess.JKDefaultDataAccess;
import com.jk.db.dataaccess.exception.JKDataAccessException;
import com.jk.db.dataaccess.exception.JKRecordNotFoundException;
import com.jk.db.dataaccess.plain.JKFinder;
import com.jk.db.dataaccess.plain.JKPlainDataAccess;
import com.jk.db.dataaccess.plain.JKUpdater;
import com.jk.db.datasource.JKDataSourceFactory;
import com.jk.util.CollectionsUtil;
import com.jk.util.ObjectUtil;

/**
 * The Class JKDaoTest.
 *
 * @author Jalal Kiswani
 */
public class JKPlainDataAccessExample {
	static Logger logger = Logger.getLogger(JKPlainDataAccessExample.class.getName());
	static JKPlainDataAccess dataAccess = JKDataSourceFactory.getPlainDataAccess();;
	/////////////////////////////////////////////////////////////
	static List<Employee> exampleRecords = new ArrayList();
	static {
		exampleRecords.add(new Employee(1, "Jalal", 100));
		exampleRecords.add(new Employee(2, "John", 200));
		exampleRecords.add(new Employee(3, "Kamal", 300));
		exampleRecords.add(new Employee(4, "Essa", 400));
		exampleRecords.add(new Employee(5, "Adam", 500));
		exampleRecords.add(new Employee(6, "Mark", 600));
		exampleRecords.add(new Employee(7, "James", 700));
		exampleRecords.add(new Employee(8, "Ayman", 800));
	}

	/////////////////////////////////////////////////////////////
	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		deleteTableOldRows();
		insertRecords();
		getAllRecordsFromDatabase();
		findRecordWithId();
		updateRecord();
		getAllRecordsFromDatabaseAsObjects();
	}

	private static void getAllRecordsFromDatabaseAsObjects() {
		logger.info("get all records as objects");

		String instanceVariables = "id,name,salary";
		String query = "SELECT * FROM employees ";
		List<Employee> list = dataAccess.executeQueryAsObjectList(Employee.class, instanceVariables, query);
		for (Employee employee : list) {
			System.out.println(ObjectUtil.toString(employee));	
		}
	}

	private static void updateRecord() {
		logger.info("update adam record with id 5 to 555 salary");
		dataAccess.execute("UPDATE employees SET salary=? WHERE id=?", 555, 5);
		findRecordWithId();
	}

	private static Employee findRecordWithId() {
		logger.info("find adam record with id 5");

		String instanceVariables = "id,name,salary";
		String query = "SELECT * FROM employees WHERE id=? ";
		Employee emp = dataAccess.executeQueryAsSingleObject(Employee.class, instanceVariables, query, 5);
		System.out.println(ObjectUtil.toString(emp));
		return emp;
	}

	private static void getAllRecordsFromDatabase() {
		logger.info("loading all records");
		String rows = dataAccess.executeQueryAsString("SELECT * FROM employees");
		System.out.println(rows);
	}

	private static void insertRecords() {
		for (Employee employee : exampleRecords) {
			dataAccess.execute("INSERT INTO employees(id,name,salary) VALUES(?,?,?)", employee.getId(), employee.getName(), employee.getSalary());
		}
	}

	private static void deleteTableOldRows() {
		logger.info("delete all records ");
		dataAccess.execute("DELETE FROM employees");
	}

}
