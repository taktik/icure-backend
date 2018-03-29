/*
{
	let hcpId = options.hcpId
}

OrExpression
	= head:AndExpression tail:(_ "|" _ AndExpression _)* { return tail.length && {'$type':'UnionFilter', 'filters':[head].concat(tail.map(it=>it[3]))} || head }

AndExpression
	= head:ComparisonExpression tail:(_ "&" _ ComparisonExpression _)* { return tail.length && {'$type':'IntersectionFilter', 'filters':[head].concat(tail.map(it=>it[3]))} || head }

ComparisonExpression
	= neg:"!"? _ "(" _ op:OrExpression _ ")" {
	return neg && {'$type':'ComplementFilter', 'subFilter':op} || op;
}
/ left:Operand _ op:Comparison _ right:Operand {
return op === '==' && (
	left.indexOf(':') === 0 && {'$type':'ServiceByHcPartyTagCodeDateFilter', 'healthcarePartyId':hcpId, 'tagCode':right, 'tagType':left.substr(1)} ||
	{'$type':'ServiceByHcPartyTagCodeDateFilter', 'healthcarePartyId':hcpId, 'codeCode':right, 'codeType':left}
) || (
	{'$type':'ComplementFilter', 'subFilter':left.indexOf(':') === 0 && {'$type':'ServiceByHcPartyTagCodeDateFilter', 'healthcarePartyId':hcpId, 'tagCode':right, 'tagType':left.substr(1)} ||
	{'$type':'ServiceByHcPartyTagCodeDateFilter', 'healthcarePartyId':hcpId, 'codeCode':right, 'codeType':left}}
)
}

Operand = QSTR / STR

Comparison
	= "==" / "!="

STR
	= head:[_a-zA-Z-] tail:[_a-zA-Z0-9-]* { return head + (tail && tail.join('') || '')}

QSTR
	= "\"" str:[^"]* "\"" { return str.join('') }

_ "whitespace"
	= [ \t\n\r]*

*/


