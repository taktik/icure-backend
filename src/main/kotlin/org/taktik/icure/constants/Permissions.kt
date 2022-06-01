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
package org.taktik.icure.constants

import java.util.*

interface Permissions {
	enum class Type(
		private val bitIndex: Int,
		val isNoCriterionSupported: Boolean,
		supportedCriterionTypes: Array<CriterionType>
	) {
		// 1
		AUTHENTICATE(0, true, arrayOf(CriterionType.VIRTUALHOST)), // 2
		ADMIN(1, true, arrayOf(CriterionType.VIRTUALHOST)), // 16
		PATIENT_VIEW(4, true, arrayOf(CriterionType.VIRTUALHOST, CriterionType.CURRENT_USER)), // 32
		DATA_VIEW(
			5, true,
			arrayOf(
				CriterionType.VIRTUALHOST,
				CriterionType.DATA_TYPE,
				CriterionType.CURRENT_USER,
				CriterionType.DATA_TYPE,
				CriterionType.PATIENT_STATUS
			)
		), // 64
		CONFIDENTIAL_DATA_VIEW(
			6, true,
			arrayOf(
				CriterionType.VIRTUALHOST,
				CriterionType.DATA_TYPE,
				CriterionType.CURRENT_USER,
				CriterionType.DATA_TYPE,
				CriterionType.PATIENT_STATUS
			)
		), // 128
		DATA_CREATE(
			7, true,
			arrayOf(
				CriterionType.VIRTUALHOST,
				CriterionType.DATA_TYPE,
				CriterionType.CURRENT_USER,
				CriterionType.DATA_TYPE,
				CriterionType.PATIENT_STATUS
			)
		), // 256
		DATA_EDIT(
			8, true,
			arrayOf(
				CriterionType.VIRTUALHOST,
				CriterionType.DATA_TYPE,
				CriterionType.CURRENT_USER,
				CriterionType.DATA_TYPE,
				CriterionType.PATIENT_STATUS
			)
		), // 1024
		DATA_DELETE(
			9, true,
			arrayOf(
				CriterionType.VIRTUALHOST,
				CriterionType.DATA_TYPE,
				CriterionType.CURRENT_USER,
				CriterionType.DATA_TYPE,
				CriterionType.PATIENT_STATUS
			)
		), // 2048
		PATIENT_CREATE(
			10, true,
			arrayOf(
				CriterionType.VIRTUALHOST,
				CriterionType.DATA_TYPE,
				CriterionType.CURRENT_USER,
				CriterionType.DATA_TYPE,
				CriterionType.PATIENT_STATUS
			)
		), // 4096
		PATIENT_EDIT(
			11, true,
			arrayOf(
				CriterionType.VIRTUALHOST,
				CriterionType.DATA_TYPE,
				CriterionType.CURRENT_USER,
				CriterionType.DATA_TYPE,
				CriterionType.PATIENT_STATUS
			)
		), // 8192
		PATIENT_MERGE(
			12, true,
			arrayOf(
				CriterionType.VIRTUALHOST,
				CriterionType.DATA_TYPE,
				CriterionType.CURRENT_USER,
				CriterionType.DATA_TYPE,
				CriterionType.PATIENT_STATUS
			)
		), // 16384
		PATIENT_DELETE(
			13, true,
			arrayOf(
				CriterionType.VIRTUALHOST,
				CriterionType.DATA_TYPE,
				CriterionType.CURRENT_USER,
				CriterionType.DATA_TYPE,
				CriterionType.PATIENT_STATUS
			)
		);

		private val supportedCriterionTypes: Set<CriterionType>
		val bitValue: Int
			get() = 1 shl bitIndex

		fun isEnabled(integer: Int): Boolean {
			return integer and bitValue == bitValue
		}

		fun isCriterionTypeSupported(criterionType: CriterionType): Boolean {
			return supportedCriterionTypes.contains(criterionType)
		}

		init {
			this.supportedCriterionTypes = HashSet(Arrays.asList(*supportedCriterionTypes))
		}
	}

	enum class CriterionType {
		VIRTUALHOST, CURRENT_USER, DATA_TYPE, PATIENT_STATUS
	}

	enum class CriterionTypeCurrentUser {
		DATA_CREATION_USER, DATA_MODIFICATION_USER, PATIENT_CREATION_USER, PATIENT_MODIFICATION_USER, PATIENT_REFERENCE_HC_USER, PATIENT_HC_TEAM_USER
	}

	enum class CriterionDataType {
		ADMINISTRATIVE, HEALTH, SENSITIVE, CONFIDENTIAL
	}
}
