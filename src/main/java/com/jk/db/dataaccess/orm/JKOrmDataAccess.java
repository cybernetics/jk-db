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
package com.jk.db.dataaccess.orm;

import java.util.List;
import java.util.Map;

import com.jk.db.dataaccess.JKDataAccess;

public interface JKOrmDataAccess extends JKDataAccess{
	public void insert(Object object);

	public void update(Object object);

	public void delete(Object object);

	public void delete(Object id, Class<?> type);

	public <T> T find(Class<T> clas, Object id);

	public <T> List<T> getList(Class<T> clas);

	public <T> List<T> getList(Class<T> clas, Map<String, Object> paramters);

}
