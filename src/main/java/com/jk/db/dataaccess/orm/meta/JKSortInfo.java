package com.jk.db.dataaccess.orm.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.Column;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)

public @interface JKSortInfo {
	public JKSortDirection sortOrder() default JKSortDirection.ASC;
	public Column column() default @Column;
}
