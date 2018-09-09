/**
@license
Copyright (c) 2016 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/
import './shared-styles.js';

/**
 * @file Web Cryptography API shim
 * @author Artem S Vybornov <vybornov@gmail.com>
 * @license MIT
 */
(function (global, factory) {
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define([], function () {
            return factory(global);
        });
    } else if (typeof module === 'object' && module.exports) {
        // CommonJS-like environments that support module.exports
        module.exports = factory(global);
    } else {
        factory(global);
    }
}(typeof self !== 'undefined' ? self : window, function (global) {
    'use strict';

    if ( typeof Promise !== 'function' )
        throw "Promise support required";

    var _crypto = global.crypto || global.msCrypto;
    if ( !_crypto ) return;

    var _subtle = _crypto.subtle || _crypto.webkitSubtle;
    if ( !_subtle ) return;

    var _Crypto     = global.Crypto || _crypto.constructor || Object,
        _SubtleCrypto = global.SubtleCrypto || _subtle.constructor || Object,
        _CryptoKey  = global.CryptoKey || global.Key || Object;

    var isEdge = global.navigator.userAgent.indexOf('Edge/') > -1;
    var isIE    = !!global.msCrypto && !isEdge;
    var isWebkit = !_crypto.subtle && !!_crypto.webkitSubtle;
    if ( !isIE && !isWebkit ) return;

    function s2a ( s ) {
        return btoa(s).replace(/\=+$/, '').replace(/\+/g, '-').replace(/\//g, '_');
    }

    function a2s ( s ) {
        s += '===', s = s.slice( 0, -s.length % 4 );
        return atob( s.replace(/-/g, '+').replace(/_/g, '/') );
    }

    function s2b ( s ) {
        var b = new Uint8Array(s.length);
        for ( var i = 0; i < s.length; i++ ) b[i] = s.charCodeAt(i);
        return b;
    }

    function b2s ( b ) {
        if ( b instanceof ArrayBuffer ) b = new Uint8Array(b);
        return String.fromCharCode.apply( String, b );
    }

    function alg ( a ) {
        var r = { 'name': (a.name || a || '').toUpperCase().replace('V','v') };
        switch ( r.name ) {
            case 'SHA-1':
            case 'SHA-256':
            case 'SHA-384':
            case 'SHA-512':
                break;
            case 'AES-CBC':
            case 'AES-GCM':
            case 'AES-KW':
                if ( a.length ) r['length'] = a.length;
                break;
            case 'HMAC':
                if ( a.hash ) r['hash'] = alg(a.hash);
                if ( a.length ) r['length'] = a.length;
                break;
            case 'RSAES-PKCS1-v1_5':
                if ( a.publicExponent ) r['publicExponent'] = new Uint8Array(a.publicExponent);
                if ( a.modulusLength ) r['modulusLength'] = a.modulusLength;
                break;
            case 'RSASSA-PKCS1-v1_5':
            case 'RSA-OAEP':
                if ( a.hash ) r['hash'] = alg(a.hash);
                if ( a.publicExponent ) r['publicExponent'] = new Uint8Array(a.publicExponent);
                if ( a.modulusLength ) r['modulusLength'] = a.modulusLength;
                break;
            default:
                throw new SyntaxError("Bad algorithm name");
        }
        return r;
    };

    function jwkAlg ( a ) {
        return {
            'HMAC': {
                'SHA-1': 'HS1',
                'SHA-256': 'HS256',
                'SHA-384': 'HS384',
                'SHA-512': 'HS512',
            },
            'RSASSA-PKCS1-v1_5': {
                'SHA-1': 'RS1',
                'SHA-256': 'RS256',
                'SHA-384': 'RS384',
                'SHA-512': 'RS512',
            },
            'RSAES-PKCS1-v1_5': {
                '': 'RSA1_5',
            },
            'RSA-OAEP': {
                'SHA-1': 'RSA-OAEP',
                'SHA-256': 'RSA-OAEP-256',
            },
            'AES-KW': {
                '128': 'A128KW',
                '192': 'A192KW',
                '256': 'A256KW',
            },
            'AES-GCM': {
                '128': 'A128GCM',
                '192': 'A192GCM',
                '256': 'A256GCM',
            },
            'AES-CBC': {
                '128': 'A128CBC',
                '192': 'A192CBC',
                '256': 'A256CBC',
            },
        }[a.name][ ( a.hash || {} ).name || a.length || '' ];
    }

    function b2jwk ( k ) {
        if ( k instanceof ArrayBuffer || k instanceof Uint8Array ) k = JSON.parse( decodeURIComponent( escape( b2s(k) ) ) );
        var jwk = { 'kty': k.kty, 'alg': k.alg, 'ext': k.ext || k.extractable };
        switch ( jwk.kty ) {
            case 'oct':
                jwk.k = k.k;
            case 'RSA':
                [ 'n', 'e', 'd', 'p', 'q', 'dp', 'dq', 'qi', 'oth' ].forEach( function ( x ) { if ( x in k ) jwk[x] = k[x] } );
                break;
            default:
                throw new TypeError("Unsupported key type");
        }
        return jwk;
    }

    function jwk2b ( k ) {
        var jwk = b2jwk(k);
        if ( isIE ) jwk['extractable'] = jwk.ext, delete jwk.ext;
        return s2b( unescape( encodeURIComponent( JSON.stringify(jwk) ) ) ).buffer;
    }

    function pkcs2jwk ( k ) {
        var info = b2der(k), prv = false;
        if ( info.length > 2 ) prv = true, info.shift(); // remove version from PKCS#8 PrivateKeyInfo structure
        var jwk = { 'ext': true };
        switch ( info[0][0] ) {
            case '1.2.840.113549.1.1.1':
                var rsaComp = [ 'n', 'e', 'd', 'p', 'q', 'dp', 'dq', 'qi' ],
                    rsaKey  = b2der( info[1] );
                if ( prv ) rsaKey.shift(); // remove version from PKCS#1 RSAPrivateKey structure
                for ( var i = 0; i < rsaKey.length; i++ ) {
                    if ( !rsaKey[i][0] ) rsaKey[i] = rsaKey[i].subarray(1);
                    jwk[ rsaComp[i] ] = s2a( b2s( rsaKey[i] ) );
                }
                jwk['kty'] = 'RSA';
                break;
            default:
                throw new TypeError("Unsupported key type");
        }
        return jwk;
    }

    function jwk2pkcs ( k ) {
        var key, info = [ [ '', null ] ], prv = false;
        switch ( k.kty ) {
            case 'RSA':
                var rsaComp = [ 'n', 'e', 'd', 'p', 'q', 'dp', 'dq', 'qi' ],
                    rsaKey = [];
                for ( var i = 0; i < rsaComp.length; i++ ) {
                    if ( !( rsaComp[i] in k ) ) break;
                    var b = rsaKey[i] = s2b( a2s( k[ rsaComp[i] ] ) );
                    if ( b[0] & 0x80 ) rsaKey[i] = new Uint8Array(b.length + 1), rsaKey[i].set( b, 1 );
                }
                if ( rsaKey.length > 2 ) prv = true, rsaKey.unshift( new Uint8Array([0]) ); // add version to PKCS#1 RSAPrivateKey structure
                info[0][0] = '1.2.840.113549.1.1.1';
                key = rsaKey;
                break;
            default:
                throw new TypeError("Unsupported key type");
        }
        info.push( new Uint8Array( der2b(key) ).buffer );
        if ( !prv ) info[1] = { 'tag': 0x03, 'value': info[1] };
        else info.unshift( new Uint8Array([0]) ); // add version to PKCS#8 PrivateKeyInfo structure
        return new Uint8Array( der2b(info) ).buffer;
    }

    var oid2str = { 'KoZIhvcNAQEB': '1.2.840.113549.1.1.1' },
        str2oid = { '1.2.840.113549.1.1.1': 'KoZIhvcNAQEB' };

    function b2der ( buf, ctx ) {
        if ( buf instanceof ArrayBuffer ) buf = new Uint8Array(buf);
        if ( !ctx ) ctx = { pos: 0, end: buf.length };

        if ( ctx.end - ctx.pos < 2 || ctx.end > buf.length ) throw new RangeError("Malformed DER");

        var tag = buf[ctx.pos++],
            len = buf[ctx.pos++];

        if ( len >= 0x80 ) {
            len &= 0x7f;
            if ( ctx.end - ctx.pos < len ) throw new RangeError("Malformed DER");
            for ( var xlen = 0; len--; ) xlen <<= 8, xlen |= buf[ctx.pos++];
            len = xlen;
        }

        if ( ctx.end - ctx.pos < len ) throw new RangeError("Malformed DER");

        var rv;

        switch ( tag ) {
            case 0x02: // Universal Primitive INTEGER
                rv = buf.subarray( ctx.pos, ctx.pos += len );
                break;
            case 0x03: // Universal Primitive BIT STRING
                if ( buf[ctx.pos++] ) throw new Error( "Unsupported bit string" );
                len--;
            case 0x04: // Universal Primitive OCTET STRING
                rv = new Uint8Array( buf.subarray( ctx.pos, ctx.pos += len ) ).buffer;
                break;
            case 0x05: // Universal Primitive NULL
                rv = null;
                break;
            case 0x06: // Universal Primitive OBJECT IDENTIFIER
                var oid = btoa( b2s( buf.subarray( ctx.pos, ctx.pos += len ) ) );
                if ( !( oid in oid2str ) ) throw new Error( "Unsupported OBJECT ID " + oid );
                rv = oid2str[oid];
                break;
            case 0x30: // Universal Constructed SEQUENCE
                rv = [];
                for ( var end = ctx.pos + len; ctx.pos < end; ) rv.push( b2der( buf, ctx ) );
                break;
            default:
                throw new Error( "Unsupported DER tag 0x" + tag.toString(16) );
        }

        return rv;
    }

    function der2b ( val, buf ) {
        if ( !buf ) buf = [];

        var tag = 0, len = 0,
            pos = buf.length + 2;

        buf.push( 0, 0 ); // placeholder

        if ( val instanceof Uint8Array ) {  // Universal Primitive INTEGER
            tag = 0x02, len = val.length;
            for ( var i = 0; i < len; i++ ) buf.push( val[i] );
        }
        else if ( val instanceof ArrayBuffer ) { // Universal Primitive OCTET STRING
            tag = 0x04, len = val.byteLength, val = new Uint8Array(val);
            for ( var i = 0; i < len; i++ ) buf.push( val[i] );
        }
        else if ( val === null ) { // Universal Primitive NULL
            tag = 0x05, len = 0;
        }
        else if ( typeof val === 'string' && val in str2oid ) { // Universal Primitive OBJECT IDENTIFIER
            var oid = s2b( atob( str2oid[val] ) );
            tag = 0x06, len = oid.length;
            for ( var i = 0; i < len; i++ ) buf.push( oid[i] );
        }
        else if ( val instanceof Array ) { // Universal Constructed SEQUENCE
            for ( var i = 0; i < val.length; i++ ) der2b( val[i], buf );
            tag = 0x30, len = buf.length - pos;
        }
        else if ( typeof val === 'object' && val.tag === 0x03 && val.value instanceof ArrayBuffer ) { // Tag hint
            val = new Uint8Array(val.value), tag = 0x03, len = val.byteLength;
            buf.push(0); for ( var i = 0; i < len; i++ ) buf.push( val[i] );
            len++;
        }
        else {
            throw new Error( "Unsupported DER value " + val );
        }

        if ( len >= 0x80 ) {
            var xlen = len, len = 4;
            buf.splice( pos, 0, (xlen >> 24) & 0xff, (xlen >> 16) & 0xff, (xlen >> 8) & 0xff, xlen & 0xff );
            while ( len > 1 && !(xlen >> 24) ) xlen <<= 8, len--;
            if ( len < 4 ) buf.splice( pos, 4 - len );
            len |= 0x80;
        }

        buf.splice( pos - 2, 2, tag, len );

        return buf;
    }

    function CryptoKey ( key, alg, ext, use ) {
        Object.defineProperties( this, {
            _key: {
                value: key
            },
            type: {
                value: key.type,
                enumerable: true,
            },
            extractable: {
                value: (ext === undefined) ? key.extractable : ext,
                enumerable: true,
            },
            algorithm: {
                value: (alg === undefined) ? key.algorithm : alg,
                enumerable: true,
            },
            usages: {
                value: (use === undefined) ? key.usages : use,
                enumerable: true,
            },
        });
    }

    function isPubKeyUse ( u ) {
        return u === 'verify' || u === 'encrypt' || u === 'wrapKey';
    }

    function isPrvKeyUse ( u ) {
        return u === 'sign' || u === 'decrypt' || u === 'unwrapKey';
    }

    [ 'generateKey', 'importKey', 'unwrapKey' ]
        .forEach( function ( m ) {
            var _fn = _subtle[m];

            _subtle[m] = function ( a, b, c ) {
                var args = [].slice.call(arguments),
                    ka, kx, ku;

                switch ( m ) {
                    case 'generateKey':
                        ka = alg(a), kx = b, ku = c;
                        break;
                    case 'importKey':
                        ka = alg(c), kx = args[3], ku = args[4];
                        if ( a === 'jwk' ) {
                            b = b2jwk(b);
                            if ( !b.alg ) b.alg = jwkAlg(ka);
                            if ( !b.key_ops ) b.key_ops = ( b.kty !== 'oct' ) ? ( 'd' in b ) ? ku.filter(isPrvKeyUse) : ku.filter(isPubKeyUse) : ku.slice();
                            args[1] = jwk2b(b);
                        }
                        break;
                    case 'unwrapKey':
                        ka = args[4], kx = args[5], ku = args[6];
                        args[2] = c._key;
                        break;
                }

                if ( m === 'generateKey' && ka.name === 'HMAC' && ka.hash ) {
                    ka.length = ka.length || { 'SHA-1': 512, 'SHA-256': 512, 'SHA-384': 1024, 'SHA-512': 1024 }[ka.hash.name];
                    return _subtle.importKey( 'raw', _crypto.getRandomValues( new Uint8Array( (ka.length+7)>>3 ) ), ka, kx, ku );
                }

                if ( isWebkit && m === 'generateKey' && ka.name === 'RSASSA-PKCS1-v1_5' && ( !ka.modulusLength || ka.modulusLength >= 2048 ) ) {
                    a = alg(a), a.name = 'RSAES-PKCS1-v1_5', delete a.hash;
                    return _subtle.generateKey( a, true, [ 'encrypt', 'decrypt' ] )
                        .then( function ( k ) {
                            return Promise.all([
                                _subtle.exportKey( 'jwk', k.publicKey ),
                                _subtle.exportKey( 'jwk', k.privateKey ),
                            ]);
                        })
                        .then( function ( keys ) {
                            keys[0].alg = keys[1].alg = jwkAlg(ka);
                            keys[0].key_ops = ku.filter(isPubKeyUse), keys[1].key_ops = ku.filter(isPrvKeyUse);
                            return Promise.all([
                                _subtle.importKey( 'jwk', keys[0], ka, true, keys[0].key_ops ),
                                _subtle.importKey( 'jwk', keys[1], ka, kx, keys[1].key_ops ),
                            ]);
                        })
                        .then( function ( keys ) {
                            return {
                                publicKey: keys[0],
                                privateKey: keys[1],
                            };
                        });
                }

                if ( ( isWebkit || ( isIE && ( ka.hash || {} ).name === 'SHA-1' ) )
                        && m === 'importKey' && a === 'jwk' && ka.name === 'HMAC' && b.kty === 'oct' ) {
                    return _subtle.importKey( 'raw', s2b( a2s(b.k) ), c, args[3], args[4] );
                }

                if ( isWebkit && m === 'importKey' && ( a === 'spki' || a === 'pkcs8' ) ) {
                    return _subtle.importKey( 'jwk', pkcs2jwk(b), c, args[3], args[4] );
                }

                if ( isIE && m === 'unwrapKey' ) {
                    return _subtle.decrypt( args[3], c, b )
                        .then( function ( k ) {
                            return _subtle.importKey( a, k, args[4], args[5], args[6] );
                        });
                }

                var op;
                try {
                    op = _fn.apply( _subtle, args );
                }
                catch ( e ) {
                    return Promise.reject(e);
                }

                if ( isIE ) {
                    op = new Promise( function ( res, rej ) {
                        op.onabort =
                        op.onerror =    function ( e ) { rej(e)               };
                        op.oncomplete = function ( r ) { res(r.target.result) };
                    });
                }

                op = op.then( function ( k ) {
                    if ( ka.name === 'HMAC' ) {
                        if ( !ka.length ) ka.length = 8 * k.algorithm.length;
                    }
                    if ( ka.name.search('RSA') == 0 ) {
                        if ( !ka.modulusLength ) ka.modulusLength = (k.publicKey || k).algorithm.modulusLength;
                        if ( !ka.publicExponent ) ka.publicExponent = (k.publicKey || k).algorithm.publicExponent;
                    }
                    if ( k.publicKey && k.privateKey ) {
                        k = {
                            publicKey: new CryptoKey( k.publicKey, ka, kx, ku.filter(isPubKeyUse) ),
                            privateKey: new CryptoKey( k.privateKey, ka, kx, ku.filter(isPrvKeyUse) ),
                        };
                    }
                    else {
                        k = new CryptoKey( k, ka, kx, ku );
                    }
                    return k;
                });

                return op;
            }
        });

    [ 'exportKey', 'wrapKey' ]
        .forEach( function ( m ) {
            var _fn = _subtle[m];

            _subtle[m] = function ( a, b, c ) {
                var args = [].slice.call(arguments);

                switch ( m ) {
                    case 'exportKey':
                        args[1] = b._key;
                        break;
                    case 'wrapKey':
                        args[1] = b._key, args[2] = c._key;
                        break;
                }

                if ( ( isWebkit || ( isIE && ( b.algorithm.hash || {} ).name === 'SHA-1' ) )
                        && m === 'exportKey' && a === 'jwk' && b.algorithm.name === 'HMAC' ) {
                    args[0] = 'raw';
                }

                if ( isWebkit && m === 'exportKey' && ( a === 'spki' || a === 'pkcs8' ) ) {
                    args[0] = 'jwk';
                }

                if ( isIE && m === 'wrapKey' ) {
                    return _subtle.exportKey( a, b )
                        .then( function ( k ) {
                            if ( a === 'jwk' ) k = s2b( unescape( encodeURIComponent( JSON.stringify( b2jwk(k) ) ) ) );
                            return  _subtle.encrypt( args[3], c, k );
                        });
                }

                var op;
                try {
                    op = _fn.apply( _subtle, args );
                }
                catch ( e ) {
                    return Promise.reject(e);
                }

                if ( isIE ) {
                    op = new Promise( function ( res, rej ) {
                        op.onabort =
                        op.onerror =    function ( e ) { rej(e)               };
                        op.oncomplete = function ( r ) { res(r.target.result) };
                    });
                }

                if ( m === 'exportKey' && a === 'jwk' ) {
                    op = op.then( function ( k ) {
                        if ( ( isWebkit || ( isIE && ( b.algorithm.hash || {} ).name === 'SHA-1' ) )
                                && b.algorithm.name === 'HMAC') {
                            return { 'kty': 'oct', 'alg': jwkAlg(b.algorithm), 'key_ops': b.usages.slice(), 'ext': true, 'k': s2a( b2s(k) ) };
                        }
                        k = b2jwk(k);
                        if ( !k.alg ) k['alg'] = jwkAlg(b.algorithm);
                        if ( !k.key_ops ) k['key_ops'] = ( b.type === 'public' ) ? b.usages.filter(isPubKeyUse) : ( b.type === 'private' ) ? b.usages.filter(isPrvKeyUse) : b.usages.slice();
                        return k;
                    });
                }

                if ( isWebkit && m === 'exportKey' && ( a === 'spki' || a === 'pkcs8' ) ) {
                    op = op.then( function ( k ) {
                        k = jwk2pkcs( b2jwk(k) );
                        return k;
                    });
                }

                return op;
            }
        });

    [ 'encrypt', 'decrypt', 'sign', 'verify' ]
        .forEach( function ( m ) {
            var _fn = _subtle[m];

            _subtle[m] = function ( a, b, c, d ) {
                if ( isIE && ( !c.byteLength || ( d && !d.byteLength ) ) )
                    throw new Error("Empy input is not allowed");

                var args = [].slice.call(arguments),
                    ka = alg(a);

                if ( isIE && m === 'decrypt' && ka.name === 'AES-GCM' ) {
                    var tl = a.tagLength >> 3;
                    args[2] = (c.buffer || c).slice( 0, c.byteLength - tl ),
                    a.tag = (c.buffer || c).slice( c.byteLength - tl );
                }

                args[1] = b._key;

                var op;
                try {
                    op = _fn.apply( _subtle, args );
                }
                catch ( e ) {
                    return Promise.reject(e);
                }

                if ( isIE ) {
                    op = new Promise( function ( res, rej ) {
                        op.onabort =
                        op.onerror = function ( e ) {
                            rej(e);
                        };

                        op.oncomplete = function ( r ) {
                            var r = r.target.result;

                            if ( m === 'encrypt' && r instanceof AesGcmEncryptResult ) {
                                var c = r.ciphertext, t = r.tag;
                                r = new Uint8Array( c.byteLength + t.byteLength );
                                r.set( new Uint8Array(c), 0 );
                                r.set( new Uint8Array(t), c.byteLength );
                                r = r.buffer;
                            }

                            res(r);
                        };
                    });
                }

                return op;
            }
        });

    if ( isIE ) {
        var _digest = _subtle.digest;

        _subtle['digest'] = function ( a, b ) {
            if ( !b.byteLength )
                throw new Error("Empy input is not allowed");

            var op;
            try {
                op = _digest.call( _subtle, a, b );
            }
            catch ( e ) {
                return Promise.reject(e);
            }

            op = new Promise( function ( res, rej ) {
                op.onabort =
                op.onerror =    function ( e ) { rej(e)               };
                op.oncomplete = function ( r ) { res(r.target.result) };
            });

            return op;
        };

        global.crypto = Object.create( _crypto, {
            getRandomValues: { value: function ( a ) { return _crypto.getRandomValues(a) } },
            subtle:          { value: _subtle },
        });

        global.CryptoKey = CryptoKey;
    }

    if ( isWebkit ) {
        _crypto.subtle = _subtle;

        global.Crypto = _Crypto;
        global.SubtleCrypto = _SubtleCrypto;
        global.CryptoKey = CryptoKey;
    }
}));

