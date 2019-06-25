/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.entities.base;

import org.taktik.icure.entities.embed.Address;
import org.taktik.icure.entities.embed.Gender;

import java.io.Serializable;
import java.util.List;

public interface Person extends Serializable, Identifiable<String> {
    String getCivility();
    void setCivility(String civility);

    Gender getGender();
    void setGender(Gender gender);

    String getFirstName();
    void setFirstName(String firstName);

    String getLastName();
    void setLastName(String lastName);

    List<Address> getAddresses();
    void setAddresses(List<Address> addresses);

    java.util.List<String> getLanguages();
    void setLanguages(List<String> languages);
}
