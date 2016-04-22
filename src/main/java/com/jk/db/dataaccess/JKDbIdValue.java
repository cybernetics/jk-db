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
package com.jk.db.dataaccess;

import java.io.Serializable;
import java.util.Vector;

/**
 * The Class JKDbIdValue.
 *
 * @author Jalal Kiswani
 */
public class JKDbIdValue implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 537896307464838186L;

	/**
	 * Creates the list.
	 *
	 * @param ids
	 *            the ids
	 * @param values
	 *            the values
	 * @return the vector
	 */
	public static Vector<JKDbIdValue> createList(final int[] ids, final Object[] values) {
		final Vector<JKDbIdValue> records = new Vector<JKDbIdValue>();
		for (int i = 0; i < ids.length; i++) {
			final JKDbIdValue rec = new JKDbIdValue();
			rec.setId(ids[i]);
			rec.setValue(values[i]);
			records.add(rec);
		}
		return records;
	}

	/**
	 * Creates the list.
	 *
	 * @param values
	 *            the values
	 * @return the vector
	 */
	public static Vector<JKDbIdValue> createList(final Object[] values) {
		final int ids[] = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			ids[i] = i;
		}
		return JKDbIdValue.createList(ids, values);
	}

	/** The id. */
	Object id;

	/** The value. */
	Object value;

	/**
	 * Instantiates a new JK db id value.
	 */
	public JKDbIdValue() {
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Object getId() {
		return this.id;
	}

	/**
	 * Gets the id as integer.
	 *
	 * @return the id as integer
	 */
	public int getIdAsInteger() {
		final String id = getId().toString();
		if (id == null || id.trim().equals("")) {
			return -1;
		}
		return Integer.parseInt(id);
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(final Object id) {
		this.id = id;
	}

	/**
	 * Sets the value.
	 *
	 * @param value
	 *            the new value
	 */
	public void setValue(final Object value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (this.value != null) {
			return this.value.toString();
		}
		return this.id.toString();
	}
}
