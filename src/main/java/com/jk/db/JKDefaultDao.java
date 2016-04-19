package com.jk.db;

import com.jk.db.datasource.JKDataSource;
import com.jk.db.datasource.JKSession;

/**
 * 
 * @author Jalal
 *
 */
public class JKDefaultDao extends JKAbstractDao {

	/**
	 * 
	 */
	public JKDefaultDao() {
		super();
	}

	/**
	 * 
	 * @param dataSource
	 */
	public JKDefaultDao(JKDataSource dataSource) {
		super(dataSource);
	}

	/**
	 * 
	 * @param session
	 */
	public JKDefaultDao(JKSession session) {
		super(session);
	}

}
