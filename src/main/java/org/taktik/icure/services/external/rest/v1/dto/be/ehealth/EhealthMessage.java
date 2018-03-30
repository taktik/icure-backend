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

package org.taktik.icure.services.external.rest.v1.dto.be.ehealth;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 04/10/12
 * Time: 11:36
 * To change this template use File | Settings | File Templates.
 */
public class EhealthMessage implements Serializable {
    private String id;
    private String publicationId;
    private Addressee sender;
    private Addressee mandatee;
    private List<Addressee> destinations;
    private boolean important;
    private boolean encrypted;
    private boolean hasAnnex;
    private boolean hasFreeInformations;
    private Long publicationDate;
    private Long expirationDate;
    private String size;
    private Map<String,String> customMetas;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    public Addressee getSender() {
        return sender;
    }

    public void setSender(Addressee sender) {
        this.sender = sender;
    }

    public Addressee getMandatee() {
        return mandatee;
    }

    public void setMandatee(Addressee mandatee) {
        this.mandatee = mandatee;
    }

    public List<Addressee> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<Addressee> destinations) {
        this.destinations = destinations;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

     public boolean isHasAnnex() {
        return hasAnnex;
    }

    public void setHasAnnex(boolean hasAnnex) {
        this.hasAnnex = hasAnnex;
    }

    public boolean isHasFreeInformations() {
        return hasFreeInformations;
    }

    public void setHasFreeInformations(boolean hasFreeInformations) {
        this.hasFreeInformations = hasFreeInformations;
    }

    public Long getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Long publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Map<String, String> getCustomMetas() {
        return customMetas;
    }

    public void setCustomMetas(Map<String, String> customMetas) {
        this.customMetas = customMetas;
    }
}
