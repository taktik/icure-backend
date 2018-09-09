import '../../icpc-styles.js';
import './dynamic-form.js';
class DynamicallyLoadedForm extends Polymer.TkLocalizerMixin(Polymer.Element) {
    static get is() {
				return 'dynamically-loaded-form';
    }

    static get properties() {
				return {
            api: {
                type: Object,
                observer: '_apiChanged'
            },
            user: {
                type: Object
            },
            patient: {
                type: Object,
                value: null
            },
            contact: {
                type: Object,
                value: null
            },
            contacts: {
                type: Array,
                value: []
            },
            servicesMap: {
                type: Object,
                value: {}
            },
            healthElements: {
                type: Array,
                value: function () {
                    return [];
                }
            },
            highlightedHes: {
                type: Array,
                value: function () {
                    return [];
                }
            },
            currentContact: {
                type: Object,
                value: null
            },
            formId: {
                type: String,
                observer: '_formIdChanged'
            },
            form: {
                type: Object,
                value: null
            },
            dataProvider: {
                type: Object,
                value: null
            },
            dataMap: {
                type: Object,
                value: null
            },
            layoutInfoPerLabel: {
                type: Object,
                value: function () {
                    return {};
                }

            },
            plannedAction:{
                type: Object,
                value : {Status : "pending", Deadline: "", HcpId : "", ProcedureId : "", ProfessionId : "", ReasonOfRef : "", isSendMail : false, isDeadline: false, isSurgical: false, isVaccineProcedure: false, VaccineCommercialNameId : "", DoseNumber : "", BatchNumber : "", ProcedureInfo: {}, Description: "", ProfessionInfo : {}, VaccineInfo: {}}
            },
            comboStatus: {
                type: Array,
                value:[
                    {
                        "id"       : "aborted",
                        "label": {"fr": "Abandonné / Contre-indiqué", "nl": "Verlaten / Niet aangegeven", "en": "Abandoned / Against indicated"}
                    },
                    {
                        "id"       : "aborted",
                        "label": {"fr": "Abandonné / Décès", "nl": "Verlaten / ", "en": "Abandoned / Death"}
                    },
                    {
                        "id"       : "aborted",
                        "label": {"fr": "Abandonné / Désabonné", "nl": "Verlaten / Afgemeld", "en": "Abandoned / Unsubscribed"}
                    },
                    {
                        "id"       : "error",
                        "label": {"fr": "Abandonné / Erreur", "nl": "Verlaten /", "en": "Abandoned / Error"}
                    },
                    {
                        "id"       : "aborted",
                        "label": {"fr": "Abandonné / Non pertient", "nl": "Verlaten / Irrelevant", "en": "Abandoned / Not relevant"}
                    },
                    {
                        "id"       : "refused",
                        "label": {"fr": "Abandonné / Refus patient", "nl": "Verlaten / Weigering van de patiënt", "en": "Abandoned / Patient refusal"}
                    },
                    {
                        "id"       : "aborted",
                        "label": {"fr": "Abandonné / Trop tard", "nl": "Verlaten / ", "en": "Abandoned / Too late"}
                    },
                    {
                        "id"       : "aborted",
                        "label": {"fr": "Abandonné par le patient", "nl": "Verlaten / erwachting", "en": "Abandoned by patient"}
                    },
                    {
                        "id"       : "pending",
                        "label": {"fr": "En attente", "nl": "Verwachting", "en": "Waiting"}
                    },
                    {
                        "id"       : "planned",
                        "label": {"fr": "En attente planifié", "nl": "Gepland wachten", "en": "Scheduled waiting"}
                    },
                    {
                        "id"       : "completed",
                        "label": {"fr": "Fait", "nl": "Geëxecuteerd", "en": "Done"}
                    },
                    {
                        "id"       : "proposed",
                        "label": {"fr": "Rappel envoyé", "nl": "Herinnering verzonden", "en": "Reminder sent"}
                    }
                ]
            },
            hcpListItem: {
                type: Array,
                value: []
            },
            filterValue: {
                type: String
            },
            proceduresFilterValue:{
                type: String
            },
            selectedItem: {
                type: Object,
                value: null
            },
            isDeadline: {
                type: Boolean,
                value: false
            },
            hcpartyTypeList: {
                type: Array,
                value: []
            },
            isStatusComplete : {
                type: Boolean,
                value: false
            },
            isStatusRefusal:{
                type: Boolean,
                value: false
            },
            proceduresListItem:{
                type: Array,
                value: []
            },
            isVaccineProcedure:{
                type: Boolean,
                value: false
            },
            drugsFilterValue:{
                type: String
            },
            drugsListItem:{
                type: Array,
                value: []
            }
				};
    }

    static get observers() {
				return ["_prepareDataProvider(contact, contacts.*, servicesMap.*, form, patient,user)", '_hcpFilterChanged(filterValue)', '_proceduresFilterChanged(proceduresFilterValue)', '_drugsFilterChanged(drugsFilterValue)'];
    }

    constructor() {
				super();
    }

    _openActionDialog(){
        this.set('plannedAction.HcpId', this.user.healthcarePartyId)
        this.$['planActionForm'].open()

    }

