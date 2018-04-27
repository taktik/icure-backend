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


package org.taktik.icure.be.drugs.logic.impl;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.be.drugs.dao.DrugsDAO;


public class DatasourceInterceptor implements MethodInterceptor {

	protected DrugsDAO drugsDAO;

	public Object invoke(MethodInvocation inv) throws Throwable {
		drugsDAO.openDataStoreSession();
		Object result = inv.proceed();
		drugsDAO.closeDataStoreSession();
		return result;
	}

	public DrugsDAO getDrugsDAO() {
		return drugsDAO;
	}

	@Autowired
public void setDrugsDAO(DrugsDAO drugsDAO) {
		this.drugsDAO = drugsDAO;
	}

}
