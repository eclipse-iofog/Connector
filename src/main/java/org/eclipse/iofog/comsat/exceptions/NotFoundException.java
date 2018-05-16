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

package org.eclipse.iofog.comsat.exceptions;

public class NotFoundException extends Exception {
	private static final long serialVersionUID = -7152467337681593296L;

	public NotFoundException(String message) {
		super(message);
	}
	
	public NotFoundException() {
		super();
	}
}
