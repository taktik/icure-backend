package org.taktik.icure.dao;

import org.taktik.icure.entities.CalendarItemType;

import java.util.List;

public interface CalendarItemTypeDAO extends GenericDAO<CalendarItemType> {
    List<CalendarItemType> getAllEntitiesIncludeDelete();
}
