/**
@license
Copyright (c) 2016 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/
import '../filter-panel/filter-panel.js';

import '../collapse-button/collapse-button.js';
import '../icons/icure-icons.js';
import '../../icd-styles.js';
import '../dynamic-form/entity-selector.js';
import '../dynamic-form/health-problem-selector.js';

import moment from 'moment/src/moment';
import _ from 'lodash/lodash';
import styx from '../../../scripts/styx';

import {PolymerElement, html} from '@polymer/polymer';
class HtMsgMenu extends TkLocalizerMixin(PolymerElement) {
  static get template() {
    return html`
		<style include="iron-flex iron-flex-alignment"></style>
		<!--suppress CssUnusedSymbol -->
		<style include="shared-styles">
			:host {
				display: block;
				z-index:3;
			}

			:host *:focus{
				outline:0!important;
			}

			.col-left{	
				box-sizing: border-box;
				grid-column: 1 / 1;
    			grid-row: 1 / 1;
				background:var(--app-background-color-dark);
				float:left;
				@apply --shadow-elevation-3dp;
				padding:20px;
				display:flex;
				flex-flow: column nowrap;
				align-items: center;
				height: 100%;
				width: 100%;
			}

			.new-msg-btn{
				margin-bottom: 16px;
				--paper-button: {
					background: var(--app-secondary-color);
					color: var(--app-text-color);
					width: 80%;
					margin: 0 auto;
					font-size: 12px;
					font-weight: bold;
				};
				--paper-button-ink-color: var(--app-secondary-color-dark);
			}
			.has-unread{
				font-weight:bold;
			}

			.unreadNumber{
				padding: 1px 5px;
				font-size: 11px;
				background: var(--app-secondary-color);
				color: var(--app-text-color);
				border-radius: 10px;
			}
			paper-listbox{
				background:transparent;
				padding: 0;
			}

			paper-item{
				background:transparent;
				outline:0;
				--paper-item-selected:{

				};

				--paper-item-disabled-color:{
					color: red;
				};

				--paper-item-focused: {
					background:transparent;
				};
				--paper-item-focused-before: {
					background:transparent;
				};

			}

			paper-listbox {
				outline:0;
				--paper-listbox-selected-item: {
					color:var(--app-text-color-light);
					background:var(--app-primary-color);
				};
				--paper-listbox-disabled-color:{
					color: red;
				};
			}

			#adminFileMenu paper-item.iron-selected {
				color:var(--app-text-color-light);
				background:var(--app-primary-color);
				@apply --text-shadow;
			}

			collapse-button {
				outline:0;
				width: 100%;
				--paper-listbox-selected-item: {
					color:var(--app-text-color-light);
					background:var(--app-primary-color);
				}
			}

			collapse-button > .menu-item.iron-selected {
				@apply --padding-right-left-16;
				color:var(--app-text-color-light);
				background:var(--app-primary-color);
				@apply --text-shadow;
			}

			paper-item.opened{
				padding-top: 8px;

			}
			.opened{
				color:var(--app-text-color);
				background:var(--app-text-color-light);
				border-radius:2px 2px 0 0;
				box-shadow: 0 4px 0 0 white,
							0 -2px 0 0 white,
							0 2px 2px 0 rgba(0, 0, 0, 0.14),
							0 1px 5px 0 rgba(0, 0, 0, 0.12),
							0 3px 1px -2px rgba(0, 0, 0, 0.2);

			}

			.opened.iron-selected{
				box-shadow: 0 4px 0 0 white,
							0 -2px 0 0 var(--app-primary-color),
							0 2px 2px 0 rgba(0, 0, 0, 0.14),
							0 1px 5px 0 rgba(0, 0, 0, 0.12),
							0 3px 1px -2px rgba(0, 0, 0, 0.2);
			}

						.sublist{
				background:var(--app-light-color);
				margin:0 0 8px -30px;
				padding:0;
				padding-bottom:4px;
				border-radius:0 0 2px 2px;
				@apply --shadow-elevation-2dp;
			}




			paper-item.list-info {
				font-weight: lighter;
				font-style: italic;
				height:48px;
			}

			.menu-item{
				@apply --padding-right-left-16;
				height:48px;
				@apply --paper-font-button;
				text-transform: inherit;
				justify-content: space-between;
				cursor: pointer;
				@apply --transition;
			}

			.sublist .menu-item {
				font-size: 13px;
				min-height:32px;
				height:32px;
			}

			.menu-item:hover{
				/*background: var(--app-dark-color-faded);*/
				@apply --transition;
			}

			.menu-item .iron-selected{
				background:var(--app-primary-color);

			}

			.menu-item .opened{
				background:white!important;
				width:80%;
				border-radius:2px;
			}

			.menu-item-icon--selected{
				width:0;
			}

			.opened .menu-item-icon--selected{
				width: 18px;
			}

			.opened > .menu-item-icon{
				transform: scaleY(-1);
			}

			paper-item.menu-item.opened {
				@apply --padding-right-left-16;
			}

			.submenu-item{
				cursor:pointer;
			}

			.submenu-item.iron-selected{
				background:var(--app-primary-color-light);
				color:var(--app-text-color-light);
				@apply --text-shadow;
			}

			.submenu-item-icon{
				height:14px;
				width:14px;
				color:var(--app-text-color-light);
				margin-right:10px;
			}

			vaadin-grid.material {
				outline: 0!important;
				font-family: Roboto, sans-serif;
				background:rgba(0,0,0,0);
				border:none;
				--divider-color: rgba(0, 0, 0, var(--dark-divider-opacity));

				--vaadin-grid-cell: {
					padding: 8px;
				};

				--vaadin-grid-header-cell: {
					height: 48px;
					padding:11.2px;
					color: rgba(0, 0, 0, var(--dark-secondary-opacity));
					font-size: 12px;
					background:rgba(0,0,0,0);
					border-top:0;
				};

				--vaadin-grid-body-cell: {
					height: 48px;
					color: rgba(0, 0, 0, var(--dark-primary-opacity));
					font-size: 13px;
				};

				--vaadin-grid-body-row-hover-cell: {
					background-color: var(--paper-grey-200);
				};

				--vaadin-grid-body-row-selected-cell: {
					background-color: var(--paper-grey-100);
				};

				--vaadin-grid-focused-cell: {
					box-shadow: none;
					font-weight: bold;
				};

			}
			vaadin-grid.material .cell {
				overflow: hidden;
				text-overflow: ellipsis;
				padding-right: 56px;
			}

			vaadin-grid.material .cell.last {
				padding-right: 24px;
				text-al;
			}

			vaadin-grid.material .cell.numeric {
				text-align: right;
			}

			vaadin-grid.material paper-checkbox {
				--primary-color: var(--paper-indigo-500);
				margin: 0 24px;
			}

			vaadin-grid.material vaadin-grid-sorter {
				--vaadin-grid-sorter-arrow: {
					display: none !important;
				};
			}

			vaadin-grid.material vaadin-grid-sorter .cell {
				flex: 1;
				display: flex;
				justify-content: space-between;
				align-items: center;
			}

			vaadin-grid.material vaadin-grid-sorter iron-icon {
				transform: scale(0.8);
			}

			vaadin-grid.material vaadin-grid-sorter:not([direction]) iron-icon {
				color: rgba(0, 0, 0, var(--dark-disabled-opacity));
			}

			vaadin-grid.material vaadin-grid-sorter[direction] {
				color: rgba(0, 0, 0, var(--dark-primary-opacity));
			}

			vaadin-grid.material vaadin-grid-sorter[direction=desc] iron-icon {
				transform: scale(0.8) rotate(180deg);
			}


			vaadin-grid.material::slotted(div){
				outline:0 !important;
			}

			paper-checkbox{
				--paper-checkbox-unchecked-color: var(--app-text-color);
				--paper-checkbox-unchecked-ink-color: var(--app-secondary-color);
				--paper-checkbox-checkmark-color: var(--app-secondary-color);
				--paper-checkbox-checked-color: var(--app-primary-color);
			}

			.trash{
				align-self: flex-start;
				width:100%;
			}

			iron-icon{
				padding:4px;
				color: var(--app-text-color-disabled);
			}

			paper-item{
				cursor: pointer;
			}
		</style>
		<div class="col-left">
			<paper-button class="new-msg-btn">[[localize('new_mes','New Message',language)]]</paper-button>
			<collapse-button>
				<paper-item slot="sublist-collapse-item" class="menu-trigger menu-item" on-tap="_inbox" elevation="">
					<div class="one-line-menu list-title"><iron-icon icon="icons:inbox"></iron-icon>[[localize('inb','Inbox',language)]] <template is="dom-if" if="[[unreadNumber]]"><span class="unreadNumber">[[unreadNumber]]</span></template></div>
					<paper-icon-button class="menu-item-icon" icon="hardware:keyboard-arrow-down" hover="none"></paper-icon-button>
				</paper-item>
				<paper-listbox class="menu-content sublist" multi="" toggle-shift="">
						<paper-item on-tap="_ehe">[[localize('ehe','Ehealth',language)]]</paper-item>
						<paper-item on-tap="_prot">[[localize('prot','Protocol',language)]]</paper-item>
						<paper-item on-tap="_labRes">[[localize('lab_res','Lab Results',language)]]</paper-item>
						<paper-item on-tap="_reportIn">[[localize('rep','Report',language)]]</paper-item>
						<paper-item on-tap="_e_invIn">[[localize('e_inv','E-Invoicing',language)]]</paper-item>
				</paper-listbox>
			</collapse-button>
			<collapse-button>
				<paper-item slot="sublist-collapse-item" class="menu-trigger menu-item" on-tap="_sentbox" elevation="">
					<div class="one-line-menu list-title"><iron-icon icon="icons:send"></iron-icon>[[localize('sen_mes','Send Messages',language)]] <template is="dom-if" if="[[sendNumber]]"><span class="unreadNumber">[[sendNumber]]</span></template></div>
					<paper-icon-button class="menu-item-icon" icon="hardware:keyboard-arrow-down" hover="none"></paper-icon-button>
				</paper-item>
				<paper-listbox class="menu-content sublist" multi="" toggle-shift="">
						<paper-item on-tap="_report">[[localize('rep','Report',language)]]</paper-item>
						<paper-item on-tap="_e_inv">[[localize('e_inv','E-Invoicing',language)]]</paper-item>
						<paper-item on-tap="_ehealthMessages">[[localize('ehe_mes','Ehealth Messages',language)]]</paper-item>
				</paper-listbox>
			</collapse-button>
			<paper-item class="trash"><iron-icon icon="icons:delete"></iron-icon>[[localize('tra','Trash',language)]]</paper-item>
		</div>
