package com.jk.db.datasource;

/**
 * 
 * @author Jalal
 *
 */
public class JKDataSourceUtil {
	/**
	 * 
	 * @param manager
	 * @return
	 */
	public static boolean isOracle(JKDataSource manager) {
		return manager.getDatabaseUrl().toLowerCase().contains("oracle");
	}
}
