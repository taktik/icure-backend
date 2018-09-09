import './dynamic-number-field.js';
import './dynamic-token-field.js';
import './dynamic-text-field.js';
import './dynamic-text-area.js';
import './dynamic-measure-field.js';
import './dynamic-popup-menu.js';
import './dynamic-date-field.js';
import './dynamic-sub-form.js';
import './dynamic-checkbox.js';
import './dynamic-medication-field.js';
import '../../icpc-styles.js';
import './ckmeans-grouping.js';
class DynamicForm extends Polymer.TkLocalizerMixin(Polymer.Element) {
    static get is() {
				return 'dynamic-form';
    }

    static get properties() {
				return {
            api: {
                type: Object
            },
            user: {
                type: Object
            },
            template: {
                type: Object,
                observer: '_templateChanged'
            },
            readOnly: {
                type: Boolean,
                value: false
            },
            compact: {
                type: Boolean,
                value: false
            },
            dataProvider: {
                type: Object
            },
            dataMap: {
                type: Object,
                value: null
            },
            isSubForm: {
                type: Boolean,
                value: false
            },
            showTitle: {
                type: Boolean,
                value: false
            },
            title: {
                type: String,
                value: null
            },
            displayedTitle: {
                type: String,
                computed: "_displayedTitle(title, showTitle, dataProvider)"
            },
            showEdit: {
                type: String,
                computed: "_showEdit(isSubForm, readOnly)"
            },
            healthElements: {
                type: Array
            },
            linkableHealthElements: {
                type: Array
            }
				};
    }

    constructor() {
				super();
    }

    _showEdit() {
				return this.readOnly && !this.isSubForm;
    }

    notify(path) {
				if (!this.template) {
            return;
				}
				let pathParts = path.split('.');
				const firstPathElement = pathParts[0];
				const item = Polymer.dom(this.root).querySelector('#sf_' + firstPathElement);

				const layoutItem = _.flatten(_.flatten(this.template.sections.map(s => s.formColumns.map(c => c.formDataList)))).find(fdl => fdl.name === firstPathElement);

				if (pathParts.length > 1) {
            item.subContexts = this._subForms(layoutItem);
            item.notify && item.notify(pathParts.slice(1).join('.'));
				} else {
            item.notify && item.notify();
				}
    }

    _displayedTitle() {
				return this.title && this.dataProvider ? this.title : "Loading …";
    }

    _linkForm(e) {
        const he = this.healthElements.find(he => he.id === e.target.id || he.idService === e.target.id);
        this.dispatchEvent(new CustomEvent('link-form', {detail: {healthElement: he}, composed: true, bubbles: true}));
    }

    _deleteForm() {
        this.dispatchEvent(new CustomEvent('delete-form', {composed: true, bubbles: true}));
    }


    _patCardClass(isSubForm) {
				return !isSubForm ? "pat-details-card" : "pat-details-card subform-card";
    }

    _value(layoutItem) {
				if (!this.dataProvider) {
            return null;
				}
				return this._isCheckboxField(layoutItem) ? '' + !!this._rawValue(layoutItem) : this._rawValue(layoutItem);
    }

    _status(layoutItem){
        if (!this.dataProvider) {
            return null;
        }

    }

    _rawValue(layoutItem) {
				if (!this.dataProvider) {
            return null;
				}
				return this._isDateField(layoutItem) ? this.dataProvider.getDateValue(layoutItem.name) : this._isMeasureField(layoutItem) ? this.dataProvider.getMeasureValue(layoutItem.name) : this._isCheckboxField(layoutItem) ? this.dataProvider.getBooleanValue(layoutItem.name) : this._isNumberField(layoutItem) ? this.dataProvider.getNumberValue(layoutItem.name) : this.dataProvider.getStringValue(layoutItem.name);
    }

    _shouldDisplay(layoutItem, readOnly, compact) {
				return !readOnly && !compact || this._isSubForm(layoutItem) || this._isMedicationField(layoutItem) && this.dataProvider.getValueContainers(layoutItem.name).length || this._rawValue(layoutItem);
    }

    _valueContainers(layoutItem) {
				if (!this.dataProvider) {
            return null;
				}
				return this.dataProvider.getValueContainers(layoutItem.name) || [];
    }

    _valueDate(layoutItem) {
				if (!this.dataProvider) {
            return null;
				}
				return this.dataProvider.getValueDateOfValue(layoutItem.name);
    }

