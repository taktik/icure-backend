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

package org.taktik.icure.be.ehealth.logic.kmehr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by aduchate on 28/06/11, 08:21
 */
public class KmehrUtils {
    public static Map<String,String[]> CD_VACCINE_TO_CD_VACCINEINDICATION;

    static {
        CD_VACCINE_TO_CD_VACCINEINDICATION = new HashMap<String,String[]>();
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0618",new String[]{"poliomyelitis"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0619",new String[]{"measles"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0622",new String[]{"seasonalinfluenza"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0624",new String[]{"hepatitisa"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0625",new String[]{"hepatitisb"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0626",new String[]{"hepatitisa","hepatitisb"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0627",new String[]{"rabies"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0628",new String[]{"varicella"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0629",new String[]{"yellowfever"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0981",new String[]{"tickborneencephalitis"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0982",new String[]{"ej"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0632",new String[]{"diphteria","tetanus","pertussis","hib"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("1127",new String[]{"tetanus"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("1128",new String[]{"diphteria"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("1129",new String[]{"pertussis"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("1130",new String[]{"hib"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0635",new String[]{"diphteria","tetanus"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0636",new String[]{"diphteria","tetanus","pertussis"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0637",new String[]{"diphteria","tetanus","pertussis","poliomyelitis"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0638",new String[]{"diphteria","tetanus","poliomyelitis"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0639",new String[]{"diphteria","tetanus","pertussis","poliomyelitis","hepatitisb","hib"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0640",new String[]{"meningitis","meningitisc"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("1132",new String[]{"meningitisc"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("1133",new String[]{"meningitis"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0641",new String[]{"pneumonia23"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("1134",new String[]{"pneumonia23"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("1135",new String[]{"pneumonia7"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0642",new String[]{"tuberculosis"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("0646",new String[]{"typhoid"});
        CD_VACCINE_TO_CD_VACCINEINDICATION.put("9901",new String[]{"papillomavirus"});
    }

    public static String medicationDayPeriodToKmehrDayPeriod(String medicationDayPeriod) {
        /*

          KMEHR                       ICURE
          morning                     morning
          afternoon                   afternoon
          evening                     afternoon
          night                       night
          beforebreakfast             beforebreakfast
          beforebreakfast             duringbreakfast
          afterbreakfast              afterbreakfast
          beforelunch                 beforelunch
          beforelunch                 duringlunch
          afterlunch                  afterlunch
          beforedinner                beforedinner
          beforedinner                duringdinner
          afterdinner                 afterdinner
          aftermeal                   afterbreakfast,afterlunch,afterdinner
          betweenbreakfastandlunch    afterbreakfast
          betweendinnerandsleep       afterdinner
          betweenlunchanddinner       afterlunch
          betweenmeals                afterbreakfast,afternoon
          thehourofsleep              night

        */

        String result = medicationDayPeriod.contains("/")?medicationDayPeriod.split("/")[1]:medicationDayPeriod;

        if (result.equals("duringbreakfast")) {
            result = "beforebreakfast";
        } else if (result.equals("duringlunch")) {
            result = "beforelunch";
        } else if (result.equals("duringdinner")) {
            result = "beforedinner";
        }
        return result;
    }


    public static Set<String> dossMedItems = new HashSet<String>(Arrays.asList(
            "intubationrefusal", "ntbr", "bloodtransfusionrefusal", "risk", "conclusion",
            "healthissue", "allergy", "adr", "medication", "socialrisk", "professionalrisk", "familyrisk"
    ));

    public static String sexCode(String iCureSex) {
        if (iCureSex == null)
            return "unknown";
        if (iCureSex.toLowerCase().equals("m")) {
            return "male";
        } else if (iCureSex.toLowerCase().equals("f")) {
            return "female";
        } else if (iCureSex.toLowerCase().equals("c")) {
            return "changed";
        }

        return "unknown";
    }

    public static String sexLetter(String sexCode) {
        if (sexCode == null)
            return "I";
        if (sexCode.toLowerCase().equals("male")) {
            return "M";
        } else if (sexCode.toLowerCase().equals("female")) {
            return "F";
        } else if (sexCode.toLowerCase().equals("changed")) {
            return "C";
        }

        return "I";
    }

    public static String getType(String s) {
        String type;
        if (s.matches("^.+\\[.+,.+\\]") || s.matches("^.+\\[.+\\]")) {
            type = s.split("\\[")[0];
        } else {
            type = s.split(":")[0];
        }
        if (type.equals("ICD")) type = "ICD10";
        if (type.equals("ICPC")) type = "ICPC2";
        if (type.equals("CD-CLINICAL")) type = "IBUI";
        if (type.startsWith("LOCAL[")) type = type.replaceAll("LOCAL\\[([A-Za-z0-9-]+)(,.+)?\\]", "$1");
        return type;
    }

    public static String getValue(String s) {
        String[] strings = s.split(":");
        return strings[strings.length - 1];
    }

    public static String dpKmehrToICure(String kmehrDp) {
        /*

            KMEHR                       ICURE
            morning                     morning
            afternoon                   afternoon
            evening                     afternoon
            night                       night
            beforebreakfast             beforebreakfast
            beforebreakfast             duringbreakfast
            afterbreakfast              afterbreakfast
            beforelunch                 beforelunch
            beforelunch                 duringlunch
            afterlunch                  afterlunch
            beforedinner                beforedinner
            beforedinner                duringdinner
            afterdinner                 afterdinner
            aftermeal                   afterbreakfast,afterlunch,afterdinner
            betweenbreakfastandlunch    afterbreakfast
            betweendinnerandsleep       afterdinner
            betweenlunchanddinner       afterlunch
            betweenmeals                afterbreakfast,afternoon
            thehourofsleep              night

          */

        if (kmehrDp.equals("aftermeal")) {
            return "afterbreakfast,afterlunch,afterdinner";
        }
        if (kmehrDp.equals("betweenbreakfastandlunch")) {
            return "afterbreakfast";
        }
        if (kmehrDp.equals("betweendinnerandsleep")) {
            return "afterdinner";
        }
        if (kmehrDp.equals("betweenlunchanddinner")) {
            return "afterlunch";
        }
        if (kmehrDp.equals("betweenmeals")) {
            return "afterbreakfast,afternoon";
        }
        if (kmehrDp.equals("thehourofsleep")) {
            return "night";
        }

        return kmehrDp;
    }


    public static String dpICureToKmehr(String icureDp) {
        /*

            KMEHR                       ICURE
            morning                     morning
            afternoon                   afternoon
            evening                     afternoon
            night                       night
            beforebreakfast             beforebreakfast
            beforebreakfast             duringbreakfast
            afterbreakfast              afterbreakfast
            beforelunch                 beforelunch
            beforelunch                 duringlunch
            afterlunch                  afterlunch
            beforedinner                beforedinner
            beforedinner                duringdinner
            afterdinner                 afterdinner
            aftermeal                   afterbreakfast,afterlunch,afterdinner
            betweenbreakfastandlunch    afterbreakfast
            betweendinnerandsleep       afterdinner
            betweenlunchanddinner       afterlunch
            betweenmeals                afterbreakfast,afternoon
            thehourofsleep              night

          */

        if (icureDp.equals("duringbreakfast")) {
            return "beforebreakfast";
        }
        if (icureDp.equals("duringlunch")) {
            return "beforelunch";
        }
        if (icureDp.equals("duringdinner")) {
            return "beforedinner";
        }

        return icureDp;
    }

    public static String getDn(String s) {
        String[] strings = s.split(":");
        if (s.length() >= 3) {
            return strings[strings.length - 2];
        }
        return null;
    }
}
