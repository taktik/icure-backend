/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.utils;
/**
 * Apply Luhn algorithm to compute check digit
 * This algorithm is used to compute the first check digit
 * during extended unique id generation
 *
 * @author Ciedmdr
 */
public class CheckDigitLuhn {
    /**
     * Computes the checksum C according Luhn algorithm
     * @param iNumber String charset to compute Luhn check digit
     * @return the check digit
     */
    public static int computeCheckDigit(String iNumber ) {
        int checkSum = 0;
        int weight = 0;
        int weightedDigit = 0;
        for(int pos=0;pos<iNumber.length();pos++) {
            weight    = (pos%2==0)?2:1;
            weightedDigit = Character.digit(iNumber.charAt(iNumber.length()-pos-1),10) * weight;
            checkSum += (weightedDigit>9?weightedDigit-9:weightedDigit);
        }
        return (10 - checkSum%10) % 10;
    }
    /**
     * Verify the number in parameter (11 DIGITS + Luhn check digit = 12 DIGITS)
     * @param iNumber
     * @return true if checked
     */
    public static boolean checkDigit( String iNumber ) {
        int checkSum         = 0;
        int weight             = 0;
        int weightedDigit     = 0;
        for(int pos=0;pos<iNumber.length();pos++) {
            weight             =     (pos%2==0)?1:2;
            weightedDigit    =     Character.digit(iNumber.charAt(iNumber.length()-pos-1),10) * weight;
            checkSum         +=     (weightedDigit>9?weightedDigit-9:weightedDigit);
        }
        if (checkSum % 10 == 0)
            return true;
        else
            return false;
    }
}