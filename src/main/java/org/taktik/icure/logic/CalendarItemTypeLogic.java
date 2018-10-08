package org.taktik.icure.logic;

import org.taktik.icure.entities.CalendarItemType;
import org.taktik.icure.exceptions.DeletionException;

import java.util.List;

public interface CalendarItemTypeLogic extends EntityPersister<CalendarItemType, String> {
    CalendarItemType createCalendarItemType(CalendarItemType calendarItemType);

    List<String> deleteCalendarItemTypes(List<String> ids) throws DeletionException;

    CalendarItemType getCalendarItemType(String calendarItemTypeId);

    CalendarItemType modifyCalendarTypeItem(CalendarItemType calendarItemType);

    List<CalendarItemType> getAllEntitiesIncludeDelete();
}
