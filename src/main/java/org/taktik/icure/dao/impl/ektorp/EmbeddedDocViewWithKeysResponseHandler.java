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

package org.taktik.icure.dao.impl.ektorp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ektorp.http.HttpResponse;
import org.ektorp.http.StdResponseHandler;
import org.ektorp.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emad7105 on 16/10/2014.
 */
public class EmbeddedDocViewWithKeysResponseHandler<T>  extends
		StdResponseHandler<List<CouchKeyValue<T>>> {

	private QueryResultWithKeysParser<T> parser;

	public EmbeddedDocViewWithKeysResponseHandler(Class<T> docType, ObjectMapper om) {
		Assert.notNull(om, "ObjectMapper may not be null");
		Assert.notNull(docType, "docType may not be null");
		parser = new QueryResultWithKeysParser<T>(docType, om);
	}

	public EmbeddedDocViewWithKeysResponseHandler(Class<T> docType, ObjectMapper om,
										  boolean ignoreNotFound) {
		Assert.notNull(om, "ObjectMapper may not be null");
		Assert.notNull(docType, "docType may not be null");
		parser = new QueryResultWithKeysParser<T>(docType, om);
		parser.setIgnoreNotFound(ignoreNotFound);
	}

	@Override
	public List<CouchKeyValue<T>> success(HttpResponse hr) throws Exception {
		parser.parseResult(hr.getContent());

		List<CouchKeyValue<T>> result = new ArrayList<CouchKeyValue<T>>();
		for (int i=0;i<parser.getRows().size();i++) {
			result.add(new CouchKeyValue<>(parser.getKeys().get(i), parser.getRows().get(i)));
		}

		return result;
	}

}