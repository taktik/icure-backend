import '../fhc-api/fhc-api.js';
import * as api from 'icc-api/dist/icc-api/iccApi'
import 'moment'

import {fhcAddressbookcontrollerApi} from 'api/fhcAddressbookcontrollerApi'
import {FhcBekmehrXApi} from 'icc-api/dist/icc-x-api/icc-bekmehr-x-api'
import {FhcCodeXApi} from 'icc-api/dist/icc-x-api/icc-code-x-api'
import {FhcContactXApi} from 'icc-api/dist/icc-x-api/icc-contact-x-api'
import {FhcCryptoXApi} from 'icc-api/dist/icc-x-api/icc-crypto-x-api'
import {FhcDocumentXApi} from 'icc-api/dist/icc-x-api/icc-document-x-api'
import {FhcFormXApi} from 'icc-api/dist/icc-x-api/icc-form-x-api'
import {FhcHcpartyXApi} from 'icc-api/dist/icc-x-api/icc-hcparty-x-api'
import {FhcHelementXApi} from 'icc-api/dist/icc-x-api/icc-helement-x-api'
import {FhcPatientXApi} from 'icc-api/dist/icc-x-api/icc-patient-x-api'

import {PolymerElement, html} from '@polymer/polymer';
class FhcApi extends PolymerElement {
    static get template() {
        return html`
        <fhc-api id="fhc-api" host="[[fhcHost]]"></fhc-api>
`;
    }

    static get is() {
        return 'icc-api'
    }

    static get properties() {
        return {
            headers: {
                type: Object,
                value: {"Content-Type": "application/json"},
                notify: true
            },
            host: {
                type: String
            },
            fhcHost: {
                type: String
            },
            baseApi: {
                type: Object,
                notify: true
            },
            hcpartyApi: {
                type: Object
            },
            hcParties: {
                type: Object,
                value: function () {
                    return {}
                }
            },
            users: {
                type: Object,
                value: function () {
                    return {}
                }
            },
            registry: {
                type: Object,
                value: function () {
                    return {}
                }
            }
        }
    }

    static get observers() {
        return ["refresh(headers, headers.*, host)"]
    }

    constructor() {
        super()
    }

    ready() {
        super.ready()
        if (this.host != null && this.headers != null) this.refresh()
    }

    refresh() {
        this.accesslogicc = new api.iccAccesslogApi(this.host, this.headers)
        this.authicc = new api.iccAuthApi(this.host, this.headers)
        this.beabicc = new api.iccBeabApi(this.host, this.headers)
        this.bechaptericc = new api.iccBechapterApi(this.host, this.headers)
        this.bedmgicc = new api.iccBedmgApi(this.host, this.headers)
        this.beefacticc = new api.iccBeefactApi(this.host, this.headers)
        this.beehboxicc = new api.iccBeehboxApi(this.host, this.headers)
        this.beeidicc = new api.iccBeeidApi(this.host, this.headers)
        this.beetarificc = new api.iccBeetarifApi(this.host, this.headers)
        this.begeninsicc = new api.iccBegeninsApi(this.host, this.headers)
        this.behubsicc = new api.iccBehubsApi(this.host, this.headers)
        this.bemikronoicc = new api.iccBemikronoApi(this.host, this.headers)
        this.beprimotoicc = new api.iccBeprimotoApi(this.host, this.headers)
        this.berecipeicc = new api.iccBerecipeApi(this.host, this.headers)
        this.beresultexporticc = new api.iccBeresultexportApi(this.host, this.headers)
        this.beresultimporticc = new api.iccBeresultimportApi(this.host, this.headers)
        this.bestsicc = new api.iccBestsApi(this.host, this.headers)
        this.betherlinkicc = new api.iccBetherlinkApi(this.host, this.headers)
        this.bevitalinkicc = new api.iccBevitalinkApi(this.host, this.headers)
        this.doctemplateicc = new api.iccDoctemplateApi(this.host, this.headers)
        this.entitytemplateicc = new api.iccEntitytemplateApi(this.host, this.headers)
        this.genericicc = new api.iccGenericApi(this.host, this.headers)
        this.icureicc = new api.iccIcureApi(this.host, this.headers)
        this.insuranceicc = new api.iccInsuranceApi(this.host, this.headers)
        this.invoiceicc = new api.iccInvoiceApi(this.host, this.headers)
        this.messageicc = new api.iccMessageApi(this.host, this.headers)
        this.replicationicc = new api.iccReplicationApi(this.host, this.headers)
        this.tarificationicc = new api.iccTarificationApi(this.host, this.headers)
        this.usericc = new api.iccUserApi(this.host, this.headers)

        this.bedrugsicc = new FhcBedrugsXApi(this.host, this.headers)
        this.codeicc = new FhcCodeXApi(this.host, this.headers)
        this.hcpartyicc = new FhcHcpartyXApi(this.host, this.headers)

        this.cryptoicc = new FhcCryptoXApi(this.host, this.headers, this.hcpartyicc)

        this.contacticc = new FhcContactXApi(this.host, this.headers, this.cryptoicc)
        this.documenticc = new FhcDocumentXApi(this.host, this.headers, this.cryptoicc)
        this.formicc = new FhcFormXApi(this.host, this.headers, this.cryptoicc)
        this.helementicc = new FhcHelementXApi(this.host, this.headers, this.cryptoicc)
        this.patienticc = new FhcPatientXApi(this.host, this.headers, this.cryptoicc, this.contacticc, this.helementicc, this.invoiceicc, this.documenticc, this.hcpartyicc)

        this.bekmehricc = new FhcBekmehrXApi(this.host, this.headers, this.contacticc, this.helementicc)

        this.dispatchEvent(new CustomEvent('refresh', {detail: {}}))
    }

