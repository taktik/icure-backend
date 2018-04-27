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


package org.taktik.icure.be.drugs.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.taktik.icure.be.drugs.logic.DrugsLogic;

public class LegendController extends AbstractController {

	protected DrugsLogic drugsLogic;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String lang = req.getParameter("lang");
		lang = drugsLogic.getAvailableLanguage(lang);
		ModelAndView mav = new ModelAndView("legend");
		mav.addObject("lang", lang);
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
