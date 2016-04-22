package com.jk.db.dataaccess.orm.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;

import com.jk.annotations.AnnotationDetector;

public class JKEntityManagerFactory {

	/**
	 * TODO : cache entity manager
	 * 
	 * @return
	 */
	public static EntityManagerFactory createEntityManagerFactory(String persisitnceUnitName, Properties prop, String entitiesPackages) {
		List<String> entityClassNames = AnnotationDetector.scanAsList(Entity.class, entitiesPackages.split(","));

		JKPersistenceUnitInfoImpl persistenceUnitInfo = new JKPersistenceUnitInfoImpl(persisitnceUnitName, entityClassNames, prop);

		// Map<String, Object> integrationSettings = new HashMap<>();
		// integrationSettings.put(AvailableSettings.INTERCEPTOR, new
		// JKCustomSessionFactoryInterceptor());

		PersistenceUnitInfoDescriptor puDescriptor = new PersistenceUnitInfoDescriptor(persistenceUnitInfo);
		EntityManagerFactoryBuilderImpl entityManagerFactoryBuilder = new EntityManagerFactoryBuilderImpl(puDescriptor, null);

		EntityManagerFactory emf = entityManagerFactoryBuilder.build();
		return emf;
	}
}
