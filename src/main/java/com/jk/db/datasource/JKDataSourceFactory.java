/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [2016] [Jalal Kiswani]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
