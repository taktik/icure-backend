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

package org.taktik.icure.dao.impl;

import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.TarificationDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.db.StringUtils;
import org.taktik.icure.entities.Tarification;

@Repository("tarificationDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Tarification' && !doc.deleted) emit( null, doc._id )}")
public class TarificationDAOImpl extends GenericDAOImpl<Tarification> implements TarificationDAO  {
	@Autowired
	public TarificationDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbBase") CouchDbICureConnector db, IDGenerator idGenerator) {
		super(Tarification.class, db, idGenerator);
		initStandardDesignDocument();
	}

	@Override
	@View(name = "by_type_code_version", map = "classpath:js/tarif/By_type_code_version.js", reduce = "function(keys, values, rereduce) {if (rereduce) {return sum(values);} else {return values.length;}}")
	public List<Tarification> findTarifications(String type, String code, String version) {
		List<Tarification> result = queryResults(
				createQuery("by_type_code_version")
						.includeDocs(true)
						.reduce(false)
						.startKey(ComplexKey.of(
								type == null ? "\u0000" : type,
								code == null ? "\u0000" : code,
								version == null ? "\u0000" : version
						))
						.endKey(ComplexKey.of(
								type == null ? ComplexKey.emptyObject() : type,
								code == null ? ComplexKey.emptyObject() : code,
								version == null ? ComplexKey.emptyObject() : version
						)));

		return result;
	}

	@Override
	@View(name = "by_region_type_code_version", map = "classpath:js/tarif/By_region_type_code_version.js", reduce = "function(keys, values, rereduce) {if (rereduce) {return sum(values);} else {return values.length;}}")
	public List<Tarification> findTarifications(String region, String type, String code, String version) {
		List<Tarification> result = queryResults(
				createQuery("by_region_type_code_version")
						.includeDocs(true)
						.reduce(false)
						.startKey(ComplexKey.of(
								region == null ? "\u0000" : region,
								type == null ? "\u0000" : type,
								code == null ? "\u0000" : code,
								version == null ? "\u0000" : version
						))
						.endKey(ComplexKey.of(
								region == null ? ComplexKey.emptyObject() : region,
								type == null ? ComplexKey.emptyObject() : type,
								code == null ? ComplexKey.emptyObject() : code,
								version == null ? ComplexKey.emptyObject() : version
						)));

		return result;
	}

	@Override
	public PaginatedList<Tarification> findTarifications(String region, String type, String code, String version, PaginationOffset pagination) {
		ComplexKey from = pagination.getStartKey() == null ?
				ComplexKey.of(
						region == null ? "\u0000" : region,
						type == null ? "\u0000" : type,
						code == null ? "\u0000" : code,
						version == null ? "\u0000" : version
				)
				: ComplexKey.of(((List) pagination.getStartKey()).toArray());
		ComplexKey to = ComplexKey.of(
				region == null ? ComplexKey.emptyObject() : region + "\ufff0",
				type == null ? ComplexKey.emptyObject() : type + "\ufff0",
				code == null ? ComplexKey.emptyObject() : code + "\ufff0",
				version == null ? ComplexKey.emptyObject() : version + "\ufff0"
		);

		return pagedQueryView(
				"by_region_type_code_version",
				from,
				to,
				pagination, false
		);
	}

	@Override
	@View(name = "by_language_label", map = "classpath:js/tarif/By_language_label.js")
	public PaginatedList<Tarification> findTarificationsByLabel(String region, String language, String label, PaginationOffset pagination) {
		label = (label!=null)? StringUtils.sanitizeString(label):null;
		List startKey = pagination == null ? null : (List) pagination.getStartKey();
		if (startKey!=null && startKey.size()>2 && startKey.get(2) != null) {
			startKey.set(2,StringUtils.sanitizeString((String) startKey.get(2)));
		}
		ComplexKey from = (startKey == null) ?
				ComplexKey.of(
						region == null ? "\u0000" : region,
						language == null ? "\u0000" : language,
						label == null ? "\u0000" : label
				)
				: ComplexKey.of(startKey.toArray());
		ComplexKey to = ComplexKey.of(
				region == null ? ComplexKey.emptyObject() : (language == null ? region + "\ufff0" : region),
				language == null ? ComplexKey.emptyObject() : (label == null ? language + "\ufff0" : language),
				label == null ? ComplexKey.emptyObject() : label + "\ufff0"
		);

		return pagedQueryView(
				"by_language_label",
				from,
				to,
				pagination, false
		);
	}

	@Override
	@View(name = "by_language_type_label", map = "classpath:js/tarif/By_language_label.js")
	public PaginatedList<Tarification> findTarificationsByLabel(String region, String language, String type, String label, PaginationOffset pagination) {
		label = (label!=null)? StringUtils.sanitizeString(label):null;
		List startKey = pagination == null ? null : (List) pagination.getStartKey();
		if (startKey!=null && startKey.size()>3 && startKey.get(3) != null) {
			startKey.set(3,StringUtils.sanitizeString((String) startKey.get(3)));
		}
		ComplexKey from = (startKey == null) ?
				ComplexKey.of(
						region == null ? "\u0000" : region,
						language == null ? "\u0000" : language,
						type == null ? "\u0000" : type,
						label == null ? "\u0000" : label
				)
				: ComplexKey.of(startKey.toArray());
		ComplexKey to = ComplexKey.of(
				region == null ? ComplexKey.emptyObject() : (language == null ? region + "\ufff0" : region),
				language == null ? ComplexKey.emptyObject() : (type == null ? language + "\ufff0" : language),
				type == null ? ComplexKey.emptyObject() : (label == null ? type + "\ufff0" : language),
				label == null ? ComplexKey.emptyObject() : label + "\ufff0"
		);

		return pagedQueryView(
				"by_language_type_label",
				from,
				to,
				pagination, false
		);
	}
}
