/*
 *  iCure Data Stack. Copyright (c) 2020  aduchate
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

package org.taktik.couchdb.annotation

/**
 * Used to distinguish a type's documents in the database.
 *
 * Declare on fields or getter methods in order for them to be used in generated views filter conditions.
 *
 * Declare on type in order specify a custom filter condition.
 *
 * A TypeDiscriminator declared on type level cannot be mixed with TypeDiscriminators declared onb fields.
 * @author henrik lundgren
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.FIELD, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(RetentionPolicy.RUNTIME)
annotation class TypeDiscriminator(
        /**
         * If TypeDiscriminator is declared on type level, a filter condition must be specified.
         * This condition is inserted along other conditions in the generated views map function:
         * function(doc) { if(CONDITION INSERTED HERE && doc.otherField) {emit(null, doc._id)} }
         *
         * Not valid to use if declared on field or method level.
         */
        val value: String = "")