    _subForms(layoutItem) {
				if (!this.dataProvider) {
            return null;
				}
				return this.dataProvider.getSubForms(layoutItem.name);
    }

    _templateChanged(change) {
				if (!this.template || !this.template.sections) {
            return;
				}
				this.layoutItemPerName = _.flatten(this.template.sections.map(s => _.flatten(s.formColumns.map(c => c.formDataList)))).reduce((acc, val) => {
            acc[val.name] = val;
            return acc;
				}, {});
    }

    _valueChanged(event) {
				if (!this.dataProvider) {
            return;
				}
				const change = event.detail;
				if (!this.layoutItemPerName || !event.target.id) {
            return;
				}
				const layoutItem = this.layoutItemPerName[event.target.id];
				if (layoutItem) {
            this._isDateField(layoutItem) ? this.dataProvider.setDateValue(layoutItem.name, change.value) : this._isMeasureField(layoutItem) ? this.dataProvider.setMeasureValue(layoutItem.name, typeof change.value === "object" ? change.value : { value: change.value }) : this._isCheckboxField(layoutItem) ? this.dataProvider.setBooleanValue(layoutItem.name, change.value && change.value !== 'false') : this._isNumberField(layoutItem) ? this.dataProvider.setNumberValue(layoutItem.name, change.value) : this.dataProvider.setStringValue(layoutItem.name, change.value);
				}
    }

    _valueContainersChanged(event) {
				if (!this.dataProvider) {
            return;
				}
				const change = event.detail;
				if (!this.layoutItemPerName || !event.target.id) {
            return;
				}
				const layoutItem = this.layoutItemPerName[event.target.id];
				if (layoutItem) {
            this._isTokenField(layoutItem) ? this.dataProvider.setValueContainers(layoutItem.name, change.value) : this._isMedicationField(layoutItem) ? this.dataProvider.setValueContainers(layoutItem.name, change.value) : null;
				}
    }

    _valueDateChanged(event) {
				if (!this.dataProvider) {
            return;
				}
				const change = event.detail;
				if (!this.layoutItemPerName || !event.target.id) {
            return;
				}
				const layoutItem = this.layoutItemPerName[event.target.id];
				if (layoutItem) {
            this.dataProvider.setValueDateOfValue(layoutItem.name, change.value);
				}
    }

    _valueDateChangedWithBooleanSet(event) {
				if (!this.dataProvider) {
            return;
				}
				const change = event.detail;
				if (!this.layoutItemPerName || !event.target.id) {
            return;
				}
				const layoutItem = this.layoutItemPerName[event.target.id];
				if (layoutItem) {
            this.dataProvider.setValueDateOfValue(layoutItem.name, change.value, true);
				}
    }

    _unit(layoutItem, dataMap) {
				if (!this.dataProvider) {
            return null;
				}
				return this._isMeasureField(layoutItem) ? (() => {
            const v = this.dataProvider.getMeasureValue(layoutItem.name); return v && v.unit;
				})() : null;
    }

    width(layoutItem) {
				return layoutItem;
    }

    _sortedGroupedFormDataList(formDataList) {
				const widthsStruct = formDataList.reduce((acc, i) => {
            acc.widths[i.name] = i.editor.left + i.editor.width; acc.maxWidth = Math.max(acc.widths[i.name], acc.maxWidth); return acc;
				}, { widths: {}, maxWidth: 32 });
				const sortedList = _.sortBy(formDataList, fd => fd.editor.top);
				const clusters = this.$['ckmeans-grouping'].cluster(sortedList.map(fd => fd.editor.top)).clusters;

				const formDataClusters = sortedList.reduce((cs, fd) => cs[_.findIndex(clusters, c => c.includes(fd.editor.top))].push(fd) && cs, new Array(clusters.length).fill(null).map(u => [])).map(c => _.sortBy(c, "editor.left"));
				formDataClusters.forEach(c => {
            let prevWidth = 0;
            for (let i = 0; i < c.length; i++) {
                let width = widthsStruct.widths[c[i].name];
                c[i].editor.flow = Math.floor(10000 * (width - prevWidth) / widthsStruct.maxWidth) / 100;
                prevWidth = width;
            }
				});

				//Now that the flow have been determined restart a kmeans
				const flowSortedList = _.sortBy(formDataList, fd => fd.editor.flow);
				const flowClustering = this.$['ckmeans-flow-grouping'].cluster(flowSortedList.map(fd => fd.editor.flow));

				//Round centroids
				flowClustering.centroids = flowClustering.centroids.map(c => Math.round(c * 12 / 100.0) * 100 / 12 - 0.00001);
				const treatedFormDataList = _.flatten(formDataClusters);
				treatedFormDataList.forEach(c => {
            c.editor.flow = Math.floor(flowClustering.centroids[_.findIndex(flowClustering.clusters, cc => cc.includes(c.editor.flow))] * 100) / 100;
				});
				formDataClusters.forEach(cs => {
            while (cs.reduce((acc, i) => acc + i.editor.flow, 0) > 100) {
                cs.reduce((max, i) => i.editor.flow > max.editor.flow ? i : max, { editor: { flow: 0 } }).editor.flow -= 8.33334;
            }
				});
				return treatedFormDataList;
    }