    accesslog() {
        return this.accesslogicc
    }

    auth() {
        return this.authicc
    }

    beab() {
        return this.beabicc
    }

    bechapter() {
        return this.bechaptericc
    }

    bedmg() {
        return this.bedmgicc
    }

    bedrugs() {
        return this.bedrugsicc
    }

    beefact() {
        return this.beefacticc
    }

    beehbox() {
        return this.beehboxicc
    }

    beeid() {
        return this.beeidicc
    }

    beetarif() {
        return this.beetarificc
    }

    begenins() {
        return this.begeninsicc
    }

    behubs() {
        return this.behubsicc
    }

    bekmehr() {
        return this.bekmehricc
    }

    bemikrono() {
        return this.bemikronoicc
    }

    beprimoto() {
        return this.beprimotoicc
    }

    berecipe() {
        return this.berecipeicc
    }

    beresultexport() {
        return this.beresultexporticc
    }

    beresultimport() {
        return this.beresultimporticc
    }

    bests() {
        return this.bestsicc
    }

    betherlink() {
        return this.betherlinkicc
    }

    bevitalink() {
        return this.bevitalinkicc
    }

    code() {
        return this.codeicc
    }

    contact() {
        return this.contacticc
    }

    doctemplate() {
        return this.doctemplateicc
    }

    document() {
        return this.documenticc
    }

    entitytemplate() {
        return this.entitytemplateicc
    }

    form() {
        return this.formicc
    }

    generic() {
        return this.genericicc
    }

    hcparty() {
        return this.hcpartyicc
    }

    helement() {
        return this.helementicc
    }

    icure() {
        return this.icureicc
    }

    insurance() {
        return this.insuranceicc
    }

    invoice() {
        return this.invoiceicc
    }

    message() {
        return this.messageicc
    }

    patient() {
        return this.patienticc
    }

    replication() {
        return this.replicationicc
    }

    tarification() {
        return this.tarificationicc
    }

    user() {
        return this.usericc
    }

    crypto() {
        return this.cryptoicc
    }

    fhc() {
        return this.$['fhc-api'];
    }

    localize(e, lng) {
        if (!e) {
            return null;
        }
        return e[lng] || e.fr || e.en || e.nl;
    }

    //Convenience methods for dates management
    after(d1, d2) {
        return d1 === null || d2 === null || d1 === undefined || d2 === undefined || this.moment(d1).isAfter(this.moment(d2));
    }

    before(d1, d2) {
        return d1 === null || d2 === null || d1 === undefined || d2 === undefined || this.moment(d1).isBefore(this.moment(d2));
    }

    moment(epochOrLongCalendar) {
        if (!epochOrLongCalendar && epochOrLongCalendar !== 0) {
            return null;
        }
        if (epochOrLongCalendar >= 18000101 && epochOrLongCalendar < 25400000) {
            return moment('' + epochOrLongCalendar, 'YYYYMMDD');
        } else if (epochOrLongCalendar >= 18000101000000) {
            return moment('' + epochOrLongCalendar, 'YYYYMMDDhhmmss');
        } else {
            return moment(epochOrLongCalendar);
        }
    }

    template(template, args) {
        const nargs = /\{([0-9a-zA-Z_ ]+)\}/g;
        return template.replace(nargs, function replaceArg(match, i, index) {
            var result;

            if (template[index - 1] === "{" && template[index + match.length] === "}") {
                //Double {{ }} means escape
                return i;
            } else {
                result = args.hasOwnProperty(i) ? args[i] : null;
                if (result === null || result === undefined) {
                    return "";
                }

                return result;
            }
        });
    }

    getAuthor(author) {
        const usr = this.users[author];
        const hcp = usr ? this.hcParties[usr.healthcarePartyId] : null;
        return hcp && hcp.lastName + " " + (hcp.firstName && hcp.firstName.length && hcp.firstName.substr(0, 1) + ".") || usr && usr.login || "N/A";
    }

    cacheRowsUsingPagination(key, paginator) {
        const cache = this.cache || ((this.cache = {}))
        const promise = cache[key]

        if (promise) { return promise }

        const executePaginator = (latestResult, acc) =>
            paginator(latestResult.nextKey,latestResult.docId).then((newResult) => {
                acc.push(...newResult.rows)
                return newResult.done ? acc : executePaginator(newResult, acc)
            })
        return (cache[key] = executePaginator({nextKey: null, nextDocId: null}, []))
    }

