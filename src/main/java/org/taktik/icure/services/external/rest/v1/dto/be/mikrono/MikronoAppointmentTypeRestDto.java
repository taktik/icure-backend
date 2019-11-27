package org.taktik.icure.services.external.rest.v1.dto.be.mikrono;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class MikronoAppointmentTypeRestDto implements Serializable {

    String color; // "#123456"
    int durationInMinutes;
    String externalRef; // same as CalendarItemType.id, stored in mikrono to know linked topaz object has changed
    String mikronoId;

    List<String> docIds; // do not use

    HashMap<String, String> otherInfos = new HashMap<String, String>();

    HashMap<String, String> subjectByLanguage = new HashMap<String, String>();

}
