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

package org.taktik.icure.be.ehealth.dto.chapter4;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.taktik.icure.services.external.rest.v1.dto.DocumentDto;
import org.taktik.icure.services.external.rest.v1.dto.MessageDto;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 11/06/13
 * Time: 15:07
 * To change this template use File | Settings | File Templates.
 */
public class AgreementResponse implements Serializable {

    private boolean acknowledged;
    private Collection<Problem> warnings;
    private Collection<Problem> errors;
    private byte[] content;

    MessageDto message;
    DocumentDto document;

    List<AgreementTransaction> transactions = new ArrayList<AgreementTransaction>();

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public void setWarnings(Collection<Problem> warnings) {
        this.warnings = warnings;
    }

    public void setErrors(Collection<Problem> errors) {
        this.errors = errors;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public Collection<Problem> getWarnings() {
        return warnings;
    }

    public Collection<Problem> getErrors() {
        return errors;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public AgreementTransaction addTransaction(AgreementTransaction t) {
        transactions.add(t);
        return t;
    }

    public List<AgreementTransaction> getTransactions() {
        return transactions;
    }

	public MessageDto getMessage() {
		return message;
	}

	public void setMessage(MessageDto message) {
		this.message = message;
	}

	public DocumentDto getDocument() {
		return document;
	}

	public void setDocument(DocumentDto document) {
		this.document = document;
	}
}
