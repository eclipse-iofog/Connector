/*
 * *******************************************************************************
 *  * Copyright (c) 2018 Edgeworx, Inc.
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Eclipse Public License v. 2.0 which is available at
 *  * http://www.eclipse.org/legal/epl-2.0
 *  *
 *  * SPDX-License-Identifier: EPL-2.0
 *  *******************************************************************************
 *
 */

package org.eclipse.iofog.connector.exceptions;

public class DuplicateIdException extends Exception {
	private static final long serialVersionUID = -5075882609298125930L;

	public DuplicateIdException(String message) {
		super(message);
	}
	
	public DuplicateIdException() {
		super();
	}
}
