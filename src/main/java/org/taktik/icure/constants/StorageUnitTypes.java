/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.constants;

public abstract class StorageUnitTypes {
	public static interface Identifier {
		String CASSANDRA	    = "org.taktik.storagetype.cassandra";
		String DISK	            = "org.taktik.storagetype.simplediskstorage";
		String JCLOUDS_BLOB_STORAGE	= "org.taktik.storagetype.jcloudsblobstorage";
		String IN_PLACE	        = "org.taktik.storagetype.inplacestorage";
	}
}