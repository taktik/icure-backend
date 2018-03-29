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

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class Math {
	static double[] obsXs = {28d, 30d, 32d, 34d, 36d, 38d, 40d, 42d};
	static double[] obsXsW50 = {16d, 17d, 18d, 19d, 20d, 21d, 22d, 23d, 24d, 25d, 26d, 27d, 28d, 30d, 32d, 34d, 36d, 38d, 40d, 42d};

	static double[] obsWeights10 = {920d, 1210d, 1510d, 1930d, 2290d, 2590d, 2840d, 2980d};
	static double[] obsWeights25 = {1080d, 1360d, 1730d, 2160d, 2550d, 2850d, 3090d, 3200d};
	static double[] obsWeights50 = {142d, 176d, 218d, 267d, 324d, 390d, 465d, 551d, 647d, 754d, 872d, 1001d, 1200d, 1610d, 2060d, 2480d, 2850d, 3130d, 3370d, 3490d};
	static double[] obsWeights75 = {1470d, 2180d, 2640d, 2970d, 3240d, 3440d, 3690d, 3780d};
	static double[] obsWeights90 = {1900d, 2540d, 2960d, 3270d, 3520d, 3720d, 3910d, 4080d};

	static double[] obsHeights10 = {35.0, 37.6, 40.5, 43.0, 45.3, 46.6, 47.5, 47.7};
	static double[] obsHeights25 = {36.8, 39.9, 40.5, 45.0, 46.5, 47.7, 48.7, 49.4};
	static double[] obsHeights50 = {37.8, 40.9, 44.3, 46.4, 48.0, 49.2, 50.0, 50.5};
	static double[] obsHeights75 = {40.5, 45.4, 47.0, 48.4, 49.6, 50.5, 51.2, 51.9};
	static double[] obsHeights90 = {44.0, 47.8, 49.1, 49.9, 50.0, 51.4, 57.1, 52.8};

	static double[] obsW10x = rlsInterpolation(obsXs, obsWeights10, 3);
	static double[] obsW25x = rlsInterpolation(obsXs, obsWeights25, 3);
	static double[] obsW50x = rlsInterpolation(obsXsW50, obsWeights50, 3);
	static double[] obsW75x = rlsInterpolation(obsXs, obsWeights75, 3);
	static double[] obsW90x = rlsInterpolation(obsXs, obsWeights90, 3);

	static double[] obsH10x = rlsInterpolation(obsXs, obsHeights10, 3);
	static double[] obsH25x = rlsInterpolation(obsXs, obsHeights25, 3);
	static double[] obsH50x = rlsInterpolation(obsXs, obsHeights50, 3);
	static double[] obsH75x = rlsInterpolation(obsXs, obsHeights75, 3);
	static double[] obsH90x = rlsInterpolation(obsXs, obsHeights90, 3);

	public static double polValue(double x, double[] coeffs) {
		double val = 0d;
		double pow = 1;
		for (int i = 0; i < coeffs.length; i++) {
			val += pow * coeffs[i];
			pow *= x;
		}
		return val;
	}

	public static Double mean(Collection<Double> items) {
		double result = 0;
		int n = 0;
		for (Double d : items) {
			if (d != null) {
				n++;
				result += d;
			}
		}
		return result / n;
	}

	public static Double sum(Collection<Double> items) {
		double result = 0;
		for (Double d : items) {
			if (d != null) {
				result += d;
			}
		}
		return result;
	}

	public static double[] rlsInterpolation(double[] x, double[] y, int pow) {
		if (pow < 1) {
			return null;
		}

		double[] coeffs = new double[pow + 1];
		double d = 1000d;
		for (int i = 0; i < pow + 1; i++) {
			coeffs[i] = 0d;
		}
		double[][] pMtx = new double[pow + 1][pow + 1];
		for (int i = 0; i < pow + 1; i++) {
			for (int j = 0; j < pow + 1; j++) {
				pMtx[i][j] = (i == j) ? d : 0;
			}
		}

		RealMatrix wV = new Array2DRowRealMatrix(coeffs);
		RealMatrix pM = new Array2DRowRealMatrix(pMtx);

		for (int k = 0; k < x.length; k++) {
			double xx = x[k];
			double yy = y[k];

			RealMatrix xV = new Array2DRowRealMatrix(pow + 1, 1);

			double aPow = 1;
			for (int i = 0; i < pow + 1; i++) {
				xV.setEntry(i, 0, aPow);
				aPow *= xx;
			}

			double alpha = yy - wV.transpose().multiply(xV).getEntry(0, 0);
			RealMatrix gV = pM.multiply(xV).scalarMultiply(1 / (1d + xV.transpose().multiply(pM).multiply(xV).getEntry(0, 0)));
			pM = pM.subtract(gV.multiply(xV.transpose()).multiply(pM));
			wV = wV.add(gV.scalarMultiply(alpha));
		}
		return wV.getColumn(0);
	}

	public static Double obsPercWeight(Double weeks, Double weight) {
		double p10 = polValue(weeks, obsW10x);
		double p25 = polValue(weeks, obsW25x);
		double p50 = polValue(weeks, obsW50x);
		double p75 = polValue(weeks, obsW75x);
		double p90 = polValue(weeks, obsW90x);

		double[] cs = rlsInterpolation(new double[]{p10, p25, p50, p75, p90}, new double[]{10d, 25d, 50d, 75d, 90d}, 3);

		return polValue(weight, cs) / 100d;
	}

	public static Double obsWeightPerc(Double weeks, Double perc) {
		double p10 = polValue(weeks, obsW10x);
		double p25 = polValue(weeks, obsW25x);
		double p50 = polValue(weeks, obsW50x);
		double p75 = polValue(weeks, obsW75x);
		double p90 = polValue(weeks, obsW90x);

		double[] cs = rlsInterpolation(new double[]{10d, 25d, 50d, 75d, 90d}, new double[]{p10, p25, p50, p75, p90}, 3);

		return polValue(perc * 100d, cs);
	}

	public static Map<String, Double> obsWeights(Double ac, Double hc, Double bipd, Double fl) {
		if (ac != null && ac.doubleValue() == 0) {
			ac = null;
		}
		if (hc != null && hc.doubleValue() == 0) {
			hc = null;
		}
		if (bipd != null && bipd.doubleValue() == 0) {
			bipd = null;
		}
		if (fl != null && fl.doubleValue() == 0) {
			fl = null;
		}

		Map<String, Double> results = new HashMap<String, Double>();
		Map<String, Double> results1 = new HashMap<String, Double>();
		Map<String, Double> results2 = new HashMap<String, Double>();
		Map<String, Double> results3 = new HashMap<String, Double>();
		Map<String, Double> results4 = new HashMap<String, Double>();

		if (ac != null) {
			results1.put("Jordaan", java.lang.Math.pow(10, (0.6328 + 0.01881 * ac - 0.000043 * ac * ac + 0.000000036239 * java.lang.Math.pow(ac, 3))));
			results1.put("Higginbottom", 0.0816 * java.lang.Math.pow(ac, 3) / 1000);
			results1.put("Campbell", java.lang.Math.pow(2.718281, -4.564 + 0.0282 * ac - 0.0000331 * ac * ac) * 1000);
			results1.put("Hadlock AC", java.lang.Math.pow(2.718281, 2.695 + 0.0253 * ac - 0.0000275 * java.lang.Math.pow(ac, 2)));
			results1.put("Warsof", java.lang.Math.pow(10, -1.8367 + 0.092 * ac / 10 - 0.000019 * java.lang.Math.pow(ac, 3) / 1000) * 1000);
		}
		if (fl != null) {
			results1.put("Warsof et al. 1986", java.lang.Math.pow(2.718281, 4.6914 + 0.00151 * java.lang.Math.pow(fl, 2) - 0.0000119 * java.lang.Math.pow(fl, 3)));
		}
		if (ac != null && fl != null) {
			results2.put("Woo et al. 1985", java.lang.Math.pow(10, 0.59 + 0.008 * ac + 0.028 * fl - 0.0000716 * ac * fl));
			results2.put("Hadlock et al. 1985", java.lang.Math.pow(10, (1.304 + 0.005281 * ac + 0.01938 * fl - 0.00004 * ac * fl)));
		}
		if (ac != null && bipd != null) {
			results2.put("Hsieh et al. 1987", java.lang.Math.pow(10, 2.1315 + 0.000056541 * ac * bipd - 0.00000015515 * bipd * java.lang.Math.pow(ac, 2) + 0.000000019782 * java.lang.Math.pow(ac, 3) + 0.0052594 * bipd));
			results2.put("Vintzileos et al. 1987", java.lang.Math.pow(10, 1.879 + 0.0084 * bipd + 0.0026 * ac));
			results2.put("Woo et al. 1985", java.lang.Math.pow(10, 1.63 + 0.016 * bipd + 0.0000111 * java.lang.Math.pow(ac, 2) - 0.0000000859 * bipd * java.lang.Math.pow(ac, 2)));
			results2.put("Hadlock et al. 1984", java.lang.Math.pow(10, 1.1134 + 0.005845 * ac - 0.00000604 * java.lang.Math.pow(ac, 2) - 0.00007365 * java.lang.Math.pow(bipd, 2) + 0.00000595 * bipd * ac + 0.01694 * bipd));
			results2.put("Jordaan. 1983", java.lang.Math.pow(10, -1.1683 + 0.00377 * ac + 0.0095 * bipd - 0.000015 * bipd * ac) * 1000);
			results2.put("Warsof et al. 1977", (java.lang.Math.pow(10, -1.599 + 0.0144 * bipd + 0.0032 * ac - 0.000000111 * java.lang.Math.pow(bipd, 2) * ac) * 1000));
			results2.put("Shepard et al. 1982", (java.lang.Math.pow(10, -1.7492 + 0.0166 * bipd + 0.0046 * ac - 0.00002546 * ac * bipd) * 1000));
		}
		if (ac != null && hc != null) {
			results2.put("Hadlock", java.lang.Math.pow(10, 1.182 + 0.0273 * hc / 10 + 0.07057 * ac / 10 - 0.00063 * ac * ac / 100 - 0.0002184 * ac * ac / 100));
			results2.put("Jordan", java.lang.Math.pow(10, 0.9119 + 0.00488 * hc + 0.00824 * ac - 0.00001599 * hc * ac));
		}
		if (ac != null && bipd != null && fl != null) {
			results3.put("Hadlock et al. 1985", java.lang.Math.pow(10, 1.335 - 0.000034 * ac * fl + 0.00316 * bipd + 0.00457 * ac + 0.01623 * fl));
			results3.put("Hsieh et al. 1987", java.lang.Math.pow(10, 2.7193 + 0.000094962 * ac * bipd - 0.01432 * fl - 0.00000076742 * ac * java.lang.Math.pow(bipd, 2) + 0.000001745 * fl * java.lang.Math.pow(bipd, 2)));
			results3.put("Shinozuka et al. 1987", 0.00023966 * java.lang.Math.pow(ac, 2) * fl + 0.001623 * java.lang.Math.pow(bipd, 3));
			results3.put("Woo et al. 1985", java.lang.Math.pow(10, 1.54 + 0.015 * bipd + 0.0000111 * java.lang.Math.pow(ac, 2) - 0.0000000764 * bipd * java.lang.Math.pow(ac, 2) + 0.005 * fl - 0.00000992 * fl * ac));
		}
		if (ac != null && hc != null && bipd != null) {
			results3.put("Jordaan. 1983", java.lang.Math.pow(10, 2.3231 + 0.002904 * ac + 0.00079 * (hc) - 0.00058 * bipd));
		}
		if (ac != null && hc != null && fl != null) {
			results3.put("Hadlock et al. 1985", java.lang.Math.pow(10, 1.326 - 0.0000326 * ac * fl + 0.00107 * (hc) + 0.00438 * ac + 0.0158 * fl));
			results3.put("Hadlock et al. 1985", java.lang.Math.pow(10, 1.326 - 0.0000326 * ac * fl + 0.00107 * (hc) + 0.00438 * ac + 0.0158 * fl));
			results3.put("Hadlock et al. 1985", java.lang.Math.pow(10, 1.326 - 0.0000326 * ac * fl + 0.00107 * (hc) + 0.00438 * ac + 0.0158 * fl));
			results3.put("Combs et al. 1993", 0.00023718 * java.lang.Math.pow(ac, 2) * fl + 0.00003312 * java.lang.Math.pow((hc), 3));
			results3.put("Ott et al. 1986", java.lang.Math.pow(10, -2.0661 + 0.004355 * hc + 0.005394 * ac - 0.000008582 * hc * ac + 1.2594 * (fl / ac)) * 1000);
			results3.put("Hadlock et al. 1985", java.lang.Math.pow(10, 1.326 - 0.0000326 * ac * fl + 0.00107 * (hc) + 0.00438 * ac + 0.0158 * fl));
		}
		if (ac != null && hc != null && fl != null && bipd != null) {
			results4.put("Hadlock et al. 1985", java.lang.Math.pow(10, 1.3596 + 0.00064 * (hc) + 0.00424 * ac + 0.0174 * fl + 0.0000061 * bipd * ac - 0.0000386 * ac * fl));
		}

		//Compute weighted means
		Double mean = null;
		if (results4.size() > 0) {
			mean = (Math.mean(results4.values()) * 4 + Math.sum(results3.values())) / (4 + results3.size());
		} else if (results3.size() > 0) {
			mean = (Math.mean(results3.values()) * 4 + Math.sum(results2.values())) / (4 + results2.size());
		} else if (results2.size() > 0) {
			mean = (Math.mean(results2.values()) * 4 + Math.sum(results1.values())) / (4 + results1.size());
		} else if (results1.size() > 0) {
			mean = Math.mean(results1.values());
		}

		results.putAll(results1);
		results.putAll(results2);
		results.putAll(results3);
		results.putAll(results4);

		if (mean != null) {
			results.put("mean", mean);
		}

		return results;
	}

	public static double percentile(String desc, Double x, Double y) {
		Double prevperc = null;
		Double prevval = null;

		Double result = null;

		for (String row : desc.split("\\|")) {
			Double perc = Double.valueOf(row.split(">")[0]);
			String vals = row.split(">")[1];

			Double val = interpolate(vals, x);

			if (val >= y) {
				if (prevval != null) {
					result = ((y - prevval) * perc + (val - y) * prevperc) / (val - prevval);
				} else {
					result = perc;
				}
				break;
			}

			prevperc = perc;
			prevval = val;
		}
		if (result == null) {
			return prevperc;
		}
		return result;
	}

	public static Double interpolate(String vals, double x) {
		Double val = null;

		Double preva = null;
		Double prevb = null;


		for (String pair : vals.split(";")) {
			Double a = Double.valueOf(pair.split(",")[0]);
			Double b = Double.valueOf(pair.split(",")[1]);

			if (a >= x) {
				if (preva != null) {
					val = ((x - preva) * b + (a - x) * prevb) / (a - preva);
				} else {
					val = b;
				}
				break;
			}

			preva = a;
			prevb = b;
		}
		if (val == null) {
			val = prevb;
		}
		return val;
	}

	public static boolean isNissValid(String niss) {
		if (niss.length() != 11 && niss.length() != 13) {
			return false;
		}

		if (niss.length() == 13) {
			return CheckDigitLuhn.checkDigit(niss.substring(0,12)) && CheckDigitVerhoeff.checkDigit(niss.substring(0,11)+niss.substring(12,13));
		} else {
			long luxYear = Long.valueOf(niss.substring(0, 4));
			if (luxYear > 1900 && luxYear <= new GregorianCalendar().get(Calendar.YEAR)) {
				long luxMonth = Long.valueOf(niss.substring(4, 6));
				long luxDay = Long.valueOf(niss.substring(6, 8));

				if ((luxMonth < 13) && (luxDay < 32)) {
					long sumprod = (Long.valueOf(niss.substring(0, 1)) * 5
							+ Long.valueOf(niss.substring(1, 2)) * 4
							+ Long.valueOf(niss.substring(2, 3)) * 3
							+ Long.valueOf(niss.substring(3, 4)) * 2
							+ Long.valueOf(niss.substring(4, 5)) * 7
							+ Long.valueOf(niss.substring(5, 6)) * 6
							+ Long.valueOf(niss.substring(6, 7)) * 5
							+ Long.valueOf(niss.substring(7, 8)) * 4
							+ Long.valueOf(niss.substring(8, 9)) * 3
							+ Long.valueOf(niss.substring(9, 10)) * 2);
					long check = 11 - sumprod % 11;
					if (check == 11) {
						check = 0;
					}

					if (check == 10) {
						//Not a valid SNS number shouldn't have been attributed
					} else {
						if (check == Long.valueOf(niss.substring(10, 11))) {
							return true;
						} else {
							check = 12 - sumprod % 11;
							if (check == 12) {
								check = 1;
							}
							if (check == 11) {
								check = 0;
							}
							if (check == Long.valueOf(niss.substring(10, 11))) {
								return true;
							}
						}
					}
				}
			}

			long number = Long.valueOf(niss.substring(0, 9));
			long checkDigits = Long.valueOf(niss.substring(9, 11));
			int year = Integer.valueOf(niss.substring(0, 2));

			boolean ck1 = (97l - (number % 97l)) == checkDigits;
			boolean ck2 = (97l - ((2000000000l + number) % 97l)) == checkDigits;

			if (year + 2000 > new GregorianCalendar().get(Calendar.YEAR)) {
				return ck1;
			} else {
				return ck1 || ck2;
			}
		}
	}
}
