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

import java.util.List;
import java.util.Map;

import com.jk.db.dataaccess.orm.JKJpaDataAccess;
import com.jk.db.dataaccess.orm.JKOrmDataAccess;
import com.jk.db.dataaccess.plain.JKAbstractPlainDataAccess;
import com.jk.db.datasource.JKDataSource;
import com.jk.db.datasource.JKSession;

/**
 * The Class JKDefaultDao.
 *
 * @author Jalal Kiswani
 */
public class JKDefaultDataAccess extends JKAbstractPlainDataAccess implements JKOrmDataAccess{
	JKJpaDataAccess jpaDataAccess=new JKJpaDataAccess();
	
//	/**
//	 * Instantiates a new JK default dao.
//	 */
//	public JKDefaultDataAccess() {
//		super();
//	}

//	/**
//	 * Instantiates a new JK default dao.
//	 *
//	 * @param dataSource
//	 *            the data source
//	 */
//	public JKDefaultDataAccess(final JKDataSource dataSource) {
//		super(dataSource);
//		ormDataAccess=new 				
//	}

//	/**
//	 * Instantiates a new JK default dao.
//	 *
//	 * @param session
//	 *            the session
//	 */
//	public JKDefaultDataAccess(final JKSession session) {
//		super(session);
//	}

	@Override
	public void insert(Object object) {
		jpaDataAccess.insert(object);
	}

	@Override
	public void update(Object object) {
		jpaDataAccess.update(object);
	}

	@Override
	public void delete(Object object) {
		jpaDataAccess.delete(object);
	}

	@Override
	public void delete(Object id, Class<?> type) {
		jpaDataAccess.delete(id,type);
	}

	@Override
	public <T> T find(Class<T> clas, Object id) {
		return jpaDataAccess.find(clas, id);
	}

	@Override
	public <T> List<T> getList(Class<T> clas) {
		return jpaDataAccess.getList(clas);
	}

	@Override
	public <T> List<T> getList(Class<T> clas, Map<String, Object> paramters) {
		return jpaDataAccess.getList(clas, paramters);
	}


}
