package com.jk.db.dataaccess.orm;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

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
		EntityManager manager = JKDataSourceFactory.getDefaultDataSource().createEntityManager();
		boolean commit = false;
		try {
			manager.persist(object);
			commit = true;
		} finally {
			JKDataSourceFactory.getDefaultDataSource().close(manager, commit);
		}
	}

	@Override
	public void update(Object object) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T find(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Object object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Object id, Class<?> type) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> List<T> getList(Map<String, Object> paramters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> getAll(Class<T> clas) {
		// TODO Auto-generated method stub
		return null;
	}

}