    detached() {
				this.flushSave();
    }

    _isNotInCurrentContact(currentContact, contact, form) {
				return form && contact && (!currentContact || (contact !== currentContact && !currentContact.subContacts.some(sc => sc.formId === form.id)));
    }

    linkForm(e) {
        const he = e.detail.healthElement
        if (!he) {
            return;
        }
        const ctc = this.currentContact
        if (!he.id) {
            this.promoteServiceToHealthElement(he).then(he => {
                if (!ctc.subContacts.some(sc => sc.formId === this.formId && sc.healthElementId === he.id)) {
                    if (this.contact === ctc) {
                        this.push('contact.subContacts', { formId: this.formId, healthElementId: he.id, services: [] })
                    } else {
                        ctc.subContacts.push({ formId: this.formId, healthElementId: he.id, services: [] })
                    }

                    this.scheduleSave(ctc);
                }
            });
        } else if (!this._selectedHes().includes(he.id)) {
            if (this.contact === ctc) {
                this.push('contact.subContacts', { formId: this.formId, healthElementId: he.id, services: [] })
            } else {
                ctc.subContacts.push({ formId: this.formId, healthElementId: he.id, services: [] })
            }

            this.scheduleSave(ctc);
        }
    }

    _highlight(e) {
			 	const id = e.target.id.substr(5)
				const hHe = this.highlightedHes.find(he=>he.id===id)
				if (!hHe) {
			 	    const newHe = this.healthElements.find(he => he.id === id)
            newHe && this.push('highlightedHes', newHe)
        } else {
			 	    this.splice('highlightedHes',this.highlightedHes.indexOf(hHe),1)
				}
    }

    _heColourStyle(he) {
				return this.highlightedHes.includes(he) ? "he-pill-highlighted "+(he.colour || "ICPC-2-fallback")+"-back" : (he.colour || "ICPC-2-fallback")
    }

    _selectedHes() {
        if (!this.dataProvider || !this.healthElements) {return []}

        const svcsStructs = _.chain(this.dataProvider.servicesInHierarchy()).map(svcLine => svcLine[0]).compact().value()
        const heIds = _.chain(svcsStructs).flatMap(svcstr=>svcstr.ctc.subContacts.filter(sc => sc.services.some(s=>s.serviceId === svcstr.svc.id)).map(sc => sc.healthElementId)).compact().uniq().value()
				const scHeIds = _.chain(this.contact.subContacts.filter(sc => sc.formId === this.formId).map(sc => sc.healthElementId)).compact().uniq().value()

				return this.healthElements.filter(he => he.id && (heIds.includes(he.id) || scHeIds.includes(he.id)))
    }

    _allContacts() {
        return this.contacts.includes(this.contact) ? this.contacts : _.concat([this.contact],this.contacts)
    }

    _prepareDataProvider() {
				if (this.contacts && this.contact && this.form && this.user && this.patient && this.servicesMap) {
            this.set('dataProvider', this.getDataProvider(this.form, ''));
            this.set('dataMap', _.fromPairs(this.form.template.layout ? _.flatten(_.flatten(this.form.template.layout.sections.map(s => s.formColumns)).map(c => c.formDataList)).map(f => [f.name, 1]) : _.flatten(this._allContacts().map(c => {
                const sc = c.subContacts.find(sc => sc.formId === this.form.id);
                return sc ? c.services.filter(s => sc.services.map(s => s.serviceId).includes(s.id)).map(s => [s.label, s]) : [];
            }))));
            if (!this.form.template.layout) {
                this.set('form.template.layout', {
                    sections: [{
                        formColumns: [{
                            formDataList: _.compact(Object.values(this.dataMap).map((s, idx) => {
                                const c = this.localizedContent(s, this.language);
                                return c ? {
                                    name: s.label,
                                    label: s.label,
                                    editor: {
                                        key: c.numberValue || c.numberValue === 0 ? 'NumberEditor' : c.measureValue ? 'MeasureEditor' : "StringEditor",
                                        left: 0,
                                        width: 100,
                                        top: idx * 20,
                                        multiline: true
                                    }
                                } : null;
                            }))
                        }]
                    }]
                });
            } else {
                this.set('layoutInfoPerLabel', (this.form.template.layout.sections && _.flatMap(this.form.template.layout.sections, s => s.formColumns && _.flatMap(s.formColumns, c => c.formDataList || []) || []) || []).reduce((acc, fli) => {
                    acc[fli.name] = fli; return acc;
                }, {}));
            }
            !this.currentContact && this.set('currentContact', this._allContacts().find(c => !c.closingDate) || null);
				}
    }

    scheduleSave(ctc) {
				if (!ctc) {
            return;
				}
				this.dispatchEvent(new CustomEvent('data-change', { detail: ctc, bubbles: true, composed: true })); //Must be fired before the end of the save otherwise the element won't exist anymore and the event will not bubble up
				if (this.saveTimeout) {
            clearTimeout(this.saveTimeout);
				}
				this.saveAction = () => {
            this.dispatchEvent(new CustomEvent('must-save-contact', { detail: ctc, bubbles: true, composed: true })); //Must be fired before the end of the save otherwise the element won't exist anymore and the event will not bubble up
				};
				this.saveTimeout = setTimeout(this.saveAction, 10000);
    }

