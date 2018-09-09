import '../filter-panel/filter-panel.js';
import '../dynamic-form/dynamically-loaded-form.js';
import '../dynamic-form/dynamic-doc.js';
import '../dynamic-form/entity-selector.js';

import jsPDF from 'jspdf/dist/jspdf.min';
import _ from 'lodash/lodash';
import JsBarcode from 'JsBarcode';
import moment from 'moment/src/moment';

class HtMsgDetail extends Polymer.TkLocalizerMixin(Polymer.Element) {
  static get template() {
    return Polymer.html`
		<style>
			.notification-panel {
				position: fixed;
				top:50%;
				right: 0;
				z-index:1000;
				color: white;
				font-size: 13px;
				background: rgba(255, 0, 0, 0.55);
				height: 96px;
				padding: 0 8px 0 12px;
				border-radius: 3px 0 0 3px;
				overflow: hidden;
				white-space: nowrap;
				width: 0;
				opacity: 0;
			}

			.notification {
				animation: notificationAnim 7.5s ease-in;
			}

			@keyframes notificationAnim {
				0%{
					width: 0;
					opacity: 0;
				}
				5%{
					width: 440px;
					opacity: 1;
				}
				7%{
					width: 420px;
					opacity: 1;
				}
				95%{
					width: 420px;
					opacity: 1;
				}
				100%{
					width: 0;
					opacity: 0;
				}
			}

			.prescription-progress-bar {
				width: calc( 100% - 40px );
			}

			.details-panel {
				box-sizing: border-box;
				grid-column: 3 / span 1;
				grid-row: 1 / span 1;
				background:var(--app-background-color-light);
				float:left;
				padding:20px;
				display:flex;
				flex-flow: column nowrap;
				align-items: flex-start;
				z-index:0;
				height: 100%;
				width: 100%;
			}

			.contact-title{
				display:block;
				@apply --paper-font-body2;
				@apply --padding-32;
				padding-bottom:8px;
				padding-top: 32px;
			}
			/*.contact-title:first-child{
				padding-top:0;
			}*/
			.pat-details-card > .card-content {
				padding: 16px 16px 32px !important;
			}

			.pat-details-card {
				width: calc(100% - 64px);
				margin: 0 32px;
			}

			.horizontal {
				display: flex;
				flex-direction: row;
				flex-wrap: wrap;
				flex-basis: 100%;
			}

			.justified {
				justify-content: space-between;
			}

			.pat-details-input {
				flex-grow: 1;
				margin: 16px;
			}

			input {
				border: none;
				width: 100%;
			}

			paper-dialog {
				margin: 0;
			}

			.contact-card-container {
				position: relative;
				overflow-y: auto;
				height: calc(100% - 48px);
				padding-bottom: 32px;
			}

			paper-dialog {
				min-width:30%;
			}

			.extra-info{
				color:var(--app-text-color-disabled);
				font-style: italic;
				font-size: 80%;
			}

			vaadin-upload{
				margin:16px;
				min-height:280px;
				background: var(--app-background-color);
				--vaadin-upload-buttons-primary: {
					padding:16px;
				};
				--vaadin-upload-button-add: {
					background: var(--app-secondary-color);
					color:var(--app-text-color);
				};
				--vaadin-upload-file-progress: {
					--paper-progress-active-color:var(--app-secondary-color);
				};
				--vaadin-upload-file-commands: {
					color: var(--app-primary-color);
				}

			}

			.close-button-icon{
				position: absolute;
				top: 0;
				right: 0;
				margin: 0;
				transform: translate(50%, -50%);
				height: 32px;
				width: 32px;
				padding: 8px;
				background: var(--app-primary-color);
			}

			paper-dialog {
				width: 80%;
			}

			vaadin-grid {
				height:100%;
				--vaadin-grid-body-row-hover-cell: {
					/* background-color: var(--app-primary-color); */
					color: white;
				};
				--vaadin-grid-body-row-selected-cell: {
					background-color: var(--app-primary-color);
					color: white;
				};
			}

			paper-input{
				--paper-input-container-focus-color: var(--app-primary-color);
			}

			.modal-title{
				background:  var(--app-background-color-dark);
				margin-top: 0;
				padding: 16px 24px;
			}

			.modal-button{
				--paper-button-ink-color: var(--app-secondary-color-dark);
				color: var(--app-text-color);
				font-weight: 400;
				font-size: 14px;
				height: 40px;
				min-width: 100px;
				padding: 10px 1.2em;
			}

			.modal-button--save{
				box-shadow: var(--shadow-elevation-2dp_-_box-shadow);
				background: var(--app-secondary-color);
				color: var(--app-primary-color-dark);
				font-weight: 700;
			}

			filter-panel{
				flex-grow: 9;
				/* --panel-width: 60%; */
			}

			.layout-bar{
				flex-grow: 1;
				display: inline-flex;
				flex-flow: row nowrap;
				align-items: center;
				justify-content: space-around;
				height:48px;
				background: var(--app-secondary-color);
				border-left: 1px solid var(--app-secondary-color-dark);
			}

			.layout-bar .list, .layout-bar .graphique, .layout-bar .doc{
				height: 32px;
				width: 32px;
				padding: 5px;
				color: var(--app-primary-color-dark);
			}

			.layout-bar .table{
				height: 30px;
				width: 30px;
				padding: 0;
				color: var(--app-primary-color-dark);
			}

			.floating-action-bar{
				display: flex;
				position: absolute;
				height: 40px;
				bottom: 16px;
				background: var(--app-secondary-color);
				border-radius: 3px;
				grid-column: 3/3;
				grid-row: 1/1;
				z-index: 1000;
				left: 50%;
				transform: translate(-50%, 0);
				box-shadow: var(--app-shadow-elevation-2);
			}

			.add-forms-container {
				position: absolute;
				bottom: 48px;
				left: 0;
				background-color: var(--app-background-color-light);
				opacity: .8;
				padding: 8px 0;
				border-radius: 2px;
				max-width: 253px;
			}
			.floating-action-bar paper-fab-speed-dial-action {
				--paper-fab-speed-dial-action-label-background: transparent;
				--paper-fab-iron-icon: {
					transform: scale(0.8);
					
				};
				--paper-fab: {
					background: var(--app-primary-color-dark);
				}
			}

			.floating-action-bar paper-button{
				--paper-button-ink-color: var(--app-secondary-color-dark);
				background: var(--app-secondary-color);
				color: var(--app-text-color);
				font-weight: bold;
				font-size: 12px;
				height: 40px;
				min-width: 130px;
				padding: 10px 1.2em;
				border-radius: 0;
				margin:0;
			}

			.floating-action-bar paper-button:hover{
				background: var(--app-dark-color-faded);
    			transition: var(--transition_-_transition);
			}

			.floating-action-bar paper-button:not(:first-child){
				border-left: 1px solid var(--app-secondary-color-dark);
			}

			.close-add-forms-btn{
				background: var(--app-secondary-color-dark) !important;
			}

			.floating-action-bar iron-icon{
				box-sizing: border-box;
				padding: 2px;
				margin-right: 8px;
			}

			.horizontal{
				flex-flow: row nowrap;
			}

			.contact-card-container {
				position: relative;
				overflow-y: auto;
				height: calc(100% - 48px);
				padding-bottom: 32px;
			}
		</style>
		<template is="dom-if" if="[[messages]]">
		<div class="details-panel" on-dragover="_onDrag">
			<div class="contact-card-container">
				<template is="dom-repeat" items="[[messages]]" as="msg">
				<div>From : [[msg.fromAddress]]</div>
				<div>To : [[msg.toAddresses]]</div>
				<div>Subject: [[msg.subject]]</div>
				<div>Body: 
					<template is="dom-repeat" items="[[msg.body]]" as="text">[[text]]</template>
				</div>
				</template>
			</div>
		</div>

	</template>
`;
  }