`;
  }

  static get is() {
      return 'ht-msg-menu';
	}

  static get properties() {
      return {
          api: {
              type: Object
          },
          user: {
              type: Object
          },
          events: {
              type: Array,
              value: function () {
                  return [];
              }

          },
          selectedLocalize: {
              type: Array,
              value: function () {
                  return [];
              }
          },
          selected: {
              type: Boolean,
              value: false
          },
          showFiltersPanel: {
              type: Boolean,
              value: false
          },
          msgSearchString: {
              type: String,
              value: null
          },
          showDetailsFiltersPanel: {
              type: Boolean,
              value: false
          },
          isLatestYear: {
              type: Boolean,
              value: false
          },
          selectedContactItems: {
              type: Array,
              value: function () {
                  return [];
              }
          },
          itemSelected: {
              type: Boolean,
              value: false
          },
          i18n: {
              type: Object
          },
          unreadNumber: {
              type: Number
          }
      };
	}

  static get observers() {
      return [];
	}

  constructor() {
      super();
	}

  ready() {
      super.ready();
      this.api.message().findMessagesByToAddress('INBOX', null, null, 1000).then(messages => {
          const msg = messages.rows.reduce((acc, m) => {
              if (!(m.status && 1 << 1 == 0)) {//status unread
                  acc.push(m)
              }return acc}, [])
          return msg.length;
      }).then(num => this.set('unreadNumber', num || 0));
  }

  _inbox(){
      this.dispatchEvent(new CustomEvent('selection-change', { detail: { selection: { item: 'inbox' } } }));
	}

  _sentbox(){
      this.dispatchEvent(new CustomEvent('selection-change', { detail: { selection: { item: 'sentbox' } } }));
  }

  _labRes(){
      this.dispatchEvent(new CustomEvent('selection-change', { detail: { selection: { item: 'labResult' } } }));
  }
  _report(){
      this.dispatchEvent(new CustomEvent('selection-change', { detail: { selection: { item: 'reportOut' } } }));
  }

  _e_inv(){
      this.dispatchEvent(new CustomEvent('selection-change', { detail: { selection: { item: 'e_invOut' } } }));
  }

  _reportIn(){
      this.dispatchEvent(new CustomEvent('selection-change', { detail: { selection: { item: 'reportIn' } } }));
  }

  _e_invIn(){
      this.dispatchEvent(new CustomEvent('selection-change', { detail: { selection: { item: 'e_invIn' } } }));
  }

  _ehealthMessages(){
      this.dispatchEvent(new CustomEvent('selection-change', { detail: { selection: { item: 'ehealthMessages' } } }));
  }
  _ehe(){
      this.dispatchEvent(new CustomEvent('selection-change', { detail: { selection: { item: 'eheIn' } } }));
  }

  _prot(){
      this.dispatchEvent(new CustomEvent('selection-change', { detail: { selection: { item: 'protIn' } } }));
  }

  _unread(){
      this.dispatchEvent(new CustomEvent('selection-change', { detail: { selection: { item: 'unread' } } }));
  }

  _submit(){
      this.dispatchEvent(new CustomEvent('selection-change', { detail: { selection: { item: 'submit' } } }));
  }

  _eheOut(){
      this.dispatchEvent(new CustomEvent('selection-change', { detail: { selection: { item: 'eheOut' } } }));
  }

  _protOut(){
      this.dispatchEvent(new CustomEvent('selection-change', { detail: { selection: { item: 'protOut' } } }));
  }

  _EFactBatch(){
      this.dispatchEvent(new CustomEvent('selection-change', { detail: { selection: { item: 'EFactBatch' } } }));
  }

  _GMD(){
      this.dispatchEvent(new CustomEvent('selection-change', { detail: { selection: { item: 'GMD' } } }));
  }


  selectedContactItemsChanged() {
      const ctcDetailPanel = this.shadowRoot.querySelector('#ctcDetailPanel');
      ctcDetailPanel && ctcDetailPanel.flushSave();

      this.set('selectedContacts', this.selectedContactItems.map(i => i.contact));
	}

  _timeFormat(date) {
      return this.api.moment(date).format(date > 99991231 ? 'DD/MM/YYYY HH:mm' : 'DD/MM/YYYY');
	}

  _dateFormat(date) {
      return this.api.moment(date).format('DD/MM/YYYY');
	}

  _selectToday() {
      this.$.adminFileMenu.select(1);

      this.set('timeSpanStart', parseInt(moment().startOf('day').format('YYYYMMDD')));
      this.set('timeSpanEnd', null);

      this.updateContactYears();
	}

  _select6Months() {
      this.set('timeSpanStart', parseInt(moment().subtract(6, 'month').format('YYYYMMDD')));
      this.set('timeSpanEnd', null);

      this.updateContactYears();
	}

  _selectAll() {
      this.set('timeSpanStart', null);
      this.set('timeSpanEnd', null);

      this.updateContactYears();
	}

  updateContactYears() {
      this.notifyPath('contactYears');
	}

  contactFilter() {
      return function (ctc) {
          const regExp = this.contactSearchString && new RegExp(this.contactSearchString, "i");

          const heIds = this.selectedMainElements.map(he => he.id);
          const poaIds = _.flatMap(this.selectedMainElements, he => he.selectedPlansOfAction ? he.selectedPlansOfAction.map(p => p.id) : []);
          const svcIds = this.selectedMainElements.filter(he => !he.id).map(he => he.idService);

          return this.api.after(ctc.openingDate, this.timeSpanStart) && this.api.before(ctc.openingDate, this.timeSpanEnd) && (!regExp || ctc.subContacts.filter(sc => sc.descr && sc.descr.match(regExp) && sc.services.length).length || ctc.services.filter(s => this.shortServiceDescription(s, this.language).match(regExp)).length) && (!heIds.length && !poaIds.length && !svcIds.length || ctc.subContacts.filter(sc => (sc.healthElementId && heIds.includes(sc.healthElementId) || sc.planOfActionId && poaIds.includes(sc.planOfActionId)) && sc.services.length).length || ctc.services.filter(s => svcIds.includes(s.id)).length) || !ctc.closingDate;
      }.bind(this);
	}

  isNotEmpty(a) {
      return a && a.length > 0;
	}

  isEmpty(a) {
      return !a || a.length === 0;
	}

  isAdminSelected(el) {
      return el && el.id === '_admin_info';
	}

  _concat(a, b) {
      return (a || []).concat(b || []);
	}

  selectedMainElementItemsChanged(event) {
      const domRepeat = event.target.querySelector("dom-repeat");
      const selectedModels = event.target.selectedItems.map(el => domRepeat.modelForElement(el).he);

      if (!domRepeat || !selectedModels) {
          return;
      }
      const allModels = domRepeat.items || [];

      this.set('selectedMainElements', this.selectedMainElements.filter(he => !allModels.includes(he)).concat(selectedModels));
  }
}

customElements.define(HtMsgMenu.is, HtMsgMenu);
