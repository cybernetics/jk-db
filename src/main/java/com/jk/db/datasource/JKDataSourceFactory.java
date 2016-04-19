package com.jk.db.datasource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import com.jk.db.JKDbConstants;
import com.jk.db.exception.JKDaoException;
import com.jk.util.IOUtil;

public class JKDataSourceFactory {
	static Logger logger = Logger.getLogger(JKDataSourceFactory.class.getName());

	private static JKDataSource defaultResourceManager;

	/**
	 * @return
	 */
	public static JKDataSource getDefaultDataSource() {
		logger.info("get default datasource");
		if (defaultResourceManager == null) {
			logger.info("trying to load config file");
			tryLoadConfig();
			if (defaultResourceManager == null) {
				throw new JKDaoException("Please set default datasource or provide config file");
			}
		}
		return defaultResourceManager;
	}

	/**
	 * 
	 */
	protected static void tryLoadConfig() {
		String configFileName = getConfigFileName();
		File file = new File(configFileName);
		if (file.exists()) {
			logger.info("Loading exists file :".concat(getConfigFileName()));
			Properties prop = IOUtil.readPropertiesFile(file);
			logger.info("constructing datasource");
			defaultResourceManager = new JKPoolingDataSource(prop);
		}
	}

	/**
	 * 
	 * @return
	 */
	protected static String getConfigFileName() {
		return System.getProperty(JKDbConstants.DEFAULT_CONFIG_FILE_NAME_KEY, JKDbConstants.DEFAULT_CONFIG_FILE_NAME);
	}

	/**
	 * 
	 * @param impl
	 * @throws ServerDownException
	 * @throws JKDaoException
	 */
	public static void setDefaultDataSource(JKDataSource impl) {
		defaultResourceManager = impl;
	}
}
