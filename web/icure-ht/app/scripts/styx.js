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
