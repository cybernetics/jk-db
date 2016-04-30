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
package com.jk.db.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.jpa.internal.EntityManagerFactoryImpl;

import com.jk.annotations.AnnotationDetector;
import com.jk.cache.JKCacheFactory;
import com.jk.cache.JKCacheManager;
import com.jk.db.dataaccess.orm.eclipselink.JKPersistenceUnitProperties;
import com.jk.db.dataaccess.orm.hibernate.JKEntityManagerFactory;
import com.jk.db.dataaccess.orm.hibernate.JKPersistenceUnitInfoImpl;
import com.jk.db.dataaccess.plain.JKDbConstants;

/**
 * The Class JKDefaultDataSource.
 *
 * @author Jalal Kiswani
 */
public class JKDefaultDataSource extends JKAbstractDataSource {

	/** The config. */
	private final Properties config;

	/** The driver name. */
	private String driverName;

	/** The db url. */
	private String dbUrl;

	/** The user name. */
	private String userName;

	/** The password. */
	private String password;

	private String entitiesPackages;

	/**
	 * Instantiates a new JK default data source.
	 *
	 * @param config
	 *            the config
	 */
	public JKDefaultDataSource(final Properties config) {
		this.config = config;
		init();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#getDatabaseUrl()
	 */
	@Override
	public String getDatabaseUrl() {
		return this.dbUrl;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#getDriverName()
	 */
	@Override
	public String getDriverName() {
		return this.driverName;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#getPassword()
	 */
	@Override
	public String getPassword() {
		return this.password;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#getProperties()
	 */
	@Override
	public Properties getProperties() {
		return this.config;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#getProperty(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String getProperty(final String name, final String defaultValue) {
		return this.config.getProperty(name, defaultValue);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#getTestQuery()
	 */
	@Override
	public String getTestQuery() {
		return this.config.getProperty("DB_TEST_QUERY", "SELECT version()");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jk.db.datasource.JKDataSource#getUsername()
	 */
	@Override
	public String getUsername() {
		return this.userName;
	}

	/**
	 * Inits the.
	 */
	protected void init() {
		this.driverName = this.config.getProperty(JKDbConstants.PROPERTY_DRIVER_NAME, JKDbConstants.DEFAULT_DB_DRIVER);
		this.dbUrl = this.config.getProperty(JKDbConstants.PROPERTY_DB_URL, JKDbConstants.DEFAULT_DB_URL);
		this.userName = this.config.getProperty(JKDbConstants.PROPERTY_DB_USER, JKDbConstants.DEFAULT_DB_USER);
		this.password = this.config.getProperty(JKDbConstants.PROPERTY_DB_PASSWORD, JKDbConstants.DEFAULT_DB_PASSWORD);
		this.entitiesPackages = this.config.getProperty(JKDbConstants.PROPERTY_DB_ENTITY_PACKAGES, JKDbConstants.DEFAULT_DB_ENTITY_PACKAGES);
	}

	@Override
	public EntityManagerFactory getEntityManagerFactory() {
		return getEntityManagerFactory(JKDbConstants.DEFAULT_PERSISINCE_UNIT_NAME);
	}

	@Override
	public EntityManagerFactory getEntityManagerFactory(String name) {
		EntityManagerFactory emf = getCache().get(name, EntityManagerFactory.class);
		if (emf == null) {
			Properties prop = new Properties();
			prop.setProperty(JKPersistenceUnitProperties.JDBC_DRIVER, getDriverName());
			prop.setProperty(JKPersistenceUnitProperties.JDBC_PASSWORD, getPassword());
			prop.setProperty(JKPersistenceUnitProperties.JDBC_URL, getDatabaseUrl());
			prop.setProperty(JKPersistenceUnitProperties.JDBC_USER, getUsername());
			PersistenceUnitInfo persisitnceUnitInfo = getPersisitnceUnitInfo(name, prop, getEntitiesPackages());
			emf = JKEntityManagerFactory.createEntityManagerFactory(persisitnceUnitInfo);
			getCache().cache(name, emf);
		}
		return emf;
	}

	protected JKCacheManager getCache() {
		return JKCacheFactory.getCacheManager();
	}

	@Override
	public PersistenceUnitInfo getPersisitnceUnitInfo(String persisitnceUnitName, Properties prop, String entitiesPackages) {
		PersistenceUnitInfo info = getCache().get(persisitnceUnitName, PersistenceUnitInfo.class);
		if (info == null) {
			List<String> entityClassNames = AnnotationDetector.scanAsList(Entity.class, entitiesPackages.split(","));
			info = new JKPersistenceUnitInfoImpl(persisitnceUnitName, entityClassNames, prop);
			getCache().cache(persisitnceUnitName, info);
		}
		return info;
	}

	@Override
	public EntityManager createEntityManager(String emfName) {
		return getEntityManagerFactory(emfName).createEntityManager();
	}

	@Override
	public EntityManager createEntityManager() {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		em.getTransaction().begin();
		;
		return em;

	}

	@Override
	public void close(EntityManager em, boolean commit) {
		if (em != null) {
			if (commit && !em.getTransaction().getRollbackOnly()) {
				em.getTransaction().commit();
			} else {
				em.getTransaction().rollback();
			}
			em.close();
		}
	}

	@Override
	public String getEntitiesPackages() {
		return entitiesPackages;
	}
}
