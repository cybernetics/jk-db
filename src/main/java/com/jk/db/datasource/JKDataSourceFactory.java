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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import com.jk.context.JKContextFactory;
import com.jk.db.dataaccess.JKDefaultDataAccess;
import com.jk.db.dataaccess.exception.JKDataAccessException;
import com.jk.db.dataaccess.orm.JKDefaultOrmDataAccess;
import com.jk.db.dataaccess.orm.JKOrmDataAccess;
import com.jk.db.dataaccess.plain.JKDbConstants;
import com.jk.db.dataaccess.plain.JKPlainDataAccess;
import com.jk.exceptions.JKException;
import com.jk.resources.JKResourceLoader;
import com.jk.resources.JKResourceLoaderFactory;

/**
 * A factory for creating JKDataSource objects.
 *
 * @author Jalal Kiswani
 */
public class JKDataSourceFactory {

	/** The logger. */
	static Logger logger = Logger.getLogger(JKDataSourceFactory.class.getName());

	/** The default resource manager. */
	private static JKDataSource defaultResourceManager;

	/**
	 * Creates a new JKDataSource object.
	 *
	 * @param prop
	 *            the prop
	 * @return the JK pooling data source
	 */
	protected static JKPoolingDataSource createInstance(final Properties prop) {
		return new JKPoolingDataSource(prop);
	}

	/**
	 * Gets the config file name.
	 *
	 * @return the config file name
	 */
	protected static String getConfigFileName() {
		return System.getProperty(JKDbConstants.DEFAULT_CONFIG_FILE_NAME_KEY, JKDbConstants.DEFAULT_CONFIG_FILE_NAME);
	}

	/**
	 * Gets the default data source.
	 *
	 * @return the default data source
	 */
	public static JKDataSource getDataSource() {
		try {
			JKDataSourceFactory.logger.info("get default datasource");
			if (JKDataSourceFactory.defaultResourceManager == null) {
				JKDataSourceFactory.logger.info("trying to load config file");
				JKDataSourceFactory.tryLoadConfig();
				if (JKDataSourceFactory.defaultResourceManager == null) {
					JKDataSourceFactory.logger.info("no configuration file is provided , defaults will be used");
					JKDataSourceFactory.defaultResourceManager = JKDataSourceFactory.createInstance(new Properties());
				}
			}
			return JKDataSourceFactory.defaultResourceManager;
		} catch (final IOException e) {
			throw new JKException(e);
		}
	}

	/**
	 * Sets the default data source.
	 *
	 * @param impl
	 *            the new default data source
	 * @throws JKDataAccessException
	 *             the JK dao exception
	 */
	public static void setDefaultDataSource(final JKDataSource impl) {
		JKDataSourceFactory.defaultResourceManager = impl;
	}

	/**
	 * Try load config.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected static void tryLoadConfig() throws IOException {
		String configFileName = JKDataSourceFactory.getConfigFileName();
		final String configPath = JKContextFactory.getCurrentContext().getConfigPath();

		final JKResourceLoader resourceLoader = JKResourceLoaderFactory.getResourceLoader();
		configFileName = configPath.concat(configFileName);
		final InputStream in = resourceLoader.getResourceAsStream(configFileName);
		if (in != null) {
			JKDataSourceFactory.logger.info("Loading exists file :".concat(configFileName));
			final Properties prop = new Properties();
			prop.load(in);
			JKDataSourceFactory.logger.info("constructing datasource");
			JKDataSourceFactory.defaultResourceManager = JKDataSourceFactory.createInstance(prop);
		}
	}

	public static JKPlainDataAccess getPlainDataAccess() {
		return new JKDefaultDataAccess();
	}

	public static JKOrmDataAccess getOrmDataAccess() {
		return new JKDefaultOrmDataAccess();
	}

}
