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

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

import com.jk.db.JKDbConstants;
import com.jk.db.exception.JKDaoException;
import com.jk.util.IOUtil;

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
	public static JKDataSource getDefaultDataSource() {
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
	}

	/**
	 * Sets the default data source.
	 *
	 * @param impl
	 *            the new default data source
	 * @throws JKDaoException
	 *             the JK dao exception
	 */
	public static void setDefaultDataSource(final JKDataSource impl) {
		JKDataSourceFactory.defaultResourceManager = impl;
	}

	/**
	 * Try load config.
	 */
	protected static void tryLoadConfig() {
		final String configFileName = JKDataSourceFactory.getConfigFileName();
		final File file = new File(configFileName);
		if (file.exists()) {
			JKDataSourceFactory.logger.info("Loading exists file :".concat(JKDataSourceFactory.getConfigFileName()));
			final Properties prop = IOUtil.readPropertiesFile(file);
			JKDataSourceFactory.logger.info("constructing datasource");
			JKDataSourceFactory.defaultResourceManager = JKDataSourceFactory.createInstance(prop);
		} else {

		}
	}
}
