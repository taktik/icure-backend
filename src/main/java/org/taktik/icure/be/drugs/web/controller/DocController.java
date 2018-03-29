/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.drugs.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.taktik.icure.be.drugs.dto.DocExtendedInfos;
import org.taktik.icure.be.drugs.dto.DocId;
import org.taktik.icure.be.drugs.logic.DrugsLogic;

/**
 * A controller for retrieving Documentation Nodes
 * @author abaudoux
 *
 */
public class DocController extends AbstractController {

	protected DrugsLogic drugsLogic;

	protected final Log log = LogFactory.getLog(getClass());

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String lang = req.getParameter("lang");
		lang = drugsLogic.getAvailableLanguage(lang);
		String docPK = req.getParameter("docId");
		DocId docId = new DocId(docPK,lang);
		DocExtendedInfos docInfos = drugsLogic.getExtendedDocInfos(docId);
		ModelAndView mav = new ModelAndView("doc");
		mav.addObject("lang", lang);
		mav.addObject("doc",docInfos);
		return mav;
	}

	public DrugsLogic getDrugsLogic() {
		return drugsLogic;
	}

	@Autowired
	public void setDrugsLogic(DrugsLogic drugsLogic) {
		this.drugsLogic = drugsLogic;
	}

}
