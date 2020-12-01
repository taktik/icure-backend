/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.exceptions;

public class ICureException extends Exception {
	private static final long serialVersionUID = -8702376071839870064L;

	public ICureException() {
		super();
	}

	public ICureException(String s, Throwable t) {
		super(s, t);
	}

	public ICureException(String s) {
		super(s);
	}

	public ICureException(Throwable t) {
		super(t);
	}
}
