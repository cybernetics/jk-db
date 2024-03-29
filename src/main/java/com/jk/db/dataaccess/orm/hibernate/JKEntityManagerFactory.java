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
package com.jk.db.dataaccess.orm.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

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
	public static EntityManagerFactory createEntityManagerFactory(PersistenceUnitInfo info) {
		

		// Map<String, Object> integrationSettings = new HashMap<>();
		// integrationSettings.put(AvailableSettings.INTERCEPTOR, new
		// JKCustomSessionFactoryInterceptor());

		PersistenceUnitInfoDescriptor puDescriptor = new PersistenceUnitInfoDescriptor(info);
		EntityManagerFactoryBuilderImpl entityManagerFactoryBuilder = new EntityManagerFactoryBuilderImpl(puDescriptor, null);

		EntityManagerFactory emf = entityManagerFactoryBuilder.build();
		return emf;
	}

}