  static get is() {
      return 'ht-msg-detail';
	}

  static get properties() {
      return {
          contacts: {
              type: Array,
              value: function () {
                  return [];
              }
          },
          api: {
              type: Object
          },
          user: {
              type: Object
          },
          currentContact: {
              type: Object,
              value: null
          },
          selectMessage: {
              type: Object
          },
          messages: {
              type: Object
          }
      };
	}

  static get observers() {
      return ['_focusChanged(selectMessage, selectMessage.*)'];
	}

  constructor() {
      super();
	}

  _focusChanged(){
	    if(this.selectMessage.selection.item){
          this.selectMessage.selection.item.body = [];
	        let tempSelect = this.selectMessage.selection.item;
        	this.api.document().findByMessage(this.user.healthcarePartyId, tempSelect).then(docs => {
              docs.length ?
        	    docs.map(doc => this.api.document().getAttachment(doc.id,doc.attachmentId, doc.secretForeignKeys).then(a=> {
                  console.log("on a un attachment !" + typeof a)
                  tempSelect.body.push(a.toString())
                  console.log(tempSelect.body)
                  this.set('messages', [tempSelect]);
              }))
                  : this.set('messages', [tempSelect]);
              this.getChildren(tempSelect.id)
          })
	    }
	}

  getChildren(item){
      this.api.message().getChildren(item).then(function(m) {
          if(m.length){
              m.map(r => r.body = []);
              this.api.document().findByMessage(this.user.healthcarePartyId, m).then(docs => {
                  docs.length?
                    	docs.map(doc => this.api.document().getAttachment(doc.id,doc.attachmentId, doc.secretForeignKeys).then(a => {m.map(r => {r.body.push(a);this.messages.push(r)});}))
                      :m.map(r => this.messages.push(r));
                  this.set('messages',this.messages)
                  m.map(r => this.getChildren(r));
              })
          }
      }.bind(this))
	}
}

customElements.define(HtMsgDetail.is, HtMsgDetail);
