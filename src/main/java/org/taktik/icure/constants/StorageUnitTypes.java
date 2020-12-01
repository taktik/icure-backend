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

package org.taktik.icure.constants;

public abstract class StorageUnitTypes {
	public static interface Identifier {
		String CASSANDRA	    = "org.taktik.storagetype.cassandra";
		String DISK	            = "org.taktik.storagetype.simplediskstorage";
		String JCLOUDS_BLOB_STORAGE	= "org.taktik.storagetype.jcloudsblobstorage";
		String IN_PLACE	        = "org.taktik.storagetype.inplacestorage";
	}
}
