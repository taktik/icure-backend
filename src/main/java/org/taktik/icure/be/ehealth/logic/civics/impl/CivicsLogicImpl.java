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

package org.taktik.icure.be.ehealth.logic.civics.impl;

import be.ehealth.businessconnector.civics.session.CivicsService;
import be.ehealth.businessconnector.civics.session.CivicsSessionServiceFactory;
import be.ehealth.technicalconnector.exception.ConnectorException;
import be.fgov.ehealth.samcivics.type.v1.*;
import org.joda.time.DateTime;
import org.taktik.icure.be.ehealth.logic.civics.CivicsLogic;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 07/11/13
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
@org.springframework.stereotype.Service
public class CivicsLogicImpl implements CivicsLogic {
    public final ParagraphAndChildrenType findParagraphText(String paragraphName, Date date, String language) throws ConnectorException {
        // the chapter to search
        String chapterName = "IV";
        // the paragraph to search
        //String paragraphName = "330100";
        // Set the language of the user
        LanguageType lang = language!=null&&language.toLowerCase().equals("nl")?LanguageType.NL:LanguageType.FR;

        /*
         * Create the request
         */
        ParagraphAndVersesRequestType request = new ParagraphAndVersesRequestType();
        // set the chapter name
        request.setChapterName(chapterName);
        // set the paragraph name
        request.setParagraphName(paragraphName);
        // set the language
        request.setLanguage(lang);
        if (date != null) { // optionally: the start date, retrieve version starting from this date
            request.setStartDate(new DateTime(date.getTime()));
        }

        /*
         * Invoke the business connector framework's eHealthBox's getMessageList operation
         */
        CivicsService service = CivicsSessionServiceFactory.getCivicsService();
        FindParagraphTextResponse response = service.findParagraphText(request);


        return response.getParagraph();
    }

}
