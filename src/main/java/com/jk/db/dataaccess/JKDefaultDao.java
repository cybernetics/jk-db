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
package com.jk.db.dataaccess;

import com.jk.db.datasource.JKDataSource;
import com.jk.db.datasource.JKSession;

/**
 * The Class JKDefaultDao.
 *
 * @author Jalal Kiswani
 */
public class JKDefaultDao extends JKAbstractDao {

	/**
	 * Instantiates a new JK default dao.
	 */
	public JKDefaultDao() {
		super();
	}

	/**
	 * Instantiates a new JK default dao.
	 *
	 * @param dataSource
	 *            the data source
	 */
	public JKDefaultDao(final JKDataSource dataSource) {
		super(dataSource);
	}

	/**
	 * Instantiates a new JK default dao.
	 *
	 * @param session
	 *            the session
	 */
	public JKDefaultDao(final JKSession session) {
		super(session);
	}

}
