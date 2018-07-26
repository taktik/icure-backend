package org.taktik.icure.db.epicure;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Tools {
	public static Long cnvTime2Long(ResultSet resultset, String sFieldname) throws SQLException {
		String sValue = resultset.getString(sFieldname);

		if (sValue != null && !"0000-00-00 00:00:00.000".equals(sValue))
			return resultset.getTimestamp(sFieldname).getTime();
		else
			return null;
	}
}
