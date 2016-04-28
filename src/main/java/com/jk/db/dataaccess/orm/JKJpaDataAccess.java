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
package com.jk.db.dataaccess.orm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.cfgxml.spi.LoadedConfig;
import org.hibernate.boot.internal.SessionFactoryBuilderImpl;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import com.jk.db.dataaccess.exception.JKDataAccessManyRowsException;
import com.jk.db.dataaccess.orm.meta.JKColumnWrapper;
import com.jk.db.dataaccess.orm.meta.JKSortInfo;
import com.jk.db.datasource.JKDataSourceFactory;

public class JKJpaDataAccess implements JKOrmDataAccess {

	@Override
	public void insert(Object object) {
		EntityManager manager = getEntityManager();
		boolean commit = false;
		try {
			manager.persist(object);
			commit = true;
		} finally {
			close(manager, commit);
		}
	}

	@Override
	public void update(Object object) {
		EntityManager manager = getEntityManager();
		boolean commit = false;
		try {
			manager.merge(object);
			commit = true;
		} finally {
			close(manager, commit);
		}
	}

	@Override
	public <T> T find(Class<T> clas, Object id) {
		EntityManager manager = getEntityManager();
		try {
			return manager.find(clas, id);
		} finally {
			JKDataSourceFactory.getDataSource().close(manager, true);
		}
	}

	@Override
	public void delete(Object object) {
		EntityManager manager = getEntityManager();
		boolean commit = false;
		try {
			object = manager.merge(object);
			manager.remove(object);
			commit = true;
		} finally {
			close(manager, commit);
		}
	}

	@Override
	public void delete(Object id, Class<?> type) {
		EntityManager manager = getEntityManager();
		boolean commit = false;
		try {
			Object object = manager.find(type, id);
			manager.remove(object);
			commit = true;
		} finally {
			close(manager, commit);
		}
	}

	@Override
	public <T> List<T> getList(Class<T> clas) {
		return getList(clas, Collections.EMPTY_MAP);
	}

	@Override
	public <T> List<T> getList(Class<T> clas, Map<String, Object> paramters) {
		EntityManager manager = getEntityManager();
		try {
			StringBuffer buf = new StringBuffer("SELECT c FROM ".concat(clas.getSimpleName()).concat(" c "));
			buf.append(" WHERE 1=1 ");
			Set<String> keySet = paramters.keySet();
			for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
				String paramName = (String) iterator.next();
				buf.append(String.format(" AND c.%s=?", paramters.get(paramName)));
			}

			return executeQuery(clas, buf.toString(), paramters);
		} finally {
			close(manager, true);
		}
	}

	public <T> List<T> executeQuery(final Class<T> clas, final String queryString, final Object... paramters) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createQuery(queryString);
			for (int i = 0; i < paramters.length; i++) {
				Object object = paramters[i];
				query.setParameter(i + 1, object);
			}
			List<T> resultList = query.getResultList();
			if (resultList == null) {
				return new Vector<>();
			}
			return resultList;
		} finally {
			close(em, true);
		}
	}

	private void close(EntityManager manager, boolean commit) {
		JKDataSourceFactory.getDataSource().close(manager, commit);
	}

	protected EntityManager getEntityManager() {
		EntityManager manager = JKDataSourceFactory.getDataSource().createEntityManager();
		return manager;
	}

	protected String getQueryOrder(final Class<? extends JKEntity> clas)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method method = clas.getMethod("getSortInfo", Class.class);
		JKSortInfo info = (JKSortInfo) method.invoke(null, clas);
		StringBuffer buf2 = new StringBuffer();
		if (info != null) {
			buf2.append("ORDER BY ");
			JKColumnWrapper col = (JKColumnWrapper) info.column();
			buf2.append("c." + col.getFieldName());
			buf2.append(" " + info.sortOrder().toString());
		}
		return buf2.toString();
	}

	public <T> T findSingleEntity(Class<T> clas, final String queryString, final Object... paramters) {
		List<T> list = executeQuery(clas, queryString, paramters);
		if (list.size() == 0) {
			return null;
		}
		if (list.size() == 1) {
			return list.get(0);
		}
		throw new JKDataAccessManyRowsException(queryString, paramters);
	}
	
	// ///////////////////////////////////////////////////////////////////////////////////////
	public <T extends JKEntity> T getFirstRecord(Class<T> clas) {
		List<T> all = getList(clas);
		if (all.size() > 0) {
			return all.get(0);
		}
		return null;
	}

}