// TODO 1 : collapse card

import moment from 'moment/src/moment';
import _ from 'lodash/lodash';
import * as models from 'icc-api/icc-api/model/models';

class HtMain extends Polymer.TkLocalizerMixin(Polymer.Element) {
  static get template() {
    return Polymer.html`
		<style include="iron-flex iron-flex-alignment"></style>
		<style include="shared-styles">
			:host {
				display: block;
				padding: 24px;
				height: calc(100vh - 64px);
				box-sizing: border-box;
			}

			.grid-container {
				display:grid;
				width:100%;
				height:100%;
				grid-template-columns: 1fr 1fr;
				grid-template-rows: 1fr 1fr 1fr;
				grid-column-gap: 24px;
  				grid-row-gap: 24px;
			}

			.row {
				cursor: pointer;
			}

			.card {
				color: var(--app-text-color);
				background: var(--app-background-color);
				border-radius: 2px;
				@apply --shadow-elevation-2dp;
				overflow:auto;
			}

			.card-title-container {
				background: #ffffff;
				padding: 16px;
			}

			.card-title {
				padding: 0;
				margin: 0;
				font-size: 16px;
				font-weight: bold;
				text-align: center;
				color: var(--app-text-color);
			}

			.card-body {
				display: block;
				padding: 16px;
				height: calc(100% - 96px);
				overflow: auto;
			}

			.consultations-list {
				margin: 8px;
				width: calc(100% - 16px);
			}

			.row {
				width: 100%;
				display: flex;
				flex-direction: row;
				justify-content: space-between;
				align-items: center;
				flex-wrap: wrap;
				margin-bottom: 24px;
			}

			.row:last-child {
				margin-bottom: 0;
			}

			.consultations-patient-photo {
				background: rgba(0, 0, 0, 0.1);
				height: 26px;
				width: 26px;
				border-radius: 50%;
				margin-right: 8px;
				overflow: hidden;
			}

			.consultations-patient-photo img {
				width: 100%;
				margin: 50%;
				transform: translate(-50%, -50%);
			}

			.consultations-patient-name {
				font-size: 14px;
				font-weight: bold;
				margin-right: 16px;
				text-transform: capitalize;
				/*min-width: 30%;*/
			}

			.consultations-message-texte {
				font-size: 14px;
				margin-right: 16px;
				/*min-width: 30%;*/
			}

			.consultations-patient-type {
				color: var(--app-secondary-color);
				font-size: 14px;
				margin-right: 16px;
			}

			.divider {
				border-bottom: 1px solid lightgrey;
				flex-grow: 7;
			}

			.consultations-time {
				font-size: 14px;
				text-align: right;
				margin-left: 16px;
			}

			.card-table-header {
				font-size: 10px;
				text-transform: uppercase;
				text-align: left;
			}

			.card-table-header.row {
				margin-bottom:12px;
				position:sticky;
				top:-16px;
				background: var(--app-background-color);
				z-index:900;
				border-bottom: 1px dotted var(--app-background-color-dark);
				padding:4px 0;
			}

			.card-table-header div, .card-table-cell {
				width: 40%;
			}

			.card-header-cell--date, .card-table-cell--date {
				width: 20%!important ;
			}


			.card-table-row {
				width: 100%;
			}

			.todo-card paper-listbox paper-item {
				padding: 0;
			}

			.card-table-cell:first-child {
				text-align: center;
				display: flex;
				flex-direction: row;
				justify-content: center;
				flex-wrap: nowrap;
				align-items: center;
				font-size: 14px;
				font-weight:600;
			}

			.card-table-cell {
				font-size: 14px;
				white-space: nowrap;
				text-overflow: ellipsis;
				overflow: hidden;
				text-align: left;
				padding-right: 8px;
			}

			.card-table-cell:first-child {
				justify-content: flex-start;
			}

			.card-table-row {
				justify-content: space-around;
				flex-wrap: nowrap;
				height:48px;
				margin:0;
			}

			.latest-patients-card {
				grid-area: 1 / 1 / span 3 / span 1;
			}

			.latest-patients-row {
				justify-content: flex-start;
			}

			.latest-patients-row:hover .consultations-patient-name,
			.latest-patients-row:hover .consultations-time,
			.latest-consultations-row:hover .consultations-patient-name,
			.latest-consultations-row:hover .consultations-time {
				text-shadow: 0 0 1px rgba(0,0,0,0.3);
			}

			.latest-consultations-row {
				justify-content: flex-start;
			}

			.patient-dateofbirth {
				color:var(--app-text-color-disabled);
				font-weight:400;
				font-size:14px;
				font-style:italic;
			}

			.todays-consultations {
				grid-area: 1 / 2 / span 1 / span 1;

			}

			.latest-lab-result {
				grid-area: 2 / 2 / span 1 / span 1;
			}

			 .todo-card {
				grid-area: 3 / 2 / span 1 / span 1;
			}

			a {
				text-decoration: none;
				color: var(--app-text-color);
			}

			paper-checkbox {
				--paper-checkbox-unchecked-color: var(--app-text-color);
				--paper-checkbox-unchecked-ink-color: var(--app-secondary-color);
				--paper-checkbox-checkmark-color: var(--app-secondary-color);
				--paper-checkbox-checked-color: var(--app-primary-color);
			}

			paper-listbox {
				--paper-listbox-background-color: red;
				background: transparent;
				padding: 0;
			}

			paper-item {
				outline: 0;
				background: var(--app-background-color);
			}

			.new-notif::after {
				content: '';
				display: inline-block;
				width: 7px;
				height: 7px;
				margin-left: 6px;
				margin-bottom: 8px;
				border-radius: 3.5px;
				background-color: var(--app-secondary-color);
			}


			@media (max-width: 1024px) {
				.grid-container {
					display:grid;
					width:100%;
					height:100%;
					grid-template-columns: 1fr 1fr 1fr 1fr;
					grid-template-rows: 1fr 1fr;
					grid-column-gap: 24px;
					grid-row-gap: 24px;
				}
				.card {
					overflow:hidden;
				}
				.latest-patients-card {
					grid-area: 1 / 1 / span 1 / span 3;
				}
				.todays-consultations {
					grid-area: 1 / 4 / span 1 / span 1;
				}
				.latest-lab-result {
					grid-area: 2 / 1 / span 1 / span 2;
				}
				
				.todo-card {
					grid-area: 2 / 3 / span 1 / span 2;
				}
				.card-body {
					overflow-x: hidden;
					overflow-y: scroll;
				}
				.card-table-cell .consultations-patient-photo, .todays-consultations .consultations-patient-photo{
					display:none;
				}

				.card-table-cell:first-child {
					text-align: left;
					display: block;
					font-weight:600;
				}

				.consultations-list {
					width:100%;
					margin:0;
				}

				.card-table-header.row{
					top: 0;
				}

			}

			@media (max-width: 768px) {
				.grid-container {
					grid-template-columns: 1fr 1fr 1fr 1fr;
					grid-template-rows: 1fr 1fr 1fr;
					grid-column-gap: 24px;
					grid-row-gap: 24px;
				}
				.latest-patients-card {
					grid-area: 1 / 1 / span 1 / span 4;	
				}
				.todays-consultations {
					grid-area: 2 / 1 / span 1 / span 2;	
				}
				.latest-lab-result {
					grid-area: 2 / 3 / span 1 / span 2 ;	
				}
				
				.todo-card {
					grid-area: 3  / 1 / span 1 / span 4;	
				}
			}

			@media (max-width: 420px) {
				.grid-container {
					grid-template-columns: 1fr;
					grid-template-rows: 1fr 1fr 1fr 1fr;
					grid-column-gap: 24px;
					grid-row-gap: 24px;
				}
				.latest-patients-card {
					grid-column-start:1;
					grid-column-end: span 1;
					grid-row-start: 1;
					grid-row-end: span 1;
				}
				.todays-consultations {
					grid-column-start:1;
					grid-column-end: span 1;
					grid-row-start: 2;
					grid-row-end: span 1;
				}
				.latest-lab-result {
					grid-column-start:1;
					grid-column-end: span 1;
					grid-row-start: 3;
					grid-row-end: span 1;
				}
				
				.todo-card {
					grid-column-start:1;
					grid-column-end: span 1;
					grid-row-start: 4;
					grid-row-end: span 1;
				}
			}

		</style>

		<div class="grid-container">
			<div class="card latest-patients-card">
				<div class="card-title-container">
					<h1 class="card-title">[[localize('lat_pat','Latest Patients',language)]]</h1>
				</div>
				<div class="card-body">
					<template is="dom-repeat" items="[[accessLogs]]" as="access">
						<div class="row latest-patients-row" data-id\$="[[access.patient.id]]" on-tap="openPatient">
							<div class="consultations-patient-photo"><img src\$="[[picture(access.patient)]]"></div>
							<div class="consultations-patient-name">[[access.patient.firstName]] [[access.patient.lastName]] <span class="patient-dateofbirth">°[[_timeFormat(access.patient.dateOfBirth)]]</span></div>
							<div class="divider"></div>
							<div class="consultations-time">[[_timeFormat(access.access.date)]]</div>
						</div>
					</template>
				</div>
			</div>
			<div class="card todays-consultations">
				<div class="card-title-container">
					<h1 class="card-title">[[localize('tod_app','Today‘s Appointment',language)]]</h1>
				</div>
				<div class="card-body">
					<div class="consultations-list">
						<template is="dom-repeat" items="[[appointments]]" as="appointment">
							<div class="row latest-consultations-row" data-id\$="[[appointment.patient.id]]" on-tap="openPatient">
								<div class="consultations-patient-photo"><img src\$="[[picture(appointment.patient)]]"></div>
								<div class="consultations-patient-name">[[appointment.patient.firstName]] [[appointment.patient.lastName]]</div>
								<div class="consultations-patient-type">#[[appointment.type]]</div>
								<div class="divider"></div>
								<div class="consultations-time">[[_shortTimeFormat(appointment.startTime)]]</div>
							</div>
						</template>
					</div>
				</div>
			</div>
			<div class="card latest-lab-result">
				<div class="card-title-container">
					<h1 class="card-title new-notif">[[localize('lat_lab_res','Latest Lab Results',language)]]</h1>
				</div>
				<div class="card-body">
					<div class="card-table-header row">
						<div class="card-header-cell">[[localize('sen','Sender',language)]]</div>
						<div class="card-header-cell card-header-cell--date">[[localize('dat','Date',language)]]</div>
						<div class="card-header-cell">[[localize('sub','Subject',language)]]</div>
					</div>
					<template is="dom-repeat" items="[[messages]]" as="msg">
						<div class="row card-table-row" data-id\$="[[msg.formId]]" on-tap="openMessage">
							<div class="card-table-cell">[[msg.fromAddress]]</div>
							<div class="card-table-cell card-table-cell--date">[[_timeFormat(msg.received)]]</div>
							<div class="card-table-cell">[[msg.subject]] </div>
						</div>
					</template>
				</div>
			</div>
			<div class="card todo-card">
				<div class="card-title-container">
					<h1 class="card-title">[[localize('pla','Planning',language)]]</h1>
				</div>
				<div class="card-body">
					<div class="card-table-header row">
						<div class="card-header-cell">[[localize('pati','Patient',language)]]</div>
						<div class="card-header-cell card-header-cell--date">[[localize('due_dat','Due date',language)]]</div>
						<div class="card-header-cell">[[localize('des','Description',language)]]</div>
					</div>
					<template is="dom-repeat" items="[[services]]" as="service">
						<paper-listbox>
							<paper-item>
								<div class="row card-table-row" data-id\$="[[service.service.id]]">
									<div class="card-table-cell">
										<div class="consultations-patient-photo"><img src\$="[[picture(service.patient)]]"></div>
										<div class="consultations-patient-name">[[service.patient.firstName]] [[service.patient.lastName]]</div>
									</div>
									<div class="card-table-cell card-table-cell--date">[[_timeFormat(service.service.valueDate)]]</div>
									<div class="card-table-cell">[[service.service.content.language.stringValue]]</div>
								</div>
							</paper-item>
						</paper-listbox>
					</template>
				</div>
			</div>
		</div>
`;
  }

