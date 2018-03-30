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

package org.taktik.icure.be.matrix;

import org.taktik.icure.entities.embed.InvoicingCode;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Created by aduchate on 14/03/2017.
 */
public class Codeline {
	static private DateTimeFormatter defaultFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy").withLocale( Locale.FRANCE )
			.withZone( ZoneId.systemDefault() );
	CodeSpec c1;
	CodeSpec c2;

	public Codeline(InvoicingCode invoicingCode1, InvoicingCode invoicingCode2, DateTimeFormatter formatter) {
		this.c1 = invoicingCode1 == null ? new CodeSpec(null,null, formatter) : new CodeSpec(invoicingCode1.getCode()!=null?invoicingCode1.getCode():invoicingCode1.getTarificationId()!=null?invoicingCode1.getTarificationId().split("\\|")[1]:null, invoicingCode1.getDateCode(), formatter==null?defaultFormatter:formatter);
		this.c2 = invoicingCode2 == null ? new CodeSpec(null,null, formatter) : new CodeSpec(invoicingCode2.getCode()!=null?invoicingCode2.getCode():invoicingCode2.getTarificationId()!=null?invoicingCode2.getTarificationId().split("\\|")[1]:null, invoicingCode2.getDateCode(), formatter==null?defaultFormatter:formatter);
	}

	public CodeSpec getC1() {
		return c1;
	}

	public void setC1(CodeSpec c1) {
		this.c1 = c1;
	}

	public CodeSpec getC2() {
		return c2;
	}

	public void setC2(CodeSpec c2) {
		this.c2 = c2;
	}

	public class CodeSpec {
		String date;
		String code;

		public CodeSpec(String code, Long dateCode, DateTimeFormatter formatter) {
			this.code = code;
			this.date = dateCode == null ? null : formatter.format(Instant.ofEpochMilli(dateCode));
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
	}
}
