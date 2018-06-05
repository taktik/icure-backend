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

	var styx = {
		parent: function(el, sel) {
			let pEl = el
			while(pEl.parentElement && pEl.parentElement !== pEl.parentElement.parentElement) {
				if (sel(pEl.parentElement)) { return pEl.parentElement }
				pEl = pEl.parentElement
			}
			return undefined
		},
        adler32: function (buf, adler, len, pos) {
			if (adler === undefined) { adler = 1 }
            var s1 = (adler & 0xffff) | 0,
                s2 = ((adler >>> 16) & 0xffff) | 0,
                n = 0

	        len = len || buf.length
	        pos = pos || 0

            while (len !== 0) {
                // Set limit ~ twice less than 5552, to keep
                // s2 in 31-bits, because we force signed ints.
                // in other case %= will fail.
                n = len > 2000 ? 2000 : len
                len -= n

                do {
                    s1 = (s1 + buf[pos++]) | 0
                    s2 = (s2 + s1) | 0
                } while (--n)

                s1 %= 65521
                s2 %= 65521
            }

            return (s1 | (s2 << 16)) | 0
        }
	}

	// Some AMD build optimizers, like r.js, check for condition patterns like:
	if (typeof define == 'function' && typeof define.amd == 'object' && define.amd) {
		// Expose Lodash on the global object to prevent errors when Lodash is
		// loaded by a script tag in the presence of an AMD loader.
		// See http://requirejs.org/docs/errors.html#mismatch for more details.
		// Use `styx.noConflict` to remove Lodash from the global object.
		root.styx = styx;

		// Define as an anonymous module so, through path mapping, it can be
		// referenced as the "underscore" module.
		define(function() {
			return styx;
		});
	}
	// Check for `exports` after `define` in case a build optimizer adds it.
	else if (freeModule) {
		// Export for Node.js.
		(freeModule.exports = styx).styx = styx;
		// Export for CommonJS support.
		freeExports.styx = styx;
	} else {
		// Export to the global object.
		root.styx = styx;
	}
}.call(this));