  static get is() {
      return 'ht-main';
	}

  static get properties() {
      return {
          api: {
              type: Object
          },
          user: {
              type: Object
          },
          accessLogs: {
              type: Array,
              value: function () {
                  return [];
              }
          },
          appointments: {
              type: Array,
              value: function () {
                  return [];
              }
          },
          messages: {
              type: Array,
              value: function () {
                  return [];
              }
          },
          labres: {
              type: Array,
              value: function () {
                  return [];
              }
          },
          services: {
              type: Array,
              value: function () {
                  return [];
              }
          }
      };
	}

  constructor() {
      super();
	}

  ready() {
      super.ready();

      this.api.hcparty().getCurrentHealthcareParty().then(hcp => {
          const language = (hcp.languages || ['fr']).find(lng => lng && lng.length === 2);
          language && this.set('language', language);
          return hcp;
      }).then(hcp => {
          this.api.accesslog().findByUserAfterDate(this.user.id, 'USER_ACCESS', +new Date() - 1000 * 3600 * 24 * 365, null, null, 1000, true).then(accessLogs => {
              const accesses = accessLogs.rows.reduce((acc, access) => {
                  const latestAccessForPatId = acc[access.patientId] || (acc[access.patientId] = { access: access });
                  if (latestAccessForPatId.access.date < access.date) {
                      latestAccessForPatId.access = access;
                  }
                  return acc;
              }, {});
              return this.api.patient().getPatients({ ids: Object.keys(accesses) }).then(patients => {
                  patients.forEach(p => accesses[p.id] && (accesses[p.id].patient = p));
                  return accesses;
              });
          }).then(accesses => this.set('accessLogs', _.sortBy(Object.values(accesses), a => -a.access.date)));

          this.api.message().findMessagesByToAddress('INBOX', null, null, 1000).then(messages => {
              messages.rows.forEach(function (m, index, messages) {
                  if (m.status && 1 << 1 == 0) {
                      messages.splice(index, 1);
                  }
              }); //todo refaire reduce
              messages.rows.sort(function (a, b) {
                  return a.status && (1 << 0) - b.status && 1 << 0;
              });
              return messages.rows;
          }).then(messages => this.set('messages', messages || []));

          this.api.bemikrono().appointments(parseInt(moment().format('YYYYMMDD'))).then(appointments => {
              return appointments && this.api.patient().getPatients(new models.ListOfIdsDto({ ids: appointments.map(a => a.patientId) })).then(patients => {
                  //todo wtf JSON not valid
                  patients.forEach((p, idx) => appointments[idx].patient = p);
                  return appointments;
              });
          }).then(appointments => this.set('appointments', appointments || []));

          const start = parseInt(moment().subtract(1, 'month').format('YYYYMMDD'));
          const end = parseInt(moment().add(1, 'month').format('YYYYMMDD'));
          const maxplanningsize = 10;
          const sort = 'valueDate';
          const desc = 'desc';

          const planningFilter = { '$type': 'UnionFilter', 'filters': [{ '$type': 'ServiceByHcPartyTagCodeDateFilter', healthcarePartyId: hcp.id, tagCode: 'planned', tagType: 'CD-LIFECYCLE', startValueDate: start, endValueDate: end }, { '$type': 'ServiceByHcPartyTagCodeDateFilter', healthcarePartyId: hcp.id, tagCode: 'planned', tagType: 'CD-LIFECYCLE', startValueDate: start * 1000000, endValueDate: end * 1000000 }] };
          this.api.contact().filterServicesBy(null, null, 1000, new models.FilterChain({ filter: planningFilter })) //todo wtf JSON not valid
          .then(planningList => {
              const svcDict = planningList.rows.reduce((acc, s) => {
                  const cs = acc[s.id];
                  if (!cs || !cs.modified || s.modified && this.api.after(s.modified, cs.modified)) {
                      acc[s.id] = s;
                  }
                  return acc;
              }, {});
              const services = _.sortBy(Object.values(svcDict).filter(s => !s.endOfLife), [s => +this.api.moment( /*s.modified||s.created||*/s.valueDate)]);
              const hcpId = this.user.healthcarePartyId;

              const ownersOfDelegations = services.reduce((acc, s) => {
                  s.cryptedForeignKeys[hcpId].forEach(d => acc[d.owner] = 1);return acc;
              }, {});
              const importedAESHcPartyKeys = {};

              return this.api.hcparty().getHcPartyKeysForDelegate(hcpId).then(keys => Promise.all(Object.keys(ownersOfDelegations).map(ownerId => this.api.crypto().decryptHcPartyKey(ownerId, hcpId, keys[ownerId]).then(importedAESHcPartyKey => importedAESHcPartyKeys[ownerId] = importedAESHcPartyKey)))).then(() => Promise.all(services.map(s => Promise.all(s.cryptedForeignKeys[hcpId].map(k => this.api.crypto().AES.decrypt(importedAESHcPartyKeys[k.owner].key, this.api.crypto().utils.hex2ua(k.key)))).then(patIds => {
                  const decodedPatIds = patIds.map(ua => this.api.crypto().utils.ua2text(ua).split(':')[1]);
                  s.patId = decodedPatIds.find(id => id != null);return decodedPatIds;
              })))).then(arraysOfArraysOfPatIdsAsUa => {
                  return this.api.patient().filterBy(null, null, maxplanningsize, /*index*/null, sort, desc, {
                      filter: {
                          '$type': 'PatientByIdsFilter',
                          'ids': _.uniqBy(_.compact(_.flatMap(arraysOfArraysOfPatIdsAsUa)))
                      }
                  });
              }).then(patients => services.map(s => ({ service: s, patient: patients.rows.find(p => p.id === s.patId) })));
          }).then(services => this.set('services', (services || []).filter(it => it.patient)));
      });
	}

  _timeFormat(date) {
      return date && this.api.moment(date).format(date > 99991231 ? 'DD/MM/YYYY HH:mm' : 'DD/MM/YYYY') || '';
	}

  _shortTimeFormat(date) {
      return date && this.api.moment(date).format(date > 99991231 ? 'HH:mm' : 'DD/MM/YYYY') || '';
	}

  picture(pat) {
      if (!pat) {
          return require('../images/Male-128.jpg');
      }
      return pat.picture ? 'data:image/jpeg;base64,' + pat.picture : pat.gender === 'female' ? require('../images/Female-128.jpg') : require('../images/Male-128.jpg');
	}

  openPatient(ev) {
      let target = ev.target;
      while (target && !target.dataset.id) {
          target = target.parentNode;
      }
      location.replace(location.href.replace(/(.+?)\/#\/main.*/, `$1/#/pat/${target.dataset.id}`));
	}

  openMessage(ev) {
      let target = ev.target;
      while (target && !target.dataset.id) {
          target = target.parentNode;
      }
      location.replace(location.href.replace(/(.+?)\/#\/main.*/, `$1/#/msg/${target.dataset.id}`));
	}
}

customElements.define(HtMain.is, HtMain);
