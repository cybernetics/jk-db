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
