package com.jk.db.dataaccess.orm;

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

	private EntityManager getEntityManager() {
		EntityManager manager = JKDataSourceFactory.getDataSource().createEntityManager();
		return manager;
	}

}
