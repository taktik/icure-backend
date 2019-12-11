///*
// * Copyright (C) 2018 Taktik SA
// *
// * This file is part of iCureBackend.
// *
// * iCureBackend is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License version 2 as published by
// * the Free Software Foundation.
// *
// * iCureBackend is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.taktik.icure.asynclogic.impl.filter.contact
//
//import kotlinx.coroutines.flow.Flow
//import org.taktik.icure.asynclogic.AsyncICureSessionLogic
//import org.taktik.icure.asynclogic.ContactLogic
//import org.taktik.icure.dto.filter.contact.ContactByServiceIdsFilter
//import org.taktik.icure.entities.Contact
//import org.taktik.icure.logic.impl.filter.Filter
//import org.taktik.icure.logic.impl.filter.Filters
//import javax.security.auth.login.LoginException
//
//class ContactByServiceIdsFilter(private val contactLogic: ContactLogic,
//                                private val sessionLogic: AsyncICureSessionLogic) : Filter<String, Contact, ContactByServiceIdsFilter> {
//
//    private suspend fun loggedHealthCarePartyId(): String {
//        val user = sessionLogic.getCurrentSessionContext().getUser()
//        return user.healthcarePartyId ?: throw LoginException("You must be logged to perform this action. ")
//    }
//
//    override fun resolve(filter: ContactByServiceIdsFilter, context: Filters): Flow<String> {
//        return contactLogic.findByServices(filter.ids)
//    }
//}
