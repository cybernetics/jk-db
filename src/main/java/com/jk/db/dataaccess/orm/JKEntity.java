package com.jk.db.dataaccess.orm;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.jk.db.dataaccess.orm.meta.JKColumnWrapper;
import com.jk.db.dataaccess.orm.meta.JKSortDirection;
import com.jk.db.dataaccess.orm.meta.JKSortInfo;
import com.jk.util.ObjectUtil;

public class JKEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	String tableName;

	public JKEntity() {
	}

	public String getTableName() {
		Table annotation = getClass().getAnnotation(Table.class);
		return annotation.name();
	}

	public <T> T getIdValue() {
		return ObjectUtil.getPropertyValue(this, this.getIdColumn().getFieldName());
	}

	// ///////////////////////////////////////////////
	public Column getFirstNonIdColumn() {
		Field[] declaredFields = getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			if (field.getAnnotation(Id.class) == null) {
				Column column = field.getAnnotation(Column.class);
				if (column != null) {
					return new JKColumnWrapper(field, column);
				}
			}
		}
		return null;
	}

	// /////////////////////////////////////////////////////////////////
	public static List<JKColumnWrapper> getColumns(Class<? extends JKEntity> clas) {
		Field[] declaredFields = clas.getDeclaredFields();
		Vector<JKColumnWrapper> columns = new Vector<>();
		for (Field field : declaredFields) {
			Column column = field.getAnnotation(Column.class);
			Id id = field.getAnnotation(Id.class);
			JKSortInfo sortInfo = field.getAnnotation(JKSortInfo.class);

			if (column != null) {
				JKColumnWrapper wrapper = new JKColumnWrapper(field, column);
				wrapper.setId(id);
				wrapper.setSortInfo(addColumnToSortInfo(wrapper, sortInfo));
				columns.add(wrapper);
			}
		}
		return columns;
	}

	// ///////////////////////////////////////////////////
	private static JKSortInfo addColumnToSortInfo(final JKColumnWrapper wrapper, final JKSortInfo sortInfo) {
		if (sortInfo == null) {
			return null;
		}
		JKSortInfo info = new JKSortInfo() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return sortInfo.annotationType();
			}

			@Override
			public JKSortDirection sortOrder() {
				return sortInfo.sortOrder();
			}

			@Override
			public Column column() {
				return wrapper;
			}
		};
		return info;
	}

	// ///////////////////////////////////////////////////
	public JKColumnWrapper getIdColumn() {
		return getIdColumn(this.getClass());
	}

	// ///////////////////////////////////////////////////
	public static JKColumnWrapper getIdColumn(Class<? extends JKEntity> clas) {
		for (JKColumnWrapper col : getColumns(clas)) {
			if (col.isId()) {
				return col;
			}
		}
		return null;
	}

	// ///////////////////////////////////////////////
	public void printFieldsAnnotations() {
		Field[] declaredFields = getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			printFieldAnnotations(field);
		}
	}

	// ///////////////////////////////////////////////
	public static void printFieldAnnotations(Field field) {
		Annotation[] annotations = field.getAnnotations();
		for (Annotation annotation : annotations) {
			System.out.println(annotation);
		}
	}

	@Override
	public String toString() {
		List<JKColumnWrapper> columns = getColumns(getClass());
		StringBuffer buf = new StringBuffer();
		buf.append(getAliasName());
		buf.append("[");
		int i = 0;
		for (JKColumnWrapper column : columns) {
			if (!column.getFieldName().toLowerCase().contains("password")) {
				if (i++ > 0) {
					buf.append(",");
				}
				buf.append(column.name());
				buf.append("=");
				buf.append(ObjectUtil.getPropertyValue(this, column.getFieldName()));
			}
		}
		buf.append("]\n");
		return buf.toString();
	}

	// ///////////////////////////////////////////////
	public static JKSortInfo getSortInfo(Class<? extends JKEntity> clas) {
		for (JKColumnWrapper column : getColumns(clas)) {
			if (column.getSortInfo() != null) {
				return column.getSortInfo();
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof JKEntity)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		// TODO : fix this to support equality before persist the entity to db
		// since
		JKEntity that = (JKEntity) obj;
		if (this.getTableName().equals(that.getTableName())) {
			if (this.getIdValue() != null && this.getIdValue().equals(that.getIdValue())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hashcode = getIdValue().hashCode();
		return hashcode;
	}

	public String getAliasName() {
		return getClass().getSimpleName();
	}
}
