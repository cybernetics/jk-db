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
import com.jk.util.CollectionsUtil;
import com.jk.util.StringUtil;

public class JKDaoTest {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

	}

	private static void testGetSystemDate() {
		JKDefaultDao dao = new JKDefaultDao();
		System.out.println(dao.getSystemDate());
	}

	private static void testQueryResultsAsArray() {
		JKDefaultDao dao = new JKDefaultDao();
		Object[] allRows = dao.exeuteQueryAsArray("SELECT * FROM employees");
		for (Object record : allRows) {
			Object[] row = (Object[]) record;
			System.out.println(String.format("Id: %s , Name: %s, Salary: %s", row[0], row[1], row[2]));
		}
	}

	private static void testSingleOuputQuery() {
		JKDefaultDao dao = new JKDefaultDao();
		Object max = dao.exeuteSingleOutputQuery("SELECT MAX(salary) FROM employees");
		System.out.println(max);
	}

	private static void testExecuteQuery() {
		JKDefaultDao dao = new JKDefaultDao();
		dao.executeUpdate("UPDATE employees SET salary=salary+5");
	}

	private static void printTableRows() {
		JKDefaultDao dao = new JKDefaultDao();
		String rows = dao.executeOutputQuery("SELECT * FROM employees", ",", "\n");
		System.out.println(rows);
	}

	private static void tesLst() {
		List list = testGetAll();
		System.out.println(StringUtil.toString(list));
	}

	/**
	 * 
	 * @return
	 */
	private static List testGetAll() {
		JKDefaultDao dao = new JKDefaultDao();
		return dao.lstRecords(new Finder() {

			@Override
			public void setParamters(PreparedStatement ps) throws SQLException {
			}

			@Override
			public Employee populate(ResultSet rs) throws SQLException, JKRecordNotFoundException, JKDaoException {
				Employee emp = new Employee();
				emp.setId(rs.getInt("id"));
				emp.setName(rs.getString("name"));
				emp.setSalary(rs.getDouble("salary"));
				return emp;
			}

			@Override
			public String getFinderSql() {
				return "SELECT * FROM employees";
			}
		});
	}

	/**
	 * 
	 * @param dao
	 * @param id
	 * @return
	 */
	private static Employee findEmployee() {
		JKDefaultDao dao = new JKDefaultDao();
		Employee emp = (Employee) dao.findRecord(new Finder() {

			@Override
			public void setParamters(PreparedStatement ps) throws SQLException {
				ps.setInt(1, 2);
			}

			@Override
			public Employee populate(ResultSet rs) throws SQLException, JKRecordNotFoundException, JKDaoException {
				Employee emp = new Employee();
				emp.setId(rs.getInt("id"));
				emp.setName(rs.getString("name"));
				emp.setSalary(rs.getDouble("salary"));
				return emp;
			}

			@Override
			public String getFinderSql() {
				return "SELECT * FROM employees WHERE id=?";
			}
		});
		return emp;
	}

	/**
	 * 
	 * @param dao
	 * @return
	 */
	private static int testInsert(final Employee emp) {
		JKDefaultDao dao = new JKDefaultDao();
		return dao.executeUpdate(new Updater() {

			@Override
			public void setParamters(PreparedStatement ps) throws SQLException {
				int counter=1;
				ps.setString(counter++, emp.getName());
				ps.setDouble(counter++, emp.getSalary());
			}

			@Override
			public String getUpdateSql() {
				return "INSERT INTO employees (name,salary) VALUES(?,?)";
			}
		});
	}
}

class Employee {
	int id;
	String name;
	double salary;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

}
