package com.jk.db.dataaccess.orm;

import java.util.List;
import java.util.Map;

import com.jk.db.dataaccess.JKDataAccess;

public interface JKOrmDataAccess extends JKDataAccess{
	public void insert(Object object);

	public void update(Object object);

	public <T> T find(Object id);

	public void delete(Object object);

	public void delete(Object id, Class<?> type);

	public <T> List<T> getList(Map<String, Object> paramters);

	public <T> List<T> getAll(Class<T> clas);

}
