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

package org.taktik.icure.dao;


import org.taktik.icure.entities.TimeTable;

import java.util.List;

public interface TimeTableDAO extends GenericDAO<TimeTable> {

    List<TimeTable> listTimeTableByAgendaId(String agendaId);

    List<TimeTable> listTimeTableByStartDateAndAgendaId(Long startDate, Long endDate, String agendaId);

    List<TimeTable> listTimeTableByEndDateAndAgendaId(Long startDate, Long endDate, String agendaId);

    List<TimeTable> listTimeTableByPeriodAndAgendaId(Long startDate, Long endDate, String agendaId);

}