    loadUsersAndHcParties() {
        return this.user().listUsers().then(users => {
            this.set('users', users.rows.reduce((acc, u) => {
                acc[u.id] = u
                return acc
            }, {}))
            return this.hcparty().getHealthcareParties(users.rows.map(u => u.healthcarePartyId).filter(id => !!id))
        }).then(hcps => this.set('hcParties', hcps.reduce((acc, hcp) => {
            acc[hcp.id] = hcp
            return acc
        }, {})))
    }

    unregisterAll(domain) {
        this.registry[domain] = {listeners:[], entities:{}}
    }

    register(entity, domain) {
        if (!entity) { return entity }

        const reg = this.registry[domain] || (this.registry[domain] = {listeners:[], entities:{}})
        let current = reg.entities[entity.id]
        if (current) {
            _.difference(_.keys(current), _.keys(entity)).forEach(k => delete current[k])
            _.assign(current, entity)

            reg.listeners.forEach(l=>{
                if (!l.pool || l.pool.some(x => x.id === entity.id)) { l.target.notifyPath(l.path) }
            })
        } else {
            current = (reg.entities[entity.id] = entity)
        }
        return current
    }
}

customElements.define(FhcApi.is, FhcApi)

    
<link rel="import" href="../../../bower_components/polymer/polymer.html">

/*<link rel="import" href="./api/fhcAddressbookcontrollerApi.ts">
<link rel="import" href="./api/fhcBasicerrorcontrollerApi.ts">
<link rel="import" href="./api/fhcConsentcontrollerApi.ts">
<link rel="import" href="./api/fhcDmgcontrollerApi.ts">
<link rel="import" href="./api/fhcEattestcontrollerApi.ts">
<link rel="import" href="./api/fhcEhboxcontrollerApi.ts">
<link rel="import" href="./api/fhcGeninscontrollerApi.ts">
<link rel="import" href="./api/fhcHubcontrollerApi.ts">
<link rel="import" href="./api/fhcRecipecontrollerApi.ts">
<link rel="import" href="./api/fhcStscontrollerApi.ts">
<link rel="import" href="./api/fhcTarificationcontrollerApi.ts">
<link rel="import" href="./api/fhcTherlinkcontrollerApi.ts">
*/

<dom-module id="fhc-api-v2">
    <template>
        <style>
        </style>
        </template>
</dom-module>

<script>
    import * as api from './fhcApi';

    import {PolymerElement, html} from '@polymer/polymer';
class FhcApiV2 extends PolymerElement {
        static get is() {
            return 'fhc-api-v2';
        }

        static get properties() {
            return {
                headers: {
                    type: Object,
                    value: { "Content-Type": "application/json" },
                    notify: true
                },
                host: {
                    type: String
                }
            };
        }static get observers() {
            return ["refresh(headers, headers.*, host)"];
        }

        constructor() {
            super();
        }

        ready() {
            super.ready();

            if (this.host != null && this.headers != null) this.refresh();
        }

        refresh() {
                this.addressbookcontrollerfhc = new api.fhcAddressbookcontrollerApi(this.host, this.headers);
                this.basicerrorcontrollerfhc = new api.fhcBasicerrorcontrollerApi(this.host, this.headers);
                this.consentcontrollerfhc = new api.fhcConsentcontrollerApi(this.host, this.headers);
                this.dmgcontrollerfhc = new api.fhcDmgcontrollerApi(this.host, this.headers);
                this.eattestcontrollerfhc = new api.fhcEattestcontrollerApi(this.host, this.headers);
                this.ehboxcontrollerfhc = new api.fhcEhboxcontrollerApi(this.host, this.headers);
                this.geninscontrollerfhc = new api.fhcGeninscontrollerApi(this.host, this.headers);
                this.hubcontrollerfhc = new api.fhcHubcontrollerApi(this.host, this.headers);
                this.recipecontrollerfhc = new api.fhcRecipecontrollerApi(this.host, this.headers);
                this.stscontrollerfhc = new api.fhcStscontrollerApi(this.host, this.headers);
                this.tarificationcontrollerfhc = new api.fhcTarificationcontrollerApi(this.host, this.headers);
                this.therlinkcontrollerfhc = new api.fhcTherlinkcontrollerApi(this.host, this.headers);

                this.dispatchEvent(new CustomEvent('refresh', {detail: {}}))
            }

            addressbookcontroller() { return this.addressbookcontrollerfhc}
            basicerrorcontroller() { return this.basicerrorcontrollerfhc}
            consentcontroller() { return this.consentcontrollerfhc}
            dmgcontroller() { return this.dmgcontrollerfhc}
            eattestcontroller() { return this.eattestcontrollerfhc}
            ehboxcontroller() { return this.ehboxcontrollerfhc}
            geninscontroller() { return this.geninscontrollerfhc}
            hubcontroller() { return this.hubcontrollerfhc}
            recipecontroller() { return this.recipecontrollerfhc}
            stscontroller() { return this.stscontrollerfhc}
            tarificationcontroller() { return this.tarificationcontrollerfhc}
            therlinkcontroller() { return this.therlinkcontrollerfhc}
            
        }
    customElements.define(FhcApiV2.is, FhcApiV2);
</script>

@end


