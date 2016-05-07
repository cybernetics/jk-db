package com.jk.db.dataaccess.orm.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Id;

public class JKColumnWrapper implements Column {

	private final Column original;
	private final Field field;
	private JKSortInfo sortInfo;
	private Id id;

	// //////////////////////////////////////////////////
	public JKColumnWrapper(Field field, Column original) {
		this.field = field;
		this.original = original;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return original.annotationType();
	}

	@Override
	public String columnDefinition() {
		return original.columnDefinition();
	}

	@Override
	public boolean insertable() {
		return original.insertable();
	}

	@Override
	public int length() {
		return original.length();
	}

	@Override
	public String name() {
		if (original.name() == null || original.name().equals("")) {
			return field.getName();
		}
		return original.name();
	}

	@Override
	public boolean nullable() {
		return original.nullable();
	}

	@Override
	public int precision() {
		return original.precision();
	}

	@Override
	public int scale() {
		return original.precision();
	}

	@Override
	public String table() {
		return original.table();
	}

	@Override
	public boolean unique() {
		return original.unique();
	}

	@Override
	public boolean updatable() {
		return original.updatable();
	}

	@Override
	public String toString() {
		return original.toString();
	}

	public String getFieldName() {
		return field.getName();
	}

	public void setSortInfo(JKSortInfo sortInfo) {
		this.sortInfo = sortInfo;
	}

	public JKSortInfo getSortInfo() {
		return sortInfo;
	}

	public void setId(Id id) {
		this.id = id;
	}

	public Column getOriginal() {
		return original;
	}

	public Field getField() {
		return field;
	}

	public Id getId() {
		return id;
	}

	public boolean isId() {
		return getId()!=null;
	}

}
