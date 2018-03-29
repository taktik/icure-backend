h = {
	"jscvmv":"##JSCVM_VERSION##",
	"dateUtils": {
		"fuzzyDateToDaysSince1970": function (d) {
			if (d > 99991231) {
				d = Math.floor(d / 1000000);
			}
			return new Date(Math.floor(d / 10000), Math.floor(d / 100) % 100 - 1, d % 100).getTime() / (24 * 3600 * 1000);
		},
		"fuzzyDateToDate": function (d) {
			if (d <= 99991231) {
				d = d * 1000000;
			}
			return new Date(Math.floor(d / 10000000000), Math.floor(d / 100000000) % 100 - 1, Math.floor(d / 1000000) % 100, Math.floor(d / 10000) % 100, Math.floor(d / 100) % 100, d % 100, 0);
		},
		"fuzzyDateToAge": function (d) {
			if (d > 99991231) {
				d = d / 1000000;
			}
			return (new Date().getTime() - new Date(d / 10000, (d / 100) % 100 - 1, d % 100).getTime()) / (365.25 * 24 * 3600 * 1000);
		},
		"dateToDaysSince1970": function (d) {
			return d.getTime() / (24 * 3600 * 1000);
		},
		"new": function (ms) {
			return new Date(ms);
		}
	},
	"StatMath" : (function () {
		var StatMath = {};

		StatMath.RealMatrix = function (data) {
			var that = {};
			that.data = data;

			that.transpose = function () {
				return StatMath.RealMatrix(that.data[0].map(function(col, i) {
					return that.data.map(function(row) {
						return row[i]
					})
				}));
			};

			that.multiply = function (mx) {
				var a = that.data;
				var b = mx.data;

				var aNumRows = a.length, aNumCols = a[0].length, bNumCols = b[0].length, m = new Array(aNumRows);  // initialize array of rows
				for (var r = 0; r < aNumRows; ++r) {
					m[r] = new Array(bNumCols); // initialize the current row
					for (var c = 0; c < bNumCols; ++c) {
						m[r][c] = 0;             // initialize the current cell
						for (var i = 0; i < aNumCols; ++i) {
							m[r][c] += a[r][i] * b[i][c];
						}
					}
				}
				return StatMath.RealMatrix(m);
			};

			that.subtract = function (m) {
				return StatMath.RealMatrix(that.data.map(function(x,i1) {
					return x.map(function(y,i2) {
						return y - m.data[i1][i2];
					});
				}));
			};

			that.add = function (m) {
				return StatMath.RealMatrix(that.data.map(function(x,i1) {
					return x.map(function(y,i2) {
						return y + m.data[i1][i2];
					});
				}));
			};

			that.scalarMultiply = function (d) {
				return StatMath.RealMatrix(that.data.map(function(x) {
					return x.map(function(y) {
						return y*d;
					});
				}));
			};

			that.setEntry = function (s1, s2, d) {
				(that.data[s1]||(that.data[s1]=[]))[s2] = d;
			};

			that.getEntry = function (s1, s2) {
				return that.data[s1][s2];
			};

			that.getColumn = function (s1) {
				return that.data.map(function(x) { return x[s1]; });
			};

			return that;
		};

		StatMath.obsXs = function () { if (StatMath.obsXs == null) StatMath.obsXs = [28.0, 30.0, 32.0, 34.0, 36.0, 38.0, 40.0, 42.0]; return StatMath.obsXs; };
		StatMath.obsXsW50 = function () { if (StatMath.obsXsW50 == null) StatMath.obsXsW50 = [16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 30.0, 32.0, 34.0, 36.0, 38.0, 40.0, 42.0]; return StatMath.obsXsW50; };
		StatMath.obsWeights10 = function () { if (StatMath.obsWeights10 == null) StatMath.obsWeights10 = [920.0, 1210.0, 1510.0, 1930.0, 2290.0, 2590.0, 2840.0, 2980.0]; return StatMath.obsWeights10; };
		StatMath.obsWeights25 = function () { if (StatMath.obsWeights25 == null) StatMath.obsWeights25 = [1080.0, 1360.0, 1730.0, 2160.0, 2550.0, 2850.0, 3090.0, 3200.0]; return StatMath.obsWeights25; };
		StatMath.obsWeights50 = function () { if (StatMath.obsWeights50 == null) StatMath.obsWeights50 = [142.0, 176.0, 218.0, 267.0, 324.0, 390.0, 465.0, 551.0, 647.0, 754.0, 872.0, 1001.0, 1200.0, 1610.0, 2060.0, 2480.0, 2850.0, 3130.0, 3370.0, 3490.0]; return StatMath.obsWeights50; };
		StatMath.obsWeights75 = function () { if (StatMath.obsWeights75 == null) StatMath.obsWeights75 = [1470.0, 2180.0, 2640.0, 2970.0, 3240.0, 3440.0, 3690.0, 3780.0]; return StatMath.obsWeights75; };
		StatMath.obsWeights90 = function () { if (StatMath.obsWeights90 == null) StatMath.obsWeights90 = [1900.0, 2540.0, 2960.0, 3270.0, 3520.0, 3720.0, 3910.0, 4080.0]; return StatMath.obsWeights90; };
		StatMath.obsHeights10 = function () { if (StatMath.obsHeights10 == null) StatMath.obsHeights10 = [35.0, 37.6, 40.5, 43.0, 45.3, 46.6, 47.5, 47.7]; return StatMath.obsHeights10; };
		StatMath.obsHeights25 = function () { if (StatMath.obsHeights25 == null) StatMath.obsHeights25 = [36.8, 39.9, 40.5, 45.0, 46.5, 47.7, 48.7, 49.4]; return StatMath.obsHeights25; };
		StatMath.obsHeights50 = function () { if (StatMath.obsHeights50 == null) StatMath.obsHeights50 = [37.8, 40.9, 44.3, 46.4, 48.0, 49.2, 50.0, 50.5]; return StatMath.obsHeights50; };
		StatMath.obsHeights75 = function () { if (StatMath.obsHeights75 == null) StatMath.obsHeights75 = [40.5, 45.4, 47.0, 48.4, 49.6, 50.5, 51.2, 51.9]; return StatMath.obsHeights75; };
		StatMath.obsHeights90 = function () { if (StatMath.obsHeights90 == null) StatMath.obsHeights90 = [44.0, 47.8, 49.1, 49.9, 50.0, 51.4, 57.1, 52.8]; return StatMath.obsHeights90; };
		StatMath.obsW10x = function () { if (StatMath.obsW10x == null) StatMath.obsW10x = StatMath.rlsInterpolation(StatMath.obsXs(), StatMath.obsWeights10(), 3); return StatMath.obsW10x; };
		StatMath.obsW25x = function () { if (StatMath.obsW25x == null) StatMath.obsW25x = StatMath.rlsInterpolation(StatMath.obsXs(), StatMath.obsWeights25(), 3); return StatMath.obsW25x; };
		StatMath.obsW50x = function () { if (StatMath.obsW50x == null) StatMath.obsW50x = StatMath.rlsInterpolation(StatMath.obsXsW50(), StatMath.obsWeights50(), 3); return StatMath.obsW50x; };
		StatMath.obsW75x = function () { if (StatMath.obsW75x == null) StatMath.obsW75x = StatMath.rlsInterpolation(StatMath.obsXs(), StatMath.obsWeights75(), 3); return StatMath.obsW75x; };
		StatMath.obsW90x = function () { if (StatMath.obsW90x == null) StatMath.obsW90x = StatMath.rlsInterpolation(StatMath.obsXs(), StatMath.obsWeights90(), 3); return StatMath.obsW90x; };
		StatMath.obsH10x = function () { if (StatMath.obsH10x == null) StatMath.obsH10x = StatMath.rlsInterpolation(StatMath.obsXs(), StatMath.obsHeights10(), 3); return StatMath.obsH10x; };
		StatMath.obsH25x = function () { if (StatMath.obsH25x == null) StatMath.obsH25x = StatMath.rlsInterpolation(StatMath.obsXs(), StatMath.obsHeights25(), 3); return StatMath.obsH25x; };
		StatMath.obsH50x = function () { if (StatMath.obsH50x == null) StatMath.obsH50x = StatMath.rlsInterpolation(StatMath.obsXs(), StatMath.obsHeights50(), 3); return StatMath.obsH50x; };
		StatMath.obsH75x = function () { if (StatMath.obsH75x == null) StatMath.obsH75x = StatMath.rlsInterpolation(StatMath.obsXs(), StatMath.obsHeights75(), 3); return StatMath.obsH75x; };
		StatMath.obsH90x = function () { if (StatMath.obsH90x == null) StatMath.obsH90x = StatMath.rlsInterpolation(StatMath.obsXs(), StatMath.obsHeights90(), 3); return StatMath.obsH90x; };
		StatMath.polValue = function (x, coeffs) {
			var val = 0.0;
			var pow = 1;
			for (var i = 0; i < coeffs.length; i++) {
				val += pow * coeffs[i];
				pow *= x;
			}
			return val;
		};
		StatMath.mean = function (items) {
			var result = 0;
			var n = 0;
			items.forEach(function (d) {
				if (d != null) {
					n++;
					result += d;
				}
			});
			return result / n;
		};
		StatMath.sum = function (items) {
			var result = 0;
			items.forEach(function (d) {
				if (d != null) {
					result += d;
				}
			});
			return result;
		};
		StatMath.rlsInterpolation = function (x, y, pow) {
			if (pow < 1) {
				return null;
			}
			var coeffs = [];
			var d = 1000.0;
			for (var iii = 0; iii < pow + 1; iii++) {
				coeffs[iii] = 0.0;
			}
			var pMtx = [];
			for (var ii = 0; ii < pow + 1; ii++) {
				for (var jj = 0; jj < pow + 1; jj++) {
					(pMtx[ii]||(pMtx[ii]=[]))[jj] = (ii === jj) ? d : 0;
				}
			}
			var wV = new StatMath.RealMatrix(coeffs.map(function(t) {return [t];}));
			var pM = new StatMath.RealMatrix(pMtx);
			for (var k = 0; k < x.length; k++) {
				var xx = x[k];
				var yy = y[k];
				var xV = new StatMath.RealMatrix([]);
				var aPow = 1;
				for (var i = 0; i < pow + 1; i++) {
					xV.setEntry(i, 0, aPow);
					aPow *= xx;
				}
				var alpha = yy - wV.transpose().multiply(xV).getEntry(0, 0);
				var gV = pM.multiply(xV).scalarMultiply(1 / (1.0 + xV.transpose().multiply(pM).multiply(xV).getEntry(0, 0)));
				pM = pM.subtract(gV.multiply(xV.transpose()).multiply(pM));
				wV = wV.add(gV.scalarMultiply(alpha));
			}
			return wV.getColumn(0);
		};
		StatMath.obsPercWeight = function (weeks, weight) {
			var p10 = StatMath.polValue(weeks, StatMath.obsW10x());
			var p25 = StatMath.polValue(weeks, StatMath.obsW25x());
			var p50 = StatMath.polValue(weeks, StatMath.obsW50x());
			var p75 = StatMath.polValue(weeks, StatMath.obsW75x());
			var p90 = StatMath.polValue(weeks, StatMath.obsW90x());
			var cs = StatMath.rlsInterpolation([p10, p25, p50, p75, p90], [10.0, 25.0, 50.0, 75.0, 90.0], 3);
			return StatMath.polValue(weight, cs) / 100.0;
		};
		StatMath.obsWeightPerc = function (weeks, perc) {
			var p10 = StatMath.polValue(weeks, StatMath.obsW10x());
			var p25 = StatMath.polValue(weeks, StatMath.obsW25x());
			var p50 = StatMath.polValue(weeks, StatMath.obsW50x());
			var p75 = StatMath.polValue(weeks, StatMath.obsW75x());
			var p90 = StatMath.polValue(weeks, StatMath.obsW90x());
			var cs = StatMath.rlsInterpolation([10.0, 25.0, 50.0, 75.0, 90.0], [p10, p25, p50, p75, p90], 3);
			return StatMath.polValue(perc * 100.0, cs);
		};
		StatMath.obsWeights = function (ac, hc, bipd, fl) {
			var results = {};
			var results1 = {};
			var results2 = {};
			var results3 = {};
			var results4 = {};
			if (ac != null) {
				results1.put("Jordaan", Math.pow(10, (0.6328 + 0.01881 * ac - 4.3E-5 * ac * ac + 3.6239E-8 * Math.pow(ac, 3))));
				results1.put("Higginbottom", 0.0816 * Math.pow(ac, 3) / 1000);
				results1.put("Campbell", Math.pow(2.718281, -4.564 + 0.0282 * ac - 3.31E-5 * ac * ac) * 1000);
				results1.put("Hadlock AC", Math.pow(2.718281, 2.695 + 0.0253 * ac - 2.75E-5 * Math.pow(ac, 2)));
				results1.put("Warsof", Math.pow(10, -1.8367 + 0.092 * ac / 10 - 1.9E-5 * Math.pow(ac, 3) / 1000) * 1000);
			}
			if (fl != null) {
				results1.put("Warsof et al. 1986", Math.pow(2.718281, 4.6914 + 0.00151 * Math.pow(fl, 2) - 1.19E-5 * Math.pow(fl, 3)));
			}
			if (ac != null && fl != null) {
				results2.put("Woo et al. 1985", Math.pow(10, 0.59 + 0.008 * ac + 0.028 * fl - 7.16E-5 * ac * fl));
				results2.put("Hadlock et al. 1985", Math.pow(10, (1.304 + 0.005281 * ac + 0.01938 * fl - 4.0E-5 * ac * fl)));
			}
			if (ac != null && bipd != null) {
				results2.put("Hsieh et al. 1987", Math.pow(10, 2.1315 + 5.6541E-5 * ac * bipd - 1.5515E-7 * bipd * Math.pow(ac, 2) + 1.9782E-8 * Math.pow(ac, 3) + 0.0052594 * bipd));
				results2.put("Vintzileos et al. 1987", Math.pow(10, 1.879 + 0.0084 * bipd + 0.0026 * ac));
				results2.put("Woo et al. 1985", Math.pow(10, 1.63 + 0.016 * bipd + 1.11E-5 * Math.pow(ac, 2) - 8.59E-8 * bipd * Math.pow(ac, 2)));
				results2.put("Hadlock et al. 1984", Math.pow(10, 1.1134 + 0.005845 * ac - 6.04E-6 * Math.pow(ac, 2) - 7.365E-5 * Math.pow(bipd, 2) + 5.95E-6 * bipd * ac + 0.01694 * bipd));
				results2.put("Jordaan. 1983", Math.pow(10, -1.1683 + 0.00377 * ac + 0.0095 * bipd - 1.5E-5 * bipd * ac) * 1000);
				results2.put("Warsof et al. 1977", (Math.pow(10, -1.599 + 0.0144 * bipd + 0.0032 * ac - 1.11E-7 * Math.pow(bipd, 2) * ac) * 1000));
				results2.put("Shepard et al. 1982", (Math.pow(10, -1.7492 + 0.0166 * bipd + 0.0046 * ac - 2.546E-5 * ac * bipd) * 1000));
			}
			if (ac != null && hc != null) {
				results2.put("Hadlock", Math.pow(10, 1.182 + 0.0273 * hc / 10 + 0.07057 * ac / 10 - 6.3E-4 * ac * ac / 100 - 2.184E-4 * ac * ac / 100));
				results2.put("Jordan", Math.pow(10, 0.9119 + 0.00488 * hc + 0.00824 * ac - 1.599E-5 * hc * ac));
			}
			if (ac != null && bipd != null && fl != null) {
				results3.put("Hadlock et al. 1985", Math.pow(10, 1.335 - 3.4E-5 * ac * fl + 0.00316 * bipd + 0.00457 * ac + 0.01623 * fl));
				results3.put("Hsieh et al. 1987", Math.pow(10, 2.7193 + 9.4962E-5 * ac * bipd - 0.01432 * fl - 7.6742E-7 * ac * Math.pow(bipd, 2) + 1.745E-6 * fl * Math.pow(bipd, 2)));
				results3.put("Shinozuka et al. 1987", 2.3966E-4 * Math.pow(ac, 2) * fl + 0.001623 * Math.pow(bipd, 3));
				results3.put("Woo et al. 1985", Math.pow(10, 1.54 + 0.015 * bipd + 1.11E-5 * Math.pow(ac, 2) - 7.64E-8 * bipd * Math.pow(ac, 2) + 0.005 * fl - 9.92E-6 * fl * ac));
			}
			if (ac != null && hc != null && bipd != null) {
				results3.put("Jordaan. 1983", Math.pow(10, 2.3231 + 0.002904 * ac + 7.9E-4 * (hc) - 5.8E-4 * bipd));
			}
			if (ac != null && hc != null && fl != null) {
				results3.put("Hadlock et al. 1985", Math.pow(10, 1.326 - 3.26E-5 * ac * fl + 0.00107 * (hc) + 0.00438 * ac + 0.0158 * fl));
				results3.put("Hadlock et al. 1985", Math.pow(10, 1.326 - 3.26E-5 * ac * fl + 0.00107 * (hc) + 0.00438 * ac + 0.0158 * fl));
				results3.put("Hadlock et al. 1985", Math.pow(10, 1.326 - 3.26E-5 * ac * fl + 0.00107 * (hc) + 0.00438 * ac + 0.0158 * fl));
				results3.put("Combs et al. 1993", 2.3718E-4 * Math.pow(ac, 2) * fl + 3.312E-5 * Math.pow((hc), 3));
				results3.put("Ott et al. 1986", Math.pow(10, -2.0661 + 0.004355 * hc + 0.005394 * ac - 8.582E-6 * hc * ac + 1.2594 * (fl / ac)) * 1000);
				results3.put("Hadlock et al. 1985", Math.pow(10, 1.326 - 3.26E-5 * ac * fl + 0.00107 * (hc) + 0.00438 * ac + 0.0158 * fl));
			}
			if (ac != null && hc != null && fl != null && bipd != null) {
				results4.put("Hadlock et al. 1985", Math.pow(10, 1.3596 + 6.4E-4 * (hc) + 0.00424 * ac + 0.0174 * fl + 6.1E-6 * bipd * ac - 3.86E-5 * ac * fl));
			}
			var mean = null;
			if (results4.size() > 0) {
				mean = (StatMath.mean(results4.values()) * 4 + StatMath.sum(results3.values())) / (4 + results3.size());
			}
			else if (results3.size() > 0) {
				mean = (StatMath.mean(results3.values()) * 4 + StatMath.sum(results2.values())) / (4 + results2.size());
			}
			else if (results2.size() > 0) {
				mean = (StatMath.mean(results2.values()) * 4 + StatMath.sum(results1.values())) / (4 + results1.size());
			}
			else if (results1.size() > 0) {
				mean = StatMath.mean(results1.values());
			}
			Object.keys(results1).forEach(function(k) {results[k]=results1[k];});
			Object.keys(results2).forEach(function(k) {results[k]=results2[k];});
			Object.keys(results3).forEach(function(k) {results[k]=results3[k];});
			Object.keys(results4).forEach(function(k) {results[k]=results4[k];});

			if (mean != null) {
				results.put("mean", mean);
			}
			return results;
		};
		StatMath.percentile = function (desc, x, y) {
			var prevperc = null;
			var prevval = null;
			var result = null;
			{
				var definitions = desc.split(/\|/);
				for (var index2625 = 0; index2625 < definitions.length; index2625++) {
					var row = definitions[index2625];
					{
						var perc = parseFloat(row.split(">")[0]);
						var vals = row.split(">")[1];
						var val = StatMath.interpolate(vals, x);
						print("v="+val);
						if (val >= y) {
							if (prevval != null) {
								result = ((y - prevval) * perc + (val - y) * prevperc) / (val - prevval);
							}
							else {
								result = perc;
							}
							break;
						}
						prevperc = perc;
						prevval = val;
					}
				}
			}
			if (result == null) {
				return prevperc;
			}
			return result;
		};

		StatMath.interpolate = function (vals, x) {
			var val = null;
			var preva = null;
			var prevb = null;
			{
				var array2628 = vals.split(";");
				for (var index2627 = 0; index2627 < array2628.length; index2627++) {
					var pair = array2628[index2627];
					{
						var a = parseFloat(pair.split(",")[0]);
						var b = parseFloat(pair.split(",")[1]);
						if (a >= x) {
							if (preva != null) {
								val = ((x - preva) * b + (a - x) * prevb) / (a - preva);
							}
							else {
								val = b;
							}
							break;
						}
						preva = a;
						prevb = b;
					}
				}
			}
			if (val == null) {
				val = prevb;
			}
			return val;
		};
		return StatMath;
	}())
};

