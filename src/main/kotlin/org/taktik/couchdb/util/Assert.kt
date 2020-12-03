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

package org.taktik.couchdb.util

/**
 *
 * @author henrik lundgren
 */
class Assert {

    companion object {

        @JvmOverloads
        fun notNull(o: Any?, message: String? = null) {
            if (o == null) {
                throw message?.let { NullPointerException(it) } ?: NullPointerException()
            }
        }

        fun isNull(o: Any?, message: String) {
            if (o != null) {
                throwIllegalArgument(message)
            }
        }

        fun isTrue(b: Boolean) {
            isTrue(b, null)
        }

        fun isTrue(b: Boolean, message: String?) {
            if (!b) {
                throwIllegalArgument(message)
            }
        }

        fun notEmpty(c: Collection<*>, message: String) {
            notNull(c, message)
            if (c.isEmpty()) {
                throwIllegalArgument(message)
            }
        }

        fun notEmpty(a: Array<Any?>, message: String) {
            notNull(a, message)
            if (a.isEmpty()) {
                throwIllegalArgument(message)
            }
        }

        @JvmOverloads
        fun hasText(s: String?, message: String? = null) {
            if (s.isNullOrEmpty()) {
                throwIllegalArgument(message)
            }
        }

        private fun throwIllegalArgument(s: String?) {
            throw s?.let { IllegalArgumentException(it) } ?: IllegalArgumentException()
        }
    }
}
