package com.jk.db.test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.jk.db.JKDefaultDao;
import com.jk.db.Updater;
import com.jk.db.exception.JKDaoException;

public class JKDaoTest {
	public static void main(String[] args) {
		JKDefaultDao dao=new JKDefaultDao();
		dao.executeUpdate(new Updater() {
			
			@Override
			public void setParamters(PreparedStatement ps) throws SQLException, JKDaoException {
				ps.setInt(1, 1);
				ps.setString(2, "Jalal");
				ps.setDouble(3, 100);
			}
			
			@Override
			public String getUpdateSql() {
				return "INSERT INTO employees VALUES(?,?,?)";
			}
		});
	}
}
