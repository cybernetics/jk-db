package com.jk.db.example;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jk.db.Finder;
import com.jk.db.JKDefaultDao;
import com.jk.db.Updater;
import com.jk.db.exception.JKDaoException;
import com.jk.db.exception.JKRecordNotFoundException;

public class JKDaoTest {
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		JKDefaultDao dao=new JKDefaultDao();
		final int id=testInsert(dao);
		
		String name = testFind(dao, id);
		System.out.println(id+","+name);
		
	}

	/**
	 * 
	 * @param dao
	 * @param id
	 * @return
	 */
	private static String testFind(JKDefaultDao dao, final int id) {
		String name=(String) dao.findRecord(new Finder() {
			
			@Override
			public void setParamters(PreparedStatement ps) throws SQLException {
				ps.setInt(1, id);
			}
			
			@Override
			public Object populate(ResultSet rs) throws SQLException, JKRecordNotFoundException, JKDaoException {
				return rs.getString(1);
			}
			
			@Override
			public String getFinderSql() {
				return "SELECT name FROM employees WHERE id=?";
			}
		});
		return name;
	}

	/**
	 * 
	 * @param dao
	 * @return
	 */
	private static int testInsert(JKDefaultDao dao) {
		return dao.executeUpdate(new Updater() {
			
			@Override
			public void setParamters(PreparedStatement ps) throws SQLException, JKDaoException {
				ps.setString(1, "Jalal");
				ps.setDouble(2, 100);
			}
			
			@Override
			public String getUpdateSql() {
				return "INSERT INTO employees (name,salary) VALUES(?,?)";
			}
		});
	}
}