    shouldSave() {
        return !!this.saveTimeout
    }

    flushSave() {
				if (this.saveTimeout) {
            clearTimeout(this.saveTimeout);
            this.saveAction();

            this.saveTimeout = undefined;
            this.saveAction = undefined;
				}
    }

    _timeFormat(date) {
				return date ? this.api.moment(date).format(date > 99991231 ? 'DD/MM/YYYY HH:mm' : 'DD/MM/YYYY') : '';
    }

    getDataProvider(form, rootPath) {
				const initWrapper = (label, init) => svc => {
            const li = this.layoutInfoPerLabel[label];
            if (li) {
                li.tags && li.tags.forEach(tag => {
                    const exTag = svc.tags.find(t => t.type === tag.type);
                    if (exTag) {
                        exTag.code = tag.code;
                        if (exTag.id) {
                            exTag.id = tag.type + '|' + tag.code + "|" + (exTag.id.split('|')[2] || '1');
                        }
                    } else {
                        svc.tags = (svc.tags || []).concat([tag]);
                    }
                });
                li.codes && li.codes.forEach(code => {
                    const exCode = svc.codes.find(c => c.type === code.type && c.code === code.code);
                    if (!exCode) {
                        svc.codes = (svc.codes || []).concat([code]);
                    }
                });
                if (li.defaultStatus || li.defaultStatus === 0) {
                    svc.status = li.defaultStatus;
                }
            }
            return init && init(svc) || svc;
				};

				const self = {
            servicesMap: {},
            //Returns an array of arrays of svcStructs (a svcStruct is a map with the ctc, the svc and the subContacts this svc belongs to). All svcStructs in the second array share the same svc id
            services: label => {
                if (label && self.servicesMap[label]) {
                    return self.servicesMap[label];
                }
                return label ? self.servicesMap[label] = this.servicesInForm(form.id, label) : this.servicesInForm(form.id);
            },
            servicesInHierarchy: label => {
                return _.concat(self.services(label), _.flatMap(form.children, sf => this.getDataProvider(sf, (rootPath.length ? rootPath + '.' : '') + sf.descr + '.' + form.children.filter(sff => sff.descr === sf.descr).indexOf(sf)).servicesInHierarchy(label)));
            },
            dispatchService: svc => {
                if (!svc) {
                    return null;
                }
                delete self.servicesMap[svc.label];
                this.dispatchEvent(new CustomEvent('new-service', {
                    detail: {
                        ctc: this.currentContact,
                        svc: svc,
                        scs: this.currentContact.subContacts.filter(sc => sc.formId === form.id)
                    }, composed: true
                }));

                return svc;
            },
            promoteOrCreateService: (label, formId, poaIds, heIds, init) => {
                const s = self.getServiceInContact(label);
                if (!this.currentContact) {
                    return s && s.svc;
                }
                return s && s.svc && (s.ctc.id === this.currentContact.id ? initWrapper(label, init)(s.svc) : self.dispatchService(this.promoteServiceInCurrentContact(s.svc, formId, poaIds, heIds, initWrapper(label, init)))) || self.dispatchService(this.createService(label, formId, poaIds, heIds, null, initWrapper(label, init)));
            },
            getOrCreateContent: (svc, lng) => svc && (svc.content && svc.content[lng] || ((svc.content || (svc.content = {}))[lng] = {})),
            //Returns an array of svcStructs (a svcStruct is a map with the ctc, the svc and the subContacts this svc belongs to). All svcStructs share the same svc id. If there is a choice between several series of services. Pick the one that preferably appears in the contact.
            getServicesLineForContact: label => {
                const sss = self.services(label);
                return sss && (sss.find(svcs => svcs.find(ss => ss.ctc.id === this.contact.id)) || sss[0]) || [];
            },
            getServiceInContact: label => {
                const ssLine = self.getServicesLineForContact(label);
                return ssLine && ssLine.find(ss => !this.api.after(ss.ctc.created, this.contact.created));
            },
            wasModified: label => {
                const s = self.getServiceInContact(label);
                return s && (this.api.before(s.ctc.openingDate, this.contact.openingDate) || this.api.before(s.ctc.created, this.contact.created));
            },
            isModifiedAfter: label => {
                const s = self.getServicesLineForContact(label);
                return s && s[0] && s[0] !== self.getServiceInContact(label);
            },
            latestModification: label => {
                const s = self.getServicesLineForContact(label);
                return s && s[0] && this._timeFormat(s[0].ctc.openingDate);
            },
            getValueContainers: label => {
                const c = _.compact(self.services(label).map(line => line && line[0]).map(s => s && s.svc && !s.svc.endOfLife && s.svc)).map(s => _.cloneDeep(s)); //Never provide the real objects so that we can compare them later on
                return c;
            },
            setValueContainers: (label, containers) => {
                if (!this.currentContact) {
                    return;
                }
                let currentValueContainers = self.getValueContainers(label);
                if (_.isEqual(currentValueContainers, containers)) {
                    return;
                }
                const isModified = containers.map(service => {
                    let svc = this.currentContact.services.find(s => s.id === service.id);
                    if (svc) {
                        _.pull(currentValueContainers, currentValueContainers.find(s => s.id === service.id));
                        if (!_.isEqual(svc.content, service.content) || svc.index !== service.index || svc.endOfLife) {
                            _.extend(svc.content, _.cloneDeep(service.content));
                            svc.index = service.index;
                            delete svc.endOfLife;

                            return true;
                        }
                    } else {
                        const prevSvc = currentValueContainers.find(s => s.id === service.id);
                        if (prevSvc) {
                            _.pull(currentValueContainers, currentValueContainers.find(s => s.id === service.id));
                        }
                        if (!prevSvc || !_.isEqual(prevSvc.content, service.content)) {
                            self.dispatchService(_.extend(this.createService(label, form.id, null, (this.highlightedHes || []).map(he => he.id), service.id, initWrapper(label)),
                                { index: service.index, content: _.cloneDeep(service.content), codes: service.codes && service.codes.map(_.cloneDeep) }));
                            return true;
                        }
                    }
                    return false;
                }).find(x => x);
                currentValueContainers.forEach(service => {
                    let svc = this.currentContact.services.find(s => s.id === service.id);
                    if (svc) {
                        svc.endOfLife = +new Date() * 1000;
                    } else {
                        self.dispatchService(_.extend(this.createService(label, form.id, null, null, service.id, initWrapper(label)), { endOfLife: +new Date() * 1000 }));
                    }
                });
                if (isModified || currentValueContainers.length) {
                    this.scheduleSave(this.currentContact);
                }
            },
            getStringValue: (label, original) => {
                const s = !original ? self.getServicesLineForContact(label)[0] : self.getServiceInContact(label);
                const c = s && s.svc && !s.svc.endOfLife && this.localizedContent(s.svc, this.language);
                return c && c.stringValue;
            },
            getNumberValue: (label, original) => {
                const s = !original ? self.getServicesLineForContact(label)[0] : self.getServiceInContact(label);
                const c = s && s.svc && !s.svc.endOfLife && this.localizedContent(s.svc, this.language);
                return c && c.numberValue;
            },
            getMeasureValue: (label, original) => {
                const s = !original ? self.getServicesLineForContact(label)[0] : self.getServiceInContact(label);
                const c = s && s.svc && !s.svc.endOfLife && this.localizedContent(s.svc, this.language);
                return c && c.measureValue;
            },
            getDateValue: (label, original) => {
                const s = !original ? self.getServicesLineForContact(label)[0] : self.getServiceInContact(label);
                const c = s && s.svc && !s.svc.endOfLife && this.localizedContent(s.svc, this.language);
                return c && (c.fuzzyDateValue || c.instantValue);
            },
            getBooleanValue: (label, original) => {
                const s = !original ? self.getServicesLineForContact(label)[0] : self.getServiceInContact(label);
                const c = s && s.svc && !s.svc.endOfLife && this.localizedContent(s.svc, this.language);
                return c && c.booleanValue;
            },
            getValueDateOfValue: (label, original) => {
                const s = !original ? self.getServicesLineForContact(label)[0] : self.getServiceInContact(label);
                return s && s.svc && !s.svc.endOfLife && s.svc.valueDate;
            },
            setStringValue: function (label, value) {
                const currentValue = self.getStringValue(label)
                if ((currentValue || null) === (value || null) || ((!currentValue || !currentValue.length) && (!value || !value.length))) {
                    return;
                }
                self.promoteOrCreateService(label, form.id, null, (this.highlightedHes || []).map(he => he.id), svc => {
                    let c = self.getOrCreateContent(svc, this.language);
                    if (c && c.stringValue !== value) {
                        c.stringValue = value;
                        this.scheduleSave(this.currentContact);
                    }
                    return svc;
                });
            }.bind(this),
            setNumberValue: function (label, value) {
                if (self.getNumberValue(label) === parseFloat(value)) {
                    return;
                }
                self.promoteOrCreateService(label, form.id, null, (this.highlightedHes || []).map(he => he.id), svc => {
                    let c = self.getOrCreateContent(svc, this.language);
                    if (c && c.numberValue !== value) {
                        c.numberValue = value;
                        this.scheduleSave(this.currentContact);
                    }
                    return svc;
                });
            }.bind(this),
            setMeasureValue: function (label, value) {
                const currentValue = self.getMeasureValue(label);
                if (((!currentValue || !currentValue.value) && !value.value) /* No valid current value and no proposed value */
                    || (currentValue && value && ((currentValue.value || 0) === (value.value || 0)) && ((currentValue.unit || null) === (value.unit || null)))) {
                    return;
                }
                self.promoteOrCreateService(label, form.id, null, (this.highlightedHes || []).map(he => he.id), svc => {
                    let c = self.getOrCreateContent(svc, this.language);
                    if (c && c.measureValue !== value) {
                        c.measureValue = value;
                        this.scheduleSave(this.currentContact);
                    }
                    return svc;
                });
            }.bind(this),
            setDateValue: function (label, value) {
                if (self.getDateValue(label) === value) {
                    return;
                }
                self.promoteOrCreateService(label, form.id, null, (this.highlightedHes || []).map(he => he.id), svc => {
                    let c = self.getOrCreateContent(svc, this.language);
                    if (c && c.fuzzyDateValue !== value) {
                        c.fuzzyDateValue = value;
                        this.scheduleSave(this.currentContact);
                    }
                    return svc;
                });
            }.bind(this),
            setBooleanValue: function (label, value) {
                if (self.getBooleanValue(label) === value) {
                    return;
                }
                self.promoteOrCreateService(label, form.id, null, (this.highlightedHes || []).map(he => he.id), svc => {
                    let c = self.getOrCreateContent(svc, this.language);
                    if (c && c.booleanValue !== value) {
                        c.booleanValue = value;
                        this.scheduleSave(this.currentContact);
                    }
                    return svc;
                });
            }.bind(this),
            setValueDateOfValue: function (label, value, setBooleanValue) {
                if (self.getValueDateOfValue(label) === value) {
                    return;
                }
                self.promoteOrCreateService(label, form.id, null, (this.highlightedHes || []).map(he => he.id), svc => {
                    if (!svc) {
                        return;
                    }
                    if (svc.valueDate !== value) {
                        svc.valueDate = value;
                        if (setBooleanValue) {
                            let c = self.getOrCreateContent(svc, this.language);
                            if (c && c.booleanValue !== value) {
                                c.booleanValue = value;
                            }
                        }
                        this.scheduleSave(this.currentContact);
                    } else if (setBooleanValue) {
                        self.setBooleanValue(!!value);
                    }
                    return svc;
                });
            }.bind(this),
            getSubForms: function (key) {
                return (form.children || []).filter(f => f.descr === key).map((subForm, idx) => {
                    return {
                        dataMap: _.fromPairs(_.flatten(_.flatten(subForm.template.layout.sections.map(s => s.formColumns)).map(c => c.formDataList)).map(f => [f.name, 1])),
                        dataProvider: this.getDataProvider(subForm, (rootPath.length ? rootPath + '.' : '') + key + '.' + idx),
                        template: subForm.template.layout
                    };
                });
            }.bind(this),
            editForm: function () {
                this.dispatchEvent(new CustomEvent('edit-form', { detail: form, composed: true }));
            }.bind(this),
            deleteForm: function () {
                if (!this.currentContact) {
                    return;
                }
                this.flushSave();

                const id = form.id;
                const subContacts = this.currentContact.subContacts.filter(sc => sc.formId === id);
                _.pullAll(this.currentContact.subContacts, subContacts);

                //Get all services in the formId
                this.servicesInForm(id).forEach(sl => {
                    if (sl.length >= 1 && sl[0].ctc === this.currentContact) {
                        sl[0].svc.content = {};
                        sl[0].svc.endOfLife = +new Date() * 1000;
                    } else {
                        (this.currentContact.services || (this.currentContact.services = [])).push(self.dispatchService(_.extend(_.cloneDeep(sl[0].svc), {
                            content: {},
                            endOfLife: +new Date() * 1000
                        })));
                    }
                });

                this.api.form().modifyForm(_.extend(form, { deletionDate: +new Date() * 1000 })).then(f => {
                    this.dispatchEvent(new CustomEvent('form-deleted', { detail: f, composed: true }));
                });
            }.bind(this),
            getId: () => form.id,
            deleteSubForm: (key, id) => {
                if (!this.currentContact) {
                    return;
                }
                this.flushSave();

                const ff = form.children.find(a => a.id === id);

                _.pull(form.children, ff);
                const subContacts = this.currentContact.subContacts.filter(sc => sc.formId === id);
                _.pullAll(this.currentContact.subContacts, subContacts);

                //Get all services in the formId
                this.servicesInForm(id).forEach(sl => {
                    if (sl.length >= 1 && sl[0].ctc === this.currentContact) {
                        sl[0].svc.content = {};
                        sl[0].svc.endOfLife = +new Date() * 1000;
                    } else {
                        (this.currentContact.services || (this.currentContact.services = [])).push(self.dispatchService(_.extend(_.cloneDeep(sl[0].svc), {
                            content: {},
                            endOfLife: +new Date() * 1000
                        })));
                    }
                });

                this.api.form().modifyForm(_.extend(ff, { deletionDate: +new Date() * 1000 })).then(f => {
                    this.$['dynamic-form'].notify((rootPath.length ? rootPath + '.' : '') + key + '.*');
                    this.scheduleSave(this.currentContact);
                });
            },
            addSubForm: (key, guid) => {
                if (!this.currentContact) {
                    return;
                }
                this.flushSave();
                this.api.hcparty().getCurrentHealthcareParty().then(hcp => this.api.form().getFormTemplatesByGuid(guid, hcp.specialityCodes[0] && hcp.specialityCodes[0].code || 'deptgeneralpractice')).then(formTemplates => {
                    if (formTemplates[0] && formTemplates[0]) {
                        //Create a new form and link it to the currentContact
                        this.api.form().newInstance(this.user, this.patient, {
                            contactId: this.currentContact.id,
                            descr: key,
                            formTemplateId: formTemplates[0].id,
                            parent: form.id
                        }).then(f => this.api.form().createForm(f)).then(f => {
                            f.template = formTemplates[0]; //Important
                            (form.children || (form.children = [])).push(f);
                            this.currentContact.subContacts.push({ formId: f.id, descr: key, services: [] });

                            this.$['dynamic-form'].notify((rootPath.length ? rootPath + '.' : '') + key + '.*');
                            this.scheduleSave(this.currentContact);
                        });
                    }
                });
            },
            filter: (data, text) => {
                return data.source === "codes" && data.types.length && text && text.length > 1 ? Promise.all(data.types.map(ct => {
                    const typeLng = this.api.code().languageForType(ct.type, this.language);
                    const words = text.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, "").split(/\s+/);
                    const sorter = x => [x.stringValue && x.stringValue.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, "").startsWith(words[0]) ? 0 : 1, x.stringValue]

                    return this.api.code().findPaginatedCodesByLabel('be', ct.type, typeLng, words[0], null, null, 200).then(results => _.sortBy(results.rows.filter(c => c.label[typeLng] && words.every(w => c.label[typeLng].toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, "").includes(w))).map(code => ({
                        id: code.id, stringValue: code.label[typeLng],
                        codes: [code].concat(code.links && code.links.map(c => ({ id: c, type: c.split('|')[0], code: c.split('|')[1], version: c.split('|')[2] })) || [])
                    })), sorter));
                })).then(responses => _.flatMap(responses)) : Promise.resolve([]);
            }
				};
				return self;
    }

    _formIdChanged(formId) {
				if (!formId) {
            return;
				}

				const loadForms = function (templates, forms, root) {
            const newFormTemplateIds = forms.map(f => f.formTemplateId).filter(id => id && !templates[id]);
            return Promise.all([
                Promise.all(newFormTemplateIds.map(id => this.api.form().getFormTemplate(id))),
                Promise.all(forms.map(f => this.api.form().getChildren(f.id, this.user.healthcarePartyId)))
            ]).then(res => {
                const [fts,children] = res
                fts.forEach(ft => { templates[ft.id] = ft });
                forms.forEach(f => f.template = f.formTemplateId ? templates[f.formTemplateId] : { layout: null, name: "Dynamic" })
                children.forEach((cs, idx) => {
                    forms[idx].children = cs;
                    cs.forEach(c => forms[c.id] = cs);
                });
                return children.length ? loadForms(templates, _.flatten(children), root) : root;
            });
				}.bind(this);
				this.api.form().getForm(formId).then(f => loadForms(this.api.cachedTemplates || (this.api.cachedTemplate = {}), [f], f)).then(form => this.set('form', form));
    }

    deleteForm() {
				this.dataProvider.deleteForm && this.dataProvider.deleteForm();
    }


    linkAllServicesInForm(he) {
        _.compact(this.dataProvider.servicesInHierarchy().map(svcLine => svcLine[0])) //Latest version of all services
            .forEach(svc => {
                this.promoteServiceInCurrentContact(svc.svc, this.form.id, null, [he.id], null);
            });
        this.scheduleSave(this.currentContact);
    }

    //Returns an array of arrays of svcStructs (a svcStruct is a map with the ctc, the svc and the subContacts this svc belongs to). All svcStructs in the second array share the same svc id
    servicesInForm(formId, label) {
				const svcStructs = (label ? this.servicesMap[label] : _.flatten(Object.values(this.servicesMap))) || [];
				return _.sortBy(_.uniqBy(svcStructs.filter(ss => (ss.scs || []).find(sc => sc.formId === formId)) //Extract all services which appear at some point in that form
            .map(ss => ss.svc.id)) //Get their ids
            .map(id => _.sortBy(svcStructs.filter(ss => ss.svc.id === id), ss => -ss.svc.modified)) //Sort them by modified for each id
            .filter(svcHistory => svcHistory.length) //Keep the ones with a history
            , svcs => -svcs[0].svc.modified); //Sort the lines of services by modification date
    }

    services(ctc, label) {
				return this.api && this.api.contact().services(ctc, label) || [];
    }

    createService(label, formId, poaId, heId, serviceId, init) {
				if (!this.currentContact) {
            return null;
				}
				const svc = this.api.contact().service().newInstance(this.user, serviceId ? { id: serviceId, label: label } : { label: label });
				(this.currentContact.services || (this.currentContact.services = [])).push(svc);

				let sc = this.currentContact.subContacts.find(sc => sc.formId === formId);
				if (!sc) {
            this.currentContact.subContacts.push(sc = { formId: formId, planOfActionId: poaId, healthElementId: heId, services: [] });
				}

				const csc = this.currentContact.subContacts.find(csc => csc.services.indexOf(svc.id) >= 0);
				if (csc) {
            if (csc !== sc) {
                csc.splice(csc.services.indexOf(svc.id), 1);
                sc.services.push({ serviceId: svc.id });
            }
				} else {
            sc.services.push({ serviceId: svc.id });
				}
				return init && init(svc) || svc;
    }

    promoteServiceToHealthElement(heSvc) {
				return this.api.helement().serviceToHealthElement(this.user, this.patient, heSvc.svc,
            this.api.contact().shortServiceDescription(heSvc.svc, language)).then(he => {
            this.promoteServiceInCurrentContact(heSvc.svc, this.form.id, null, [he.id], null);
            this.scheduleSave(this.currentContact);
            this.dispatchEvent(new CustomEvent('health-elements-change', { detail: { hes: [he] }, bubbles: true, composed: true }));

            return he;
				});
    }

    promoteServiceInCurrentContact(svc, formId, poaIds, heIds, init) {
				return this.api.contact().promoteServiceInContact(this.currentContact, this.user, this._allContacts(), svc, formId, poaIds, heIds, init);
    }

    shortServiceDescription(svc, lng) {
				let rawDesc = this.api && this.api.contact().shortServiceDescription(svc, lng);
				return rawDesc && '' + rawDesc || '';
    }

    contentHasData(c) {
				return this.api && this.api.contact().contentHasData(c) || false;
    }

    _localize(e, lng) {
				return this.api && this.api.contact().localize(e, lng) || "";
    }

    localizedContent(svc, lng) {
				return this.api && svc && this.api.contact().localize(svc.content, lng) || {};
    }

    _hcpFilterChanged(e){

        let latestSearchValue = this.filterValue || e && e.detail.value;
        this.latestSearchValue = latestSearchValue;
        if (!latestSearchValue || latestSearchValue.length < 2) {
            console.log("Cancelling empty search");
            this.set('hcpListItem', []);
            return;
        }
        this._hcpDataProvider() && this._hcpDataProvider().filter(latestSearchValue).then(res => {
            if (latestSearchValue !== this.latestSearchValue) {
                console.log("Cancelling obsolete search");
                return;
            }
            this.set('hcpListItem', res.rows);
        });
    }

    _proceduresFilterChanged(e){
        let latestSearchValue = this.filterValue || e && e.detail.value;
        this.latestSearchValue = latestSearchValue;
        if (!latestSearchValue || latestSearchValue.length < 2) {
            console.log("Cancelling empty search");
            this.set('proceduresListItem', []);
            return;
        }
        this._proceduresDataProvider() && this._proceduresDataProvider().filter(latestSearchValue).then(res => {
            if (latestSearchValue !== this.latestSearchValue) {
                console.log("Cancelling obsolete search");
                return;
            }
            this.set('proceduresListItem', res.rows);
        });
    }

    _drugsFilterChanged(e){
        let latestSearchValue = this.filterValue || e && e.detail.value;
        this.latestSearchValue = latestSearchValue;
        if (!latestSearchValue || latestSearchValue.length < 2) {
            console.log("Cancelling empty search");
            this.set('drugsListItem', []);
            return;
        }
        this._drugsDataProvider() && this._drugsDataProvider().filter(latestSearchValue).then(res => {
            if (latestSearchValue !== this.latestSearchValue) {
                console.log("Cancelling obsolete search");
                return;
            }
            this.set('drugsListItem', res.rows);
        });
    }

    _hcpDataProvider() {
        return {
            filter: function (filterValue) {
                const desc = 'desc';
                let count = 15;
                return Promise.all([this.api.hcparty().findByName(filterValue, null,  null, count, desc)]).then(results => {
                    const hcpList = results[0];
                    const filtered = _.flatten(hcpList.rows.filter(hcp => hcp.lastName && hcp.lastName !== "" || hcp.firstName && hcp.firstName !== "").map(hcp => ({id: hcp.id , name : hcp.lastName + ' ' +hcp.firstName}) ));
                    return { totalSize: filtered.length, rows: filtered };
                });

            }.bind(this)
        };
    }

    _proceduresDataProvider(){
        return {
            filter: function (proceduresFilterValue) {
                let count = 15;
                return Promise.all([this.api.code().findPaginatedCodesByLabel('be', 'BE-THESAURUS-PROCEDURES', 'fr', proceduresFilterValue, null, null, count)]).then(results => {
                    const procedureList = results[0];
                    const filtered = _.flatten(procedureList.rows.map(procedure => ({id: procedure.id , label : procedure.label, code : procedure.code, searchTerms : procedure.searchTerms}) ));
                    return { totalSize: filtered.length, rows: filtered};
                });

            }.bind(this)
        };
    }

    _drugsDataProvider(){
        return {
            filter: function (drugsFilterValue) {
                let count = 15;
                return Promise.all([this.api.bedrugs().getMedecinePackages(drugsFilterValue, this.language, null, 0, count)]).then(results => {
                    const drugsList = results[0];
                    const filtered = _.flatten(drugsList.map(drugs => ({name: drugs.name, id: drugs.id.id}) ));
                    return { totalSize: filtered.length, rows: filtered };
                });

            }.bind(this)
        };
    }


    _checkIsDeadline(){
        if(this.plannedAction.Deadline !== ""){
            this.set('plannedAction.isDeadline', true)
				}else{
            this.set('plannedAction.isDeadline', false)
				}

    }

    _isSendMailCheck(e){
        this.set('plannedAction.isSendMail', e.target.checked)
    }

    _isSurgical(e){
        this.set('plannedAction.isSurgical', e.target.checked)
    }

    analyzeStatus(e){
        const status = e.detail.value

        if(status === "completed"){
            this.set("isStatusComplete", true)
        }else{
            this.set("isStatusComplete", false)
        }

        if(status === "refused"){
            this.set("isStatusRefusal", true)
        }else{
            this.set("isStatusRefusal", false)
        }
    }

    detectVaccinProcedure(e){
        const code = e.detail.value
        let codeExp = code.split(".");
        let CISPType = codeExp[0].substr(1,3)

        if(CISPType === "44"){
            this.set("isVaccineProcedure", true)
        }else{
            this.set("isVaccineProcedure", false)
        }
    }

    _statusChanged(detail){

        const label = detail.detail.representedObject
        const services = this.api.contact().services(this.currentContact, label)
				var biteArray = [0,0,0]

				if(detail.detail.status === "stat_act"){
				    biteArray[0] = 1
				}else if(detail.detail.status === "stat_pas_rev"){
				    biteArray[1] = 1
				}else if(detail.detail.status === "stat_n_pres"){
				    biteArray[2] = 1
				}

				const status = parseInt(biteArray[0]+""+biteArray[1]+""+biteArray[2], 2)

				services.map(service => {
				    service.status = status
            const indexOfService = this.currentContact.services.indexOf(service)
            this.set('currentContact.services[indexOfService]', service)
        })

        this.scheduleSave(this.currentContact);

    }

    _apiChanged() {

        if (this.api) {
            this.api.cacheRowsUsingPagination(
                'CD-HCPARTY-pers',
                (key,docId) =>
                    this.api.code().findPaginatedCodesByLabel('be', 'CD-HCPARTY', 'fr', 'pers', key && JSON.stringify(key), docId, 100)
                        .then(pl => ({
                            rows:pl.rows,
                            nextKey: pl.nextKeyPair && pl.nextKeyPair.startKey,
                            nextDocId: pl.nextKeyPair && pl.nextKeyPair.startKeyDocId,
                            done:!pl.nextKeyPair
                        }))
            ).then(rows => this.set('hcpartyTypeList', _.orderBy(rows, ['label.fr'], ['asc'])))
        }
    }

    planAction(){
				const tabProfession = this.plannedAction.ProfessionId.split("|")

        Promise.all(
            [
                this.api.code().findPaginatedCodesByLabel('be', 'BE-THESAURUS-PROCEDURES', 'fr', this.plannedAction.ProcedureId, null, null, 10),
                this.api.code().findPaginatedCodesByLabel('be', 'CD-HCPARTY', 'fr', tabProfession[1], null, null, 10)
            ]).then(
            ([results, code]) => {
                if(this.plannedAction.VaccineCommercialNameId !== ""){
                    this.api.bedrugs().getMppInfos(this.plannedAction.VaccineCommercialNameId, this.language).then(
                        mpp => {
                            this.set('plannedAction.VaccineInfo', mpp)
                            this.set('plannedAction.ProcedureInfo', results.rows[0])
                            this.set('plannedAction.ProfessionInfo', code.rows[0])
                            this._planAction()
                        }
                    )
                }else{
                    this.set('plannedAction.ProcedureInfo', results.rows[0])
                    this.set('plannedAction.ProfessionInfo', code.rows[0])
                    this._planAction()
                }
            });

    }

    _planAction(){
        console.log(this.plannedAction)

				const action = this.plannedAction
				const contactId = this.contact.id
				const label = "Actes"
        const responsible = action.HcpId !== "" ? action.HcpId : this.user.healthcarePartyId
				const valueDate = action.Deadline !== "" ? this.api.moment(action.Deadline).format('YYYYMMDD') : ""

        let act = {
            responsible: responsible,
            content: {
                fr: {
                    stringValue: action.ProcedureInfo.label.fr
                },
                nl: {
                    stringValue: action.ProcedureInfo.label.nl
                }
            },
            codes: [
                {
                    region:	action.ProcedureInfo.regions,
                    type: 	action.ProcedureInfo.type,
                    code: action.ProcedureId,
                    version : action.ProcedureInfo.version,
                    label: action.ProcedureInfo.label
                },
                {
                    region: action.ProfessionInfo.regions,
                    type: action.ProfessionInfo.type,
                    version : action.ProfessionInfo.version,
                    code: action.ProfessionInfo.code,
                    label: action.ProfessionInfo.label
                }
            ],
            comment: action.Description,
            valueDate: valueDate,
            tags: [
                {
                    type: "CD-LIFECYCLE",
                    code: action.Status,
                    version: "1.0"
                }
            ]
        }

        if(this.plannedAction.VaccineCommercialNameId !== ""){



            let vaccine = {
                region: ["be", this.plannedAction.VaccineInfo.id.lang],
                type: "CD-ITEM",
                code: "vaccine",
                version: "1.0",
                label: {"fr": this.plannedAction.VaccineInfo.name}
            }

            act.codes.push(vaccine)

            _.extend(this.createService(label, null, null, contactId), act)
        }else{
            _.extend(this.createService(label, null, null, contactId), act)
				}

        //this.scheduleSave(this.currentContact);
        //console.log(this.currentContact)
    }
}

customElements.define(DynamicallyLoadedForm.is, DynamicallyLoadedForm);