    _isTextArea(layoutItem) {
				return layoutItem.editor.key === 'StringEditor' && layoutItem.editor.multiline === true;
    }

    _isTextField(layoutItem) {
				return layoutItem.editor.key === 'StringEditor' && layoutItem.editor.multiline === false;
    }

    _isPopupMenu(layoutItem) {
				return layoutItem.editor.key === 'PopupMenuEditor';
    }

    _isNumberField(layoutItem) {
				return layoutItem.editor.key === 'NumberEditor';
    }

    _isDateField(layoutItem) {
				return layoutItem.editor.key === 'DateTimeEditor';
    }

    _isValueDateField(layoutItem) {
				return layoutItem.editor.key === 'CheckBoxEditor' && layoutItem.editor.displayValueDate;
    }

    _isCheckboxField(layoutItem) {
				return layoutItem.editor.key === 'CheckBoxEditor' && !layoutItem.editor.displayValueDate;
    }

    _isMeasureField(layoutItem) {
				return layoutItem.editor.key === 'MeasureEditor';
    }

    _isTokenField(layoutItem) {
				return layoutItem.editor.key === 'TokenFieldEditor';
    }

    _isMedicationField(layoutItem) {
				return layoutItem.editor.key === 'MedicationTableEditor';
    }

    _isSubForm(layoutItem) {
				return layoutItem.subForm === true;
    }

    _isModifiedAfter(layoutItem) {
				return this.dataProvider && this.dataProvider.isModifiedAfter && this.dataProvider.isModifiedAfter(layoutItem.name) || false;
    }

    _wasModified(layoutItem) {
				return this.dataProvider && this.dataProvider.wasModified && this.dataProvider.wasModified(layoutItem.name) || false;
    }

    _lastModified(layoutItem) {
				return this.dataProvider && this.dataProvider.latestModification && this.dataProvider.latestModification(layoutItem.name) || "0";
    }

    loadDataMap() {
				console.log("Form ready");
    }

    editForm() {
				this.dataProvider.editForm && this.dataProvider.editForm();
    }

    _deleteSubForm(e, detail) {
				e.stopPropagation();
				const layoutItem = Polymer.dom(this.root).querySelector('#layoutitems-repeat').itemForElement(e.target);
				this.dataProvider.deleteSubForm && this.dataProvider.deleteSubForm(layoutItem.name, detail.id);
    }

    _addSubForm(e, detail) {
				e.stopPropagation();
				const layoutItem = Polymer.dom(this.root).querySelector('#layoutitems-repeat').itemForElement(e.target);
				this.dataProvider.addSubForm && this.dataProvider.addSubForm(layoutItem.name, detail.guid);
    }

    _tokenDataSource(d) {
				return d && { filter: text => this.dataProvider && this.dataProvider.filter && this.dataProvider.filter(d.editor.dataSource || d.codeTypes && { source: "codes", types: d.codeTypes }, text) || Promise.resolve([]) } || null;
    }

    _popupDataSource(d, options) {
        const ds = d.editor.dataSource || d.codeTypes && { source: "codes", types: d.codeTypes }
				return d && (d.codeTypes && d.codeTypes.length || d.editor.dataSource) ? { filter: text => this.dataProvider && this.dataProvider.filter && this.dataProvider.filter(ds, text) || Promise.resolve([]), get: id => this.dataProvider && this.dataProvider.filter && this.dataProvider.filter(ds, null, id) || Promise.resolve(null) } : null;
    }


}

customElements.define(DynamicForm.is, DynamicForm);
