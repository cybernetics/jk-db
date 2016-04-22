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
