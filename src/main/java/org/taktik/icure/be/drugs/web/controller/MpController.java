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
//
//
//package org.taktik.icure.be.drugs.web.controller;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.mvc.AbstractController;
//import org.taktik.icure.be.drugs.dto.MpFullInfos;
//import org.taktik.icure.be.drugs.dto.MpId;
//import org.taktik.icure.be.drugs.logic.DrugsLogic;
//
//
///**
// * A controller for retrieving Mp Informations
// * @author abaudoux
// *
// */
//public class MpController extends AbstractController {
//
//	protected DrugsLogic drugsLogic;
//
//	@Override
//	protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//		String lang = req.getParameter("lang");
//		lang = drugsLogic.getAvailableLanguage(lang);
//		String mpPK = req.getParameter("mpId");
//		MpId mpId = new MpId(mpPK,lang);
//		MpFullInfos mpInfos = drugsLogic.getFullMpInfos(mpId);
//		ModelAndView mav = new ModelAndView("mp");
//		mav.addObject("lang", lang);
//		mav.addObject("mp",mpInfos);
//		return mav;
//	}
//
//	public DrugsLogic getDrugsLogic() {
//		return drugsLogic;
//	}
//
//	@Autowired
//	public void setDrugsLogic(DrugsLogic drugsLogic) {
//		this.drugsLogic = drugsLogic;
//	}
//
//}