;(function(){
	/** Detect free variable `global` from Node.js. */
	var freeGlobal = typeof global == 'object' && global && global.Object === Object && global;
	/** Detect free variable `self`. */
	var freeSelf = typeof self == 'object' && self && self.Object === Object && self;
	/** Used as a reference to the global object. */
	var root = freeGlobal || freeSelf || Function('return this')();
	/** Detect free variable `exports`. */
	var freeExports = typeof exports == 'object' && exports && !exports.nodeType && exports;
	var freeModule = freeExports && typeof module == 'object' && module && !module.nodeType && module;
	/** Detect the popular CommonJS extension `module.exports`. */
	var moduleExports = freeModule && freeModule.exports === freeExports;

	var filterExParser = (function() {
		"use strict";

		function peg$subclass(child, parent) {
			function ctor() { this.constructor = child; }
			ctor.prototype = parent.prototype;
			child.prototype = new ctor();
		}

		function peg$SyntaxError(message, expected, found, location) {
			this.message  = message;
			this.expected = expected;
			this.found    = found;
			this.location = location;
			this.name     = "SyntaxError";

			if (typeof Error.captureStackTrace === "function") {
				Error.captureStackTrace(this, peg$SyntaxError);
			}
		}

		peg$subclass(peg$SyntaxError, Error);

		peg$SyntaxError.buildMessage = function(expected, found) {
			var DESCRIBE_EXPECTATION_FNS = {
				literal: function(expectation) {
					return "\"" + literalEscape(expectation.text) + "\"";
				},

				"class": function(expectation) {
					var escapedParts = "",
						i;

					for (i = 0; i < expectation.parts.length; i++) {
						escapedParts += expectation.parts[i] instanceof Array
							? classEscape(expectation.parts[i][0]) + "-" + classEscape(expectation.parts[i][1])
							: classEscape(expectation.parts[i]);
					}

					return "[" + (expectation.inverted ? "^" : "") + escapedParts + "]";
				},

				any: function(expectation) {
					return "any character";
				},

				end: function(expectation) {
					return "end of input";
				},

				other: function(expectation) {
					return expectation.description;
				}
			};

			function hex(ch) {
				return ch.charCodeAt(0).toString(16).toUpperCase();
			}

			function literalEscape(s) {
				return s
					.replace(/\\/g, '\\\\')
					.replace(/"/g,  '\\"')
					.replace(/\0/g, '\\0')
					.replace(/\t/g, '\\t')
					.replace(/\n/g, '\\n')
					.replace(/\r/g, '\\r')
					.replace(/[\x00-\x0F]/g,          function(ch) { return '\\x0' + hex(ch); })
					.replace(/[\x10-\x1F\x7F-\x9F]/g, function(ch) { return '\\x'  + hex(ch); });
			}

			function classEscape(s) {
				return s
					.replace(/\\/g, '\\\\')
					.replace(/\]/g, '\\]')
					.replace(/\^/g, '\\^')
					.replace(/-/g,  '\\-')
					.replace(/\0/g, '\\0')
					.replace(/\t/g, '\\t')
					.replace(/\n/g, '\\n')
					.replace(/\r/g, '\\r')
					.replace(/[\x00-\x0F]/g,          function(ch) { return '\\x0' + hex(ch); })
					.replace(/[\x10-\x1F\x7F-\x9F]/g, function(ch) { return '\\x'  + hex(ch); });
			}

			function describeExpectation(expectation) {
				return DESCRIBE_EXPECTATION_FNS[expectation.type](expectation);
			}

			function describeExpected(expected) {
				var descriptions = new Array(expected.length),
					i, j;

				for (i = 0; i < expected.length; i++) {
					descriptions[i] = describeExpectation(expected[i]);
				}

				descriptions.sort();

				if (descriptions.length > 0) {
					for (i = 1, j = 1; i < descriptions.length; i++) {
						if (descriptions[i - 1] !== descriptions[i]) {
							descriptions[j] = descriptions[i];
							j++;
						}
					}
					descriptions.length = j;
				}

				switch (descriptions.length) {
					case 1:
						return descriptions[0];

					case 2:
						return descriptions[0] + " or " + descriptions[1];

					default:
						return descriptions.slice(0, -1).join(", ")
							+ ", or "
							+ descriptions[descriptions.length - 1];
				}
			}

			function describeFound(found) {
				return found ? "\"" + literalEscape(found) + "\"" : "end of input";
			}

			return "Expected " + describeExpected(expected) + " but " + describeFound(found) + " found.";
		};

		function peg$parse(input, options) {
			options = options !== void 0 ? options : {};

			var peg$FAILED = {},

				peg$startRuleFunctions = { OrExpression: peg$parseOrExpression },
				peg$startRuleFunction  = peg$parseOrExpression,

				peg$c0 = "|",
				peg$c1 = peg$literalExpectation("|", false),
				peg$c2 = function(head, tail) { return tail.length && {'$type':'UnionFilter', 'filters':[head].concat(tail.map(it=>it[3]))} || head },
				peg$c3 = "&",
				peg$c4 = peg$literalExpectation("&", false),
				peg$c5 = function(head, tail) { return tail.length && {'$type':'IntersectionFilter', 'filters':[head].concat(tail.map(it=>it[3]))} || head },
				peg$c6 = "!",
				peg$c7 = peg$literalExpectation("!", false),
				peg$c8 = "(",
				peg$c9 = peg$literalExpectation("(", false),
				peg$c10 = ")",
				peg$c11 = peg$literalExpectation(")", false),
				peg$c12 = function(neg, op) {
					return neg && {'$type':'ComplementFilter', 'subFilter':op} || op;
				},
				peg$c13 = function(left, op, right) {
					return op === '==' && (
						left.indexOf(':') === 0 && {'$type':'ServiceByHcPartyTagCodeDateFilter', 'healthcarePartyId':hcpId, 'tagCode':right, 'tagType':left.substr(1)} ||
						{'$type':'ServiceByHcPartyTagCodeDateFilter', 'healthcarePartyId':hcpId, 'codeCode':right, 'codeType':left}
					) || (
						{'$type':'ComplementFilter', 'subFilter':left.indexOf(':') === 0 && {'$type':'ServiceByHcPartyTagCodeDateFilter', 'healthcarePartyId':hcpId, 'tagCode':right, 'tagType':left.substr(1)} ||
						{'$type':'ServiceByHcPartyTagCodeDateFilter', 'healthcarePartyId':hcpId, 'codeCode':right, 'codeType':left}}
					)
				},
				peg$c14 = "==",
				peg$c15 = peg$literalExpectation("==", false),
				peg$c16 = "!=",
				peg$c17 = peg$literalExpectation("!=", false),
				peg$c18 = /^[_a-zA-Z\-]/,
				peg$c19 = peg$classExpectation(["_", ["a", "z"], ["A", "Z"], "-"], false, false),
				peg$c20 = /^[_a-zA-Z0-9\-]/,
				peg$c21 = peg$classExpectation(["_", ["a", "z"], ["A", "Z"], ["0", "9"], "-"], false, false),
				peg$c22 = function(head, tail) { return head + (tail && tail.join('') || '')},
				peg$c23 = "\"",
				peg$c24 = peg$literalExpectation("\"", false),
				peg$c25 = /^[^"]/,
				peg$c26 = peg$classExpectation(["\""], true, false),
				peg$c27 = function(str) { return str.join('') },
				peg$c28 = peg$otherExpectation("whitespace"),
				peg$c29 = /^[ \t\n\r]/,
				peg$c30 = peg$classExpectation([" ", "\t", "\n", "\r"], false, false),

				peg$currPos          = 0,
				peg$savedPos         = 0,
				peg$posDetailsCache  = [{ line: 1, column: 1 }],
				peg$maxFailPos       = 0,
				peg$maxFailExpected  = [],
				peg$silentFails      = 0,

				peg$result;

			if ("startRule" in options) {
				if (!(options.startRule in peg$startRuleFunctions)) {
					throw new Error("Can't start parsing from rule \"" + options.startRule + "\".");
				}

				peg$startRuleFunction = peg$startRuleFunctions[options.startRule];
			}

			function text() {
				return input.substring(peg$savedPos, peg$currPos);
			}

			function location() {
				return peg$computeLocation(peg$savedPos, peg$currPos);
			}

			function expected(description, location) {
				location = location !== void 0 ? location : peg$computeLocation(peg$savedPos, peg$currPos)

				throw peg$buildStructuredError(
					[peg$otherExpectation(description)],
					input.substring(peg$savedPos, peg$currPos),
					location
				);
			}

			function error(message, location) {
				location = location !== void 0 ? location : peg$computeLocation(peg$savedPos, peg$currPos)

				throw peg$buildSimpleError(message, location);
			}

			function peg$literalExpectation(text, ignoreCase) {
				return { type: "literal", text: text, ignoreCase: ignoreCase };
			}

			function peg$classExpectation(parts, inverted, ignoreCase) {
				return { type: "class", parts: parts, inverted: inverted, ignoreCase: ignoreCase };
			}

			function peg$anyExpectation() {
				return { type: "any" };
			}

			function peg$endExpectation() {
				return { type: "end" };
			}

			function peg$otherExpectation(description) {
				return { type: "other", description: description };
			}

			function peg$computePosDetails(pos) {
				var details = peg$posDetailsCache[pos], p;

				if (details) {
					return details;
				} else {
					p = pos - 1;
					while (!peg$posDetailsCache[p]) {
						p--;
					}

					details = peg$posDetailsCache[p];
					details = {
						line:   details.line,
						column: details.column
					};

					while (p < pos) {
						if (input.charCodeAt(p) === 10) {
							details.line++;
							details.column = 1;
						} else {
							details.column++;
						}

						p++;
					}

					peg$posDetailsCache[pos] = details;
					return details;
				}
			}

			function peg$computeLocation(startPos, endPos) {
				var startPosDetails = peg$computePosDetails(startPos),
					endPosDetails   = peg$computePosDetails(endPos);

				return {
					start: {
						offset: startPos,
						line:   startPosDetails.line,
						column: startPosDetails.column
					},
					end: {
						offset: endPos,
						line:   endPosDetails.line,
						column: endPosDetails.column
					}
				};
			}

			function peg$fail(expected) {
				if (peg$currPos < peg$maxFailPos) { return; }

				if (peg$currPos > peg$maxFailPos) {
					peg$maxFailPos = peg$currPos;
					peg$maxFailExpected = [];
				}

				peg$maxFailExpected.push(expected);
			}

			function peg$buildSimpleError(message, location) {
				return new peg$SyntaxError(message, null, null, location);
			}

			function peg$buildStructuredError(expected, found, location) {
				return new peg$SyntaxError(
					peg$SyntaxError.buildMessage(expected, found),
					expected,
					found,
					location
				);
			}

			function peg$parseOrExpression() {
				var s0, s1, s2, s3, s4, s5, s6, s7, s8;

				s0 = peg$currPos;
				s1 = peg$parseAndExpression();
				if (s1 !== peg$FAILED) {
					s2 = [];
					s3 = peg$currPos;
					s4 = peg$parse_();
					if (s4 !== peg$FAILED) {
						if (input.charCodeAt(peg$currPos) === 124) {
							s5 = peg$c0;
							peg$currPos++;
						} else {
							s5 = peg$FAILED;
							if (peg$silentFails === 0) { peg$fail(peg$c1); }
						}
						if (s5 !== peg$FAILED) {
							s6 = peg$parse_();
							if (s6 !== peg$FAILED) {
								s7 = peg$parseAndExpression();
								if (s7 !== peg$FAILED) {
									s8 = peg$parse_();
									if (s8 !== peg$FAILED) {
										s4 = [s4, s5, s6, s7, s8];
										s3 = s4;
									} else {
										peg$currPos = s3;
										s3 = peg$FAILED;
									}
								} else {
									peg$currPos = s3;
									s3 = peg$FAILED;
								}
							} else {
								peg$currPos = s3;
								s3 = peg$FAILED;
							}
						} else {
							peg$currPos = s3;
							s3 = peg$FAILED;
						}
					} else {
						peg$currPos = s3;
						s3 = peg$FAILED;
					}
					while (s3 !== peg$FAILED) {
						s2.push(s3);
						s3 = peg$currPos;
						s4 = peg$parse_();
						if (s4 !== peg$FAILED) {
							if (input.charCodeAt(peg$currPos) === 124) {
								s5 = peg$c0;
								peg$currPos++;
							} else {
								s5 = peg$FAILED;
								if (peg$silentFails === 0) { peg$fail(peg$c1); }
							}
							if (s5 !== peg$FAILED) {
								s6 = peg$parse_();
								if (s6 !== peg$FAILED) {
									s7 = peg$parseAndExpression();
									if (s7 !== peg$FAILED) {
										s8 = peg$parse_();
										if (s8 !== peg$FAILED) {
											s4 = [s4, s5, s6, s7, s8];
											s3 = s4;
										} else {
											peg$currPos = s3;
											s3 = peg$FAILED;
										}
									} else {
										peg$currPos = s3;
										s3 = peg$FAILED;
									}
								} else {
									peg$currPos = s3;
									s3 = peg$FAILED;
								}
							} else {
								peg$currPos = s3;
								s3 = peg$FAILED;
							}
						} else {
							peg$currPos = s3;
							s3 = peg$FAILED;
						}
					}
					if (s2 !== peg$FAILED) {
						peg$savedPos = s0;
						s1 = peg$c2(s1, s2);
						s0 = s1;
					} else {
						peg$currPos = s0;
						s0 = peg$FAILED;
					}
				} else {
					peg$currPos = s0;
					s0 = peg$FAILED;
				}

				return s0;
			}

			function peg$parseAndExpression() {
				var s0, s1, s2, s3, s4, s5, s6, s7, s8;

				s0 = peg$currPos;
				s1 = peg$parseComparisonExpression();
				if (s1 !== peg$FAILED) {
					s2 = [];
					s3 = peg$currPos;
					s4 = peg$parse_();
					if (s4 !== peg$FAILED) {
						if (input.charCodeAt(peg$currPos) === 38) {
							s5 = peg$c3;
							peg$currPos++;
						} else {
							s5 = peg$FAILED;
							if (peg$silentFails === 0) { peg$fail(peg$c4); }
						}
						if (s5 !== peg$FAILED) {
							s6 = peg$parse_();
							if (s6 !== peg$FAILED) {
								s7 = peg$parseComparisonExpression();
								if (s7 !== peg$FAILED) {
									s8 = peg$parse_();
									if (s8 !== peg$FAILED) {
										s4 = [s4, s5, s6, s7, s8];
										s3 = s4;
									} else {
										peg$currPos = s3;
										s3 = peg$FAILED;
									}
								} else {
									peg$currPos = s3;
									s3 = peg$FAILED;
								}
							} else {
								peg$currPos = s3;
								s3 = peg$FAILED;
							}
						} else {
							peg$currPos = s3;
							s3 = peg$FAILED;
						}
					} else {
						peg$currPos = s3;
						s3 = peg$FAILED;
					}
					while (s3 !== peg$FAILED) {
						s2.push(s3);
						s3 = peg$currPos;
						s4 = peg$parse_();
						if (s4 !== peg$FAILED) {
							if (input.charCodeAt(peg$currPos) === 38) {
								s5 = peg$c3;
								peg$currPos++;
							} else {
								s5 = peg$FAILED;
								if (peg$silentFails === 0) { peg$fail(peg$c4); }
							}
							if (s5 !== peg$FAILED) {
								s6 = peg$parse_();
								if (s6 !== peg$FAILED) {
									s7 = peg$parseComparisonExpression();
									if (s7 !== peg$FAILED) {
										s8 = peg$parse_();
										if (s8 !== peg$FAILED) {
											s4 = [s4, s5, s6, s7, s8];
											s3 = s4;
										} else {
											peg$currPos = s3;
											s3 = peg$FAILED;
										}
									} else {
										peg$currPos = s3;
										s3 = peg$FAILED;
									}
								} else {
									peg$currPos = s3;
									s3 = peg$FAILED;
								}
							} else {
								peg$currPos = s3;
								s3 = peg$FAILED;
							}
						} else {
							peg$currPos = s3;
							s3 = peg$FAILED;
						}
					}
					if (s2 !== peg$FAILED) {
						peg$savedPos = s0;
						s1 = peg$c5(s1, s2);
						s0 = s1;
					} else {
						peg$currPos = s0;
						s0 = peg$FAILED;
					}
				} else {
					peg$currPos = s0;
					s0 = peg$FAILED;
				}

				return s0;
			}

			function peg$parseComparisonExpression() {
				var s0, s1, s2, s3, s4, s5, s6, s7;

				s0 = peg$currPos;
				if (input.charCodeAt(peg$currPos) === 33) {
					s1 = peg$c6;
					peg$currPos++;
				} else {
					s1 = peg$FAILED;
					if (peg$silentFails === 0) { peg$fail(peg$c7); }
				}
				if (s1 === peg$FAILED) {
					s1 = null;
				}
				if (s1 !== peg$FAILED) {
					s2 = peg$parse_();
					if (s2 !== peg$FAILED) {
						if (input.charCodeAt(peg$currPos) === 40) {
							s3 = peg$c8;
							peg$currPos++;
						} else {
							s3 = peg$FAILED;
							if (peg$silentFails === 0) { peg$fail(peg$c9); }
						}
						if (s3 !== peg$FAILED) {
							s4 = peg$parse_();
							if (s4 !== peg$FAILED) {
								s5 = peg$parseOrExpression();
								if (s5 !== peg$FAILED) {
									s6 = peg$parse_();
									if (s6 !== peg$FAILED) {
										if (input.charCodeAt(peg$currPos) === 41) {
											s7 = peg$c10;
											peg$currPos++;
										} else {
											s7 = peg$FAILED;
											if (peg$silentFails === 0) { peg$fail(peg$c11); }
										}
										if (s7 !== peg$FAILED) {
											peg$savedPos = s0;
											s1 = peg$c12(s1, s5);
											s0 = s1;
										} else {
											peg$currPos = s0;
											s0 = peg$FAILED;
										}
									} else {
										peg$currPos = s0;
										s0 = peg$FAILED;
									}
								} else {
									peg$currPos = s0;
									s0 = peg$FAILED;
								}
							} else {
								peg$currPos = s0;
								s0 = peg$FAILED;
							}
						} else {
							peg$currPos = s0;
							s0 = peg$FAILED;
						}
					} else {
						peg$currPos = s0;
						s0 = peg$FAILED;
					}
				} else {
					peg$currPos = s0;
					s0 = peg$FAILED;
				}
				if (s0 === peg$FAILED) {
					s0 = peg$currPos;
					s1 = peg$parseOperand();
					if (s1 !== peg$FAILED) {
						s2 = peg$parse_();
						if (s2 !== peg$FAILED) {
							s3 = peg$parseComparison();
							if (s3 !== peg$FAILED) {
								s4 = peg$parse_();
								if (s4 !== peg$FAILED) {
									s5 = peg$parseOperand();
									if (s5 !== peg$FAILED) {
										peg$savedPos = s0;
										s1 = peg$c13(s1, s3, s5);
										s0 = s1;
									} else {
										peg$currPos = s0;
										s0 = peg$FAILED;
									}
								} else {
									peg$currPos = s0;
									s0 = peg$FAILED;
								}
							} else {
								peg$currPos = s0;
								s0 = peg$FAILED;
							}
						} else {
							peg$currPos = s0;
							s0 = peg$FAILED;
						}
					} else {
						peg$currPos = s0;
						s0 = peg$FAILED;
					}
				}

				return s0;
			}

			function peg$parseOperand() {
				var s0;

				s0 = peg$parseQSTR();
				if (s0 === peg$FAILED) {
					s0 = peg$parseSTR();
				}

				return s0;
			}

			function peg$parseComparison() {
				var s0;

				if (input.substr(peg$currPos, 2) === peg$c14) {
					s0 = peg$c14;
					peg$currPos += 2;
				} else {
					s0 = peg$FAILED;
					if (peg$silentFails === 0) { peg$fail(peg$c15); }
				}
				if (s0 === peg$FAILED) {
					if (input.substr(peg$currPos, 2) === peg$c16) {
						s0 = peg$c16;
						peg$currPos += 2;
					} else {
						s0 = peg$FAILED;
						if (peg$silentFails === 0) { peg$fail(peg$c17); }
					}
				}

				return s0;
			}

			function peg$parseSTR() {
				var s0, s1, s2, s3;

				s0 = peg$currPos;
				if (peg$c18.test(input.charAt(peg$currPos))) {
					s1 = input.charAt(peg$currPos);
					peg$currPos++;
				} else {
					s1 = peg$FAILED;
					if (peg$silentFails === 0) { peg$fail(peg$c19); }
				}
				if (s1 !== peg$FAILED) {
					s2 = [];
					if (peg$c20.test(input.charAt(peg$currPos))) {
						s3 = input.charAt(peg$currPos);
						peg$currPos++;
					} else {
						s3 = peg$FAILED;
						if (peg$silentFails === 0) { peg$fail(peg$c21); }
					}
					while (s3 !== peg$FAILED) {
						s2.push(s3);
						if (peg$c20.test(input.charAt(peg$currPos))) {
							s3 = input.charAt(peg$currPos);
							peg$currPos++;
						} else {
							s3 = peg$FAILED;
							if (peg$silentFails === 0) { peg$fail(peg$c21); }
						}
					}
					if (s2 !== peg$FAILED) {
						peg$savedPos = s0;
						s1 = peg$c22(s1, s2);
						s0 = s1;
					} else {
						peg$currPos = s0;
						s0 = peg$FAILED;
					}
				} else {
					peg$currPos = s0;
					s0 = peg$FAILED;
				}

				return s0;
			}

			function peg$parseQSTR() {
				var s0, s1, s2, s3;

				s0 = peg$currPos;
				if (input.charCodeAt(peg$currPos) === 34) {
					s1 = peg$c23;
					peg$currPos++;
				} else {
					s1 = peg$FAILED;
					if (peg$silentFails === 0) { peg$fail(peg$c24); }
				}
				if (s1 !== peg$FAILED) {
					s2 = [];
					if (peg$c25.test(input.charAt(peg$currPos))) {
						s3 = input.charAt(peg$currPos);
						peg$currPos++;
					} else {
						s3 = peg$FAILED;
						if (peg$silentFails === 0) { peg$fail(peg$c26); }
					}
					while (s3 !== peg$FAILED) {
						s2.push(s3);
						if (peg$c25.test(input.charAt(peg$currPos))) {
							s3 = input.charAt(peg$currPos);
							peg$currPos++;
						} else {
							s3 = peg$FAILED;
							if (peg$silentFails === 0) { peg$fail(peg$c26); }
						}
					}
					if (s2 !== peg$FAILED) {
						if (input.charCodeAt(peg$currPos) === 34) {
							s3 = peg$c23;
							peg$currPos++;
						} else {
							s3 = peg$FAILED;
							if (peg$silentFails === 0) { peg$fail(peg$c24); }
						}
						if (s3 !== peg$FAILED) {
							peg$savedPos = s0;
							s1 = peg$c27(s2);
							s0 = s1;
						} else {
							peg$currPos = s0;
							s0 = peg$FAILED;
						}
					} else {
						peg$currPos = s0;
						s0 = peg$FAILED;
					}
				} else {
					peg$currPos = s0;
					s0 = peg$FAILED;
				}

				return s0;
			}

			function peg$parse_() {
				var s0, s1;

				peg$silentFails++;
				s0 = [];
				if (peg$c29.test(input.charAt(peg$currPos))) {
					s1 = input.charAt(peg$currPos);
					peg$currPos++;
				} else {
					s1 = peg$FAILED;
					if (peg$silentFails === 0) { peg$fail(peg$c30); }
				}
				while (s1 !== peg$FAILED) {
					s0.push(s1);
					if (peg$c29.test(input.charAt(peg$currPos))) {
						s1 = input.charAt(peg$currPos);
						peg$currPos++;
					} else {
						s1 = peg$FAILED;
						if (peg$silentFails === 0) { peg$fail(peg$c30); }
					}
				}
				peg$silentFails--;
				if (s0 === peg$FAILED) {
					s1 = peg$FAILED;
					if (peg$silentFails === 0) { peg$fail(peg$c28); }
				}

				return s0;
			}


			let hcpId = options.hcpId


			peg$result = peg$startRuleFunction();

			if (peg$result !== peg$FAILED && peg$currPos === input.length) {
				return peg$result;
			} else {
				if (peg$result !== peg$FAILED && peg$currPos < input.length) {
					peg$fail(peg$endExpectation());
				}

				throw peg$buildStructuredError(
					peg$maxFailExpected,
					peg$maxFailPos < input.length ? input.charAt(peg$maxFailPos) : null,
					peg$maxFailPos < input.length
						? peg$computeLocation(peg$maxFailPos, peg$maxFailPos + 1)
						: peg$computeLocation(peg$maxFailPos, peg$maxFailPos)
				);
			}
		}

		return {
			SyntaxError: peg$SyntaxError,
			parse:       peg$parse
		};
	})()

	// Some AMD build optimizers, like r.js, check for condition patterns like:
	if (typeof define == 'function' && typeof define.amd == 'object' && define.amd) {
		// Expose Lodash on the global object to prevent errors when Lodash is
		// loaded by a script tag in the presence of an AMD loader.
		// See http://requirejs.org/docs/errors.html#mismatch for more details.
		// Use `filterExParser.noConflict` to remove Lodash from the global object.
		root.filterExParser = filterExParser;

		// Define as an anonymous module so, through path mapping, it can be
		// referenced as the "underscore" module.
		define(function() {
			return filterExParser;
		});
	}
	// Check for `exports` after `define` in case a build optimizer adds it.
	else if (freeModule) {
		// Export for Node.js.
		(freeModule.exports = filterExParser).filterExParser = filterExParser;
		// Export for CommonJS support.
		freeExports.filterExParser = filterExParser;
	} else {
		// Export to the global object.
		root.filterExParser = filterExParser;
	}
}.call(this));
