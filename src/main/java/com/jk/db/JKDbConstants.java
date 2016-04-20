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
package com.jk.db;

/**
 * The Class JKDbConstants.
 *
 * @author Jalal Kiswani
 */
public class JKDbConstants {

	/** The Constant DEFAULT_CONFIG_FILE_NAME. */
	public static final String DEFAULT_CONFIG_FILE_NAME = "jk-db.properties";

	/** The Constant DEFAULT_CONFIG_FILE_NAME_KEY. */
	public static final String DEFAULT_CONFIG_FILE_NAME_KEY = "jk.db.config.file";

	/** The Constant PROPERTY_DRIVER_NAME. */
	public static final String PROPERTY_DRIVER_NAME = "db-driver-name";

	/** The Constant PROPERTY_DB_URL. */
	public static final String PROPERTY_DB_URL = "db-url";

	/** The Constant PROPERTY_DB_USER. */
	public static final String PROPERTY_DB_USER = "db-user";

	/** The Constant PROPERTY_DB_PASSWORD. */
	public static final String PROPERTY_DB_PASSWORD = "db-password";

	/** The Constant PROPERTY_QUERY_LIMIT. */
	public static final String PROPERTY_QUERY_LIMIT = "query-limit";

	/** The Constant PROPERTY_INITIAL_POOL_SIZE_KEY. */
	public static final String PROPERTY_INITIAL_POOL_SIZE_KEY = "db-initial-size";

	/** The Constant PROPERTY_MAX_POOL_SIZE_KEY. */
	public static final String PROPERTY_MAX_POOL_SIZE_KEY = "db-max-total";

	/** The Constant DEFAULT_LIMIT. */
	public static final int DEFAULT_LIMIT = 100;

	/** The Constant DEFAULT_POOL_INITIAL_SIZE. */
	public static final String DEFAULT_POOL_INITIAL_SIZE = "2";

	/** The Constant DEFAULT_POOL_MAX_SIZE. */
	public static final String DEFAULT_POOL_MAX_SIZE = "10";

	/** The Constant DEFAULT_DB_DRIVER. */
	public static final String DEFAULT_DB_DRIVER = "com.mysql.jdbc.Driver";

	/** The Constant DEFAULT_DB_URL. */
	public static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/app";

	/** The Constant DEFAULT_DB_USER. */
	public static final String DEFAULT_DB_USER = "root";

	/** The Constant DEFAULT_DB_PASSWORD. */
	public static final String DEFAULT_DB_PASSWORD = "123456";

}
