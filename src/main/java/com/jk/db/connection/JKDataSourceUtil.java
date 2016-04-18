package com.jk.db.connection;

public class JKDataSourceUtil {
	public static boolean isOracle(JKDataSource manager) {
		return manager.getDatabaseUrl().toLowerCase().contains("oracle");
	}
}