/*
print(h.StatMath.RealMatrix([[1,2,3],[4,5,6],[7,8,9]]).transpose().data);
print(h.StatMath.RealMatrix([[1,2,3],[4,5,6],[7,8,9]]).multiply(h.StatMath.RealMatrix([[1,4,7],[2,5,8],[3,6,9]])).data);
print(h.StatMath.RealMatrix([[1,2,3],[4,5,6],[7,8,9]]).add(h.StatMath.RealMatrix([[1,4,7],[2,5,8],[3,6,9]])).data);
print(h.StatMath.RealMatrix([[1,2,3],[4,5,6],[7,8,9]]).subtract(h.StatMath.RealMatrix([[1,4,7],[2,5,8],[3,6,9]])).data);
print(h.StatMath.RealMatrix([[1,2,3],[4,5,6],[7,8,9]]).scalarMultiply(3).data);
print(h.StatMath.interpolate("11,15.36;12,19.40;13,23.30;14,27.14;15,30.89;16,34.53;17,38.12;18,41.58;19,45.00;20,48.22;21,51.43;22,54.53;23,57.51;24,60.42;25,63.25;26,65.94;27,68.55;28,71.03;29,73.50;30,75.80;31,78.00;32,80.16;33,82.14;34,84.07;35,85.90;36,87.61;37,89.24;38,90.70;39,92.10;40,93.45;41,94.00", 15.5));
print(h.StatMath.percentile("3>11,12.08;12,15.81;13,19.47;14,23.05;15,26.56;16,29.97;17,33.32;18,36.55;19,39.76;20,42.85;21,45.86;22,48.79;23,51.63;24,54.38;25,57.04;26,59.62;27,62.12;28,64.50;29,66.84;30,69.07;31,71.22;32,73.30;33,75.24;34,77.14;35,78.94;36,80.64;37,82.27;38,83.78;39,85.22;40,86.57;41,87.00|10>11,13.12;12,16.96;13,20.71;14,24.36;15,27.93;16,31.41;17,34.85;18,38.15;19,41.46;20,44.56;21,47.66;22,50.61;23,53.48;24,56.31;25,59.00;26,61.64;27,64.15;28,66.61;29,68.98;30,71.21;31,73.39;32,75.49;33,77.46;34,79.36;35,81.14;36,82.88;37,84.50;38,86.00;39,87.43;40,88.78;41,89.00|50>11,15.36;12,19.40;13,23.30;14,27.14;15,30.89;16,34.53;17,38.12;18,41.58;19,45.00;20,48.22;21,51.43;22,54.53;23,57.51;24,60.42;25,63.25;26,65.94;27,68.55;28,71.03;29,73.50;30,75.80;31,78.00;32,80.16;33,82.14;34,84.07;35,85.90;36,87.61;37,89.24;38,90.70;39,92.10;40,93.45;41,94.00|90>11,17.60;12,21.81;13,25.92;14,29.92;15,33.82;16,37.62;17,41.35;18,44.97;19,48.52;20,51.90;21,55.23;22,58.44;23,61.54;24,64.57;25,67.48;26,70.24;27,72.92;28,75.52;29,77.97;30,80.37;31,82.63;32,84.80;33,86.84;34,88.80;35,90.61;36,92.35;37,93.97;38,95.42;39,96.86;40,98.13;41,99.00|97>11,18.63;12,22.92;13,27.12;14,31.23;15,35.23;16,39.08;17,42.87;18,46.56;19,50.18;20,53.64;21,57.00;22,60.30;23,63.45;24,66.50;25,69.42;26,72.27;27,75.00;28,77.60;29,80.09;30,82.52;31,84.80;32,87.00;33,89.04;34,91.00;35,92.83;36,94.56;37,96.19;38,97.66;39,99.05;40,100.31;41,101.00|",15, 30.89))
*/
