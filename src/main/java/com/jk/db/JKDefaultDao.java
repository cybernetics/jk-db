package com.jk.db;

import com.jk.db.connection.JKDataSource;

public class JKDefaultDao extends JKAbstractDao {

	public JKDefaultDao() {
		super();
	}

	public JKDefaultDao(JKDataSource dataSource) {
		super(dataSource);
	}

	public JKDefaultDao(JKSession session) {
		super(session);
	}



	

}
