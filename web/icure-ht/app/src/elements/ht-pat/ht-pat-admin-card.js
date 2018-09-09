/**
@license
Copyright (c) 2016 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/
import '../dynamic-form/dynamic-form.js';

class HtPatAdminCard extends Polymer.TkLocalizerMixin(Polymer.Element) {
  static get template() {
    return Polymer.html`
		<style include="iron-flex iron-flex-alignment"></style>
		<style>
			:host {
				height: 100%;
			}

			.container {
				width: 100%;
				height: 100%;
			}

            paper-tabs {
                background: var(--app-secondary-color);
                --paper-tabs-selection-bar-color: var(--app-text-color-disabled);
                --paper-tabs: {
                    color: var(--app-text-color);
                };
            }

            paper-tab {
                --paper-tab-ink: var(--app-text-color);
            }

            paper-tab.iron-selected {
                font-weight: bold;
            }

            paper-tab.iron-selected iron-icon{
                opacity: 1;
            }

            paper-tab iron-icon{
                opacity: 0.5;
                color: var(--app-text-color);
            }



			paper-material.card {
				background-color: #fff;
				padding: 10px;
				margin-left: 5px;
				margin-right: 5px;
				margin-bottom: 10px;
			}

			paper-input {
				padding-left: 5px;
				padding-right: 5px;
			}

            paper-input {
                --paper-input-container-focus-color: var(--app-primary-color);
                --paper-input-container-label: {
                    color: var(--app-text-color);
                    opacity: 1;
                };
                --paper-input-container-underline-disabled: {
                    border-bottom: 1px solid var(--app-text-color);

                };
                --paper-input-container-color: var(--app-text-color);
            }

			paper-dropdown-menu {
				padding-left: 5px;
				padding-right: 5px;
			}

            iron-pages {
                padding: 32px 0 0;
                width: 100%;
                height: calc(100% - 140px);
                box-sizing: border-box;
            }

            page>dynamic-form{
                width: 100%;
            }




            :host #institution-list {
                height: calc(100% - 140px);
                outline: none;
            }

            #institution-list{
                width: 98%;
                padding: 5px;
                height: calc(100% - 140px);
            }

            .grid-institution{
                width: 100%;
                padding: 5px;
                height: calc(100% - 20px);
            }

            vaadin-grid.material {
                box-shadow: var(--app-shadow-elevation-1);
                border: 0;
                font-family: Roboto, sans-serif;
                --divider-color: rgba(0, 0, 0, var(--dark-divider-opacity));
                margin: 0 32px 32px;

               
                   

                --vaadin-grid-cell: {
                    padding: 8px;
                };

                --vaadin-grid-header-cell: {
                    height: 64px;
                    color: rgba(0, 0, 0, var(--dark-secondary-opacity));
                    font-size: 12px;
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
            }

            vaadin-grid.material .cell.numeric {
                text-align: right;
            }

            vaadin-grid.material paper-checkbox {
                --primary-color: var(--paper-indigo-500);
                margin: 0 24px;
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

            paper-dialog paper-input{
                padding: 0;
            }

            paper-dialog > div {
                margin-top: 0;
            }

            paper-dialog vaadin-grid.material{
                margin:24px 0 0;
            }

            .buttons{
                position: absolute;
                bottom: 0;
                width: 100%;
                box-sizing: border-box;
                padding: 8px 24px;
                flex-flow: row wrap;
                justify-content: space-between;
            }

            .buttons paper-checkbox{
                align-self: center;
            }

            #dialogAddInstitution{
                height: 400px;
                width: 600px;
            }

            .formAddStay{
                width: 100%;
                border-collapse: collapse;
            }

            .full-width{
                width: 100%;
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

            .administrative-panel {
                background: var(--app-background-color);
                margin: 0;
                grid-column: 2 / 4;
                grid-row: 1 / 1;
            }

             .modal-button--extra {
                padding-left: 0;
                padding-right: 0;
                margin: 0;
             }

            .add-btn-stay-container{
                display:table;
                margin: auto;
            }

            .btn{
                --paper-button-ink-color: var(--app-secondary-color-dark);
                background: var(--app-secondary-color);
                color: var(--app-text-color);
                font-weight: 700;
                font-size: 12px;
                height: 40px;
                min-width: 100px;
                box-shadow: var(--shadow-elevation-2dp_-_box-shadow);
                padding: 10px 1.2em;
            }

            #institutionComment{
                height: 140px;
            }

            #add-person-to-care-team{
                min-height: 554px;
                max-height: 50%;
                min-width: 800px;
                max-width: 60%;
            }

            #add-new-person-to-care-team{
                height: 520px;
                width: 500px;
            }

            #internal-care-team-list, #external-care-team-list, #dmg-owner-list{
                max-height: 50%;
                height: auto;
            }

            #showHcpInfo {
                min-height: 520px;
                max-height: 50%;
                min-width: 500px;
                max-width: 60%;
            }

            .hcpInfo{
                max-height: calc(520px - 56px);
                overflow: auto;  
                padding: 0;             
            }

            .iconHcpInfo{
                height: 18px;
            }

            .indent{
                margin-bottom: 12px;
            }

            .indent paper-input{
                margin: 0 24px;
            }

            .titleHcpInfo{
                height: 48px;
                width: calc(100% - 48px);
                color: var(--app-text-color);
                background-color: var(--app-background-color-dark);
                padding: 0 24px;
                font-weight: bold;
                display:flex;
                flex-flow: row wrap;
                justify-content: flex-start;
                align-items: center;
            }
            

            .titleHcpInfo_Icon{
                height: 24px;
                width: 24px;
                opacity: 0.5;
            }

            .titleHcpInfo_Txt{
                padding-left: 8px;
            }

            .label{
                font-weight: bold;
            }

            .hcpAdr{
                margin-bottom: 10px;
            }

            .button_add_provider{
                width:75%;
                position: fixed;
                bottom: 16px;
                display: flex;
                flex-direction: row;
                justify-content: center;
                align-items: center;
            }

            .add-btn{
                --paper-button-ink-color: var(--app-secondary-color-dark);
                background:var(--app-secondary-color);
                color:var(--app-text-color);
                font-weight:bold;
                font-size:12px;
                height:40px;
                min-width:100px;
                @apply --shadow-elevation-2dp;
                padding: 10px 1.2em;
            }

            

            iron-icon.smaller {
                padding-right: 8px;
                width: 16px;
                height: 16px;
            }

            .subtitle{
                display:block;
				@apply --paper-font-body2;
				@apply --padding-32;
                padding-top: 0;
				padding-bottom: 8px;
                margin: 0;
            }

            .modal-title{
                background: var(--app-background-color-dark);
                margin-top: 0;
                padding: 16px 24px;
            }

        </style>
        <div class="administrative-panel">
            <paper-tabs selected="{{tabs}}">
                <paper-tab class="adm-tab"><iron-icon class="smaller" icon="vaadin:clipboard-text"></iron-icon>[[localize('adm_form','Administrative form',language)]]</paper-tab>
                <paper-tab class="adm-tab"><iron-icon class="smaller" icon="vaadin:family"></iron-icon>[[localize('adm_ctc_per','Contact persons',language)]]</paper-tab>
                <paper-tab class="adm-tab"><iron-icon class="smaller" icon="vaadin:doctor"></iron-icon>[[localize('adm_h_t','Care team',language)]]</paper-tab>
            </paper-tabs>

            <iron-pages selected="[[tabs]]" class="">
                <page>
                    <dynamic-form id="dynamic-form-administrative" api="[[api]]" user="[[user]]" template="[[patientForm]]" data-map="[[patientMap]]" data-provider="[[dataProvider]]" i18n="[[i18n]]" resources="[[resources]]" language="[[language]]"></dynamic-form>
                </page>
                <page>
                    <dynamic-form id="dynamic-form-partnerships" api="[[api]]" user="[[user]]" template="[[partnershipsContainerForm]]" data-map="[[patientMap]]" data-provider="[[dataProvider]]" i18n="[[i18n]]" resources="[[resources]]" language="[[language]]"></dynamic-form>
                </page>
                <page>
                    <div>
                        <h4 class="subtitle">[[localize('icp','Internal care provider',language)]]</h4>
                        <vaadin-grid id="internal-care-team-list" class="material" overflow="bottom" multi-sort="[[multiSort]]" items="[[currentInternalCareTeam]]" active-item="{{selectedCareProvider}}" on-tap="showInfoSelectedHcp">
                            <vaadin-grid-column width="120px">
                                <template class="header">
                                    <vaadin-grid-sorter path="lastName">[[localize('las_nam','Last name',language)]]</vaadin-grid-sorter>
                                </template>
                                <template>
                                    <div class="cell frozen">[[item.lastName]]</div>
                                </template>
                            </vaadin-grid-column>
                            <vaadin-grid-column width="120px">
                                <template class="header">
                                    <vaadin-grid-sorter path="firstName">[[localize('fir_nam','Last name',language)]]</vaadin-grid-sorter>
                                </template>
                                <template>
                                    <div class="cell frozen">[[item.firstName]]</div>
                                </template>
                            </vaadin-grid-column>
                            <vaadin-grid-column width="80px">
                                <template class="header">
                                    <vaadin-grid-sorter path="nihii">[[localize('inami','Nihii',language)]]</vaadin-grid-sorter>
                                </template>
                                <template>
                                    <div class="cell frozen">[[formatNihiiNumber(item.nihii)]]</div>
                                </template>
                            </vaadin-grid-column>
                            <vaadin-grid-column width="80px">
                                <template class="header">
                                    <vaadin-grid-sorter path="speciality">[[localize('speciality','Speciality',language)]]</vaadin-grid-sorter>
                                </template>
                                <template>
                                    <div class="cell frozen">[[item.speciality]]</div>
                                </template>
                            </vaadin-grid-column>
                        </vaadin-grid>
                    </div>
                    <div>
                        <h4 class="subtitle">[[localize('ecp','External care provider',language)]]</h4>
                        <vaadin-grid id="external-care-team-list" class="material" overflow="bottom" multi-sort="[[multiSort]]" items="[[currentExternalCareTeam]]" active-item="{{selectedCareProvider}}" on-tap="showInfoSelectedHcp">
                            <vaadin-grid-column width="120px">
                                <template class="header">
                                    <vaadin-grid-sorter path="lastName">[[localize('las_nam','Last name',language)]]</vaadin-grid-sorter>
                                </template>
                                <template>
                                    <div class="cell frozen">[[item.lastName]]</div>
                                </template>
                            </vaadin-grid-column>
                            <vaadin-grid-column width="120px">
                                <template class="header">
                                    <vaadin-grid-sorter path="firstName">[[localize('fir_nam','Last name',language)]]</vaadin-grid-sorter>
                                </template>
                                <template>
                                    <div class="cell frozen">[[item.firstName]]</div>
                                </template>
                            </vaadin-grid-column>
                            <vaadin-grid-column width="80px">
                                <template class="header">
                                    <vaadin-grid-sorter path="nihii">[[localize('inami','Nihii',language)]]</vaadin-grid-sorter>
                                </template>
                                <template>
                                    <div class="cell frozen">[[formatNihiiNumber(item.nihii)]]</div>
                                </template>
                            </vaadin-grid-column>
                            <vaadin-grid-column width="80px">
                                <template class="header">
                                    <vaadin-grid-sorter path="speciality">[[localize('speciality','Speciality',language)]]</vaadin-grid-sorter>
                                </template>
                                <template>
                                    <div class="cell frozen">[[item.speciality]]</div>
                                </template>
                            </vaadin-grid-column>
                        </vaadin-grid>
                    </div>
                    <div>
                        <h4 class="subtitle">[[localize('dmg-owner','DMG owner',language)]]</h4>
                        <vaadin-grid id="dmg-owner-list" class="material" overflow="bottom" multi-sort="[[multiSort]]" items="[[currentDMGOwner]]" active-item="{{selectedCareProvider}}" on-tap="showInfoSelectedHcp">
                            <vaadin-grid-column width="240px">
                                <template class="header">
                                    <vaadin-grid-sorter path="name">[[localize('nam','Name',language)]]</vaadin-grid-sorter>
                                </template>
                                <template>
                                    <div class="cell frozen">[[getHcpName(item)]]</div>
                                </template>
                            </vaadin-grid-column>
                            <vaadin-grid-column width="80px">
                                <template class="header">
                                    <vaadin-grid-sorter path="nihii">[[localize('inami','Nihii',language)]]</vaadin-grid-sorter>
                                </template>
                                <template>
                                    <div class="cell frozen">[[formatNihiiNumber(item.nihii)]]</div>
                                </template>
                            </vaadin-grid-column>
                            <vaadin-grid-column width="80px">
                                <template class="header">
                                    <vaadin-grid-sorter path="speciality">[[localize('speciality','Speciality',language)]]</vaadin-grid-sorter>
                                </template>
                                <template>
                                    <div class="cell frozen">[[item.speciality]]</div>
                                </template>
                            </vaadin-grid-column>
                            <vaadin-grid-column>
                                <template class="header">
                                    <vaadin-grid-sorter path="beginDate">[[localize('sta_dat','Start date',language)]]</vaadin-grid-sorter>
                                </template>
                                <template>
                                    <div class="cell frozen">[[getStartDate(item)]]</div>
                                </template>
                            </vaadin-grid-column>
                            <vaadin-grid-column>
                                <template class="header">
                                    <vaadin-grid-sorter path="endDate">[[localize('end_dat','End date',language)]]</vaadin-grid-sorter>
                                </template>
                                <template>
                                    <div class="cell frozen">[[getEndDate(item)]]</div>
                                </template>
                            </vaadin-grid-column>
                        </vaadin-grid>
                    </div>
                    <div class="button_add_provider">
                        <paper-button class="add-btn" on-tap="showAddPersonToCareTeam">[[localize('add_per','Add person',language)]]</paper-button>
                    </div>
                </page>
            </iron-pages>
        </div>

        <paper-dialog id="add-person-to-care-team">    
            <h2 class="modal-title">[[localize('add_per_to_car_tea','Add person',language)]]</h2>
            <div>
                <vaadin-grid id="hcp-list" class="material" overflow="bottom" items="[[currentHcp]]" active-item="{{selectedHcp}}">
                    <vaadin-grid-column width="100px">
                        <template class="header">
                        </template>
                        <template>
                            <vaadin-checkbox id="[[item.id]]" checked="[[_sharingHcp(item, currentHcp.*)]]" on-checked-changed="_checkHcp"></vaadin-checkbox>
                        </template>
                    </vaadin-grid-column>
                    <vaadin-grid-column width="120px">
                        <template class="header">
                            <vaadin-grid-sorter path="lastName">[[localize('las_nam','Last name',language)]]</vaadin-grid-sorter>
                        </template>
                        <template>
                            <div class="cell frozen">[[item.lastName]]</div>
                        </template>
                    </vaadin-grid-column>
                    <vaadin-grid-column width="120px">
                        <template class="header">
                            <vaadin-grid-sorter path="firstName">[[localize('fir_nam','First name',language)]]</vaadin-grid-sorter>
                        </template>
                        <template>
                            <div class="cell frozen">[[item.firstName]]</div>
                        </template>
                    </vaadin-grid-column>
                    <vaadin-grid-column width="80px">
                        <template class="header">
                            <vaadin-grid-sorter path="nihii">[[localize('inami','Nihii',language)]]</vaadin-grid-sorter>
                        </template>
                        <template>
                            <div class="cell frozen">[[formatNihiiNumber(item.nihii)]]</div>
                        </template>
                    </vaadin-grid-column>
                    <vaadin-grid-column width="80px">
                        <template class="header">
                            <vaadin-grid-sorter path="speciality">[[localize('speciality','Speciality',language)]]</vaadin-grid-sorter>
                        </template>
                        <template>
                            <div class="cell frozen">[[item.speciality]]</div>
                        </template>
                    </vaadin-grid-column>
                </vaadin-grid>
            </div>
            <div class="buttons">
                    <paper-button class="modal-button modal-button--extra" on-tap="showAddNewPersonToCareTeamForm"><iron-icon icon="icons:add"></iron-icon>[[localize('new_per','New person',language)]]</paper-button>
                <div>
                    <paper-button class="modal-button" dialog-dismiss="">[[localize('can','Cancel',language)]]</paper-button>
                    <paper-button class="modal-button--save" dialog-confirm="" autofocus="" on-tap="confirmSharing">[[localize('save','Save',language)]]</paper-button>
                </div>
            </div>
        </paper-dialog>

        <paper-dialog id="add-new-person-to-care-team">
            <h2 class="modal-title">[[localize('add_new_per_to_car_tea','Add new person to the care team',language)]]</h2>
            <div>
                <paper-input id="" label="[[localize('las_nam','Last name',language)]]" value="{{newHcpCareTeam.LastName}}"></paper-input>
                <paper-input id="" label="[[localize('fir_nam','First name',language)]]" value="{{newHcpCareTeam.FirstName}}"></paper-input>
                <paper-input id="" label="[[localize('ema','Email',language)]]" value="{{newHcpCareTeam.Email}}"></paper-input>
                <paper-input id="" label="[[localize('inami','Nihii',language)]]" value="{{newHcpCareTeam.Nihii}}"></paper-input>
                <paper-input id="" label="[[localize('niss','Niss',language)]]" value="{{newHcpCareTeam.Niss}}"></paper-input>
                <paper-input id="" label="[[localize('speciality','Speciality',language)]]" value="{{newHcpCareTeam.Speciality}}"></paper-input>
            </div>
            <div class="buttons">
                <paper-checkbox value="{{newHcpCareTeam.Invite}}" on-change="chckInvite">Invite care provider</paper-checkbox>

                <div>
                    <paper-button class="modal-button" dialog-dismiss="">[[localize('can','Cancel',language)]]</paper-button>
                    <paper-button class="modal-button--save" dialog-confirm="" autofocus="" on-tap="addNewExternalPersonToCareTeam">[[localize('save','Save',language)]]</paper-button>
                </div>
            </div>
        </paper-dialog>


        <paper-dialog id="showHcpInfo">
            <div class="hcpInfo">
                <div>
                   <div class="titleHcpInfo">
                       <div class="titleHcpInfo_Icon">
                            <iron-icon icon="icons:perm-contact-calendar"></iron-icon>
                       </div>
                       <div class="titleHcpInfo_Txt">
                           [[localize('gen_info','General informations',language)]]
                       </div>
                   </div>
                   <div class="indent">
                       <paper-input id="" label="[[localize('las_nam','Last name',language)]]" value="[[selectedPerson.lastName]]" readonly=""></paper-input>
                       <paper-input id="" label="[[localize('fir_nam','First name',language)]]" value="[[selectedPerson.firstName]]" readonly=""></paper-input>
                       <paper-input id="" label="[[localize('name','Name',language)]]" value="[[selectedPerson.name]]" readonly=""></paper-input>
                       <paper-input id="" label="Type:" value="[[selectedPerson.hcpType]]" readonly=""></paper-input>
                       <paper-input id="" label="[[localize('inami','Nihii',language)]]" value="[[formatNihiiNumber(selectedPerson.nihii)]]" readonly=""></paper-input>
                       <paper-input id="" label="[[localize('ssin','Ssin',language)]]" value="[[formatNissNumber(selectedPerson.ssin)]]" readonly=""></paper-input>
                       <paper-input id="" label="[[localize('speciality','Speciality',language)]]" value="[[selectedPerson.speciality]]" readonly=""></paper-input>
                   </div>
                   <template is="dom-if" if="[[selectedPerson.addresses]]">
                        <div>
                            <div class="titleHcpInfo">
                                <div class="titleHcpInfo_Icon">
                                    <iron-icon class="iconHcpInfo" icon="icons:home"></iron-icon>
                                </div>
                                <div class="titleHcpInfo_Txt">
                                    [[localize('addresses','Addresses',language)]]
                                </div>
                            </div>
                            <div class="indent">
                                <template is="dom-repeat" items="[[selectedPerson.addresses]]" as="adr">
                                    <div class="hcpAdr">
                                        <paper-input id="" label="[[localize('street','Street',language)]]" value="[[adr.street]]" readonly=""></paper-input>
                                        <paper-input id="" label="[[localize('number','Number',language)]]" value="[[adr.number]]" readonly=""></paper-input>
                                        <paper-input id="" label="[[localize('postalCode','Postal code',language)]]" value="[[adr.postalCode]]" readonly=""></paper-input>
                                        <paper-input id="" label="[[localize('city','City',language)]]" value="[[adr.city]]" readonly=""></paper-input>
                                        <div class="titleHcpInfo">
                                            <div class="titleHcpInfo_Icon">
                                                <iron-icon class="iconHcpInfo" icon="communication:phone"></iron-icon>
                                            </div>
                                            <div class="titleHcpInfo_Txt">
                                                [[localize('telecom','Telecom',language)]]
                                            </div>
                                        </div>
                                        <template is="dom-repeat" items="[[adr.telecoms]]" as="telecom">
                                            <paper-input id="" label="[[localize('tel_type','Telecom type',language)]]" value="[[telecom.telecomType]]" readonly=""></paper-input>
                                            <paper-input id="" label="[[localize('tel_num','Telecom number',language)]]" value="[[telecom.telecomNumber]]" readonly=""></paper-input>
                                        </template>
                                    </div>
                                </template>
                            </div>
                        </div>
                   </template>
                </div>
                <template is="dom-if" if="[[selectedPerson.pphc.referralPeriods]]">
                    <div>
                        <div class="titleHcpInfo">
                            <div class="titleHcpInfo_Icon">
                                <iron-icon class="iconHcpInfo" icon="vaadin:clock"></iron-icon>
                            </div>
                            <div class="titleHcpInfo_Txt">
                                [[localize('ref_per','Referral periods',language)]]
                            </div>
                        </div>
                        <div class="indent">
                            <template is="dom-repeat" items="[[selectedPerson.pphc.referralPeriods]]" as="referralPeriod">
                                <paper-input id="" label="[[localize('startDate','Start date',language)]]" value="[[_timeFormat(referralPeriod.startDate)]]" readonly=""></paper-input>
                                <paper-input id="" label="[[localize('endDate','End date',language)]]" value="[[_timeFormat(referralPeriod.endDate)]]" readonly=""></paper-input>
                            </template>
                        </div>
                    </div>
                </template>
            </div>
            <div class="buttons" style="flex-flow: row-reverse wrap; padding: 8px 0;">
                <paper-button class="modal-button" dialog-dismiss="">[[localize('clo','Close',language)]]</paper-button>
            </div>
        </paper-dialog>

        <paper-dialog id="ht-invite-hcp-link">
            <h3>Lien de première connexion</h3>
            <h4>[[invitedHcpLink]]</h4>
        </paper-dialog>
`;
  }

  static get is() {
      return 'ht-pat-admin-card';
  }

  static get properties() {
      return {
          api: {
              type: Object
          },
          patientForm: {
              type: Object,
              value: function () {
                  return require('./rsrc/PatientAdministrativeForm.json');
              }
          },
          addressForm: {
              type: Object,
              value: function () {
                  return require('./rsrc/PatientAddressForm.json');
              }
          },
          telecomForm: {
              type: Object,
              value: function () {
                  return require('./rsrc/PatientTelecomForm.json');
              }
          },
          insuranceForm: {
              type: Object,
              value: function () {
                  return require('./rsrc/PatientInsuranceForm.json');
              }
          },
          partnershipsForm: {
              type: Object,
              value: function () {
                  return require('./rsrc/PatientPartnershipsForm.json');
              }
          },
          partnershipsContainerForm: {
              type: Object,
              value: function () {
                  return require('./rsrc/PatientPartnershipsContainerForm.json');
              }
          },
          user: {
              type: Object
          },
          patient: {
              type: Object,
              notify: true
          },
          patientMap: {
              type: Object
          },
          dataProvider: {
              type: Object,
              value: null
          },

          newHcpCareTeam: {
              type: Object,
              value: {
                  'LastName': '',
                  'FirstName': '',
                  'Nihii': '',
                  'Speciality': '',
                  'Email': '',
                  'Niss': '',
                  'Invite': false
              }
          },

          currentInternalCareTeam: {
              type: Array,
              value: [],
              notify: true
          },

          currentExternalCareTeam: {
              type: Array,
              value: [],
              notify: true
          },

          currentDMGOwner: {
              type: Array,
              value: [],
              notify: true
          },

          currentHcp: {
              type: Array,
              value: []
          },
          selectedCareProvider: {
              type: Object
          },
          selectedPerson: {
              type: Object
          },
          hcpSelectedForTeam: {
              type: Object,
              notify: true,
              value: () => []
          },
          invitedHcpLink: {
              type: String,
              value: ""
          },
          tabs: {
              type: Number,
              value: 0
          }
      };
  }

  static get observers() {
      return ['patientChanged(api,user,patient)'];
  }

  constructor() {
      super();
  }

  detached() {
      this.flushSave();
  }

  ready() {
      super.ready();
  }

  patientChanged() {
      if (this.api && this.user && this.patient) {
          this.flushSave();

          this.initCurrentCareTeam();

          if (this.patient.partnerships && this.patient.partnerships.length) {
              (this.patient.partnerships.length ? this.api.patient().getPatients({ids:this.patient.partnerships.map(ps => ps.partnerId)}) : Promise.resolve([])).then(ppss =>
                  ppss.map(pps => {
                      const index = this.patient.partnerships.findIndex(ps => ps.partnerId === pps.id)
                      this.set('patient.partnerships.' + index + '.partnerInfo', pps)
                  })
              ).finally(() => {
                  this.dataProvider = this.patientDataProvider(this.patient, '', this.patient && this.patient.id);
                  this.set('patientMap', _.cloneDeep(this.patient));

                  if (!this.root.activeElement) {
                      this.$['dynamic-form-administrative'].loadDataMap();
                  } else {
                      this.$[this.root.activeElement.id].loadDataMap();
                  }
              })
          } else {
              this.dataProvider = this.patientDataProvider(this.patient, '', this.patient && this.patient.id);
              this.set('patientMap', _.cloneDeep(this.patient));

              if (!this.root.activeElement) {
                  this.$['dynamic-form-administrative'].loadDataMap();
              } else {
                  this.$[this.root.activeElement.id].loadDataMap();
              }

          }
      }
  }


  scheduleSave(patient) {
      if (this.saveTimeout) {
          clearTimeout(this.saveTimeout);
      }

      this.saveAction = function () {


          if (this.patient.partnerships) {
              const partnerships = this.patient.partnerships

              partnerships.map(partner => {
                  const mark = partnerships.find(fp => fp.partnerId === partner.partnerId)
                  const index = partnerships.indexOf(partner)
                  ;(partner.partnerInfo || (partner.partnerInfo = {})).addresses = partner.addresses

                  if (!mark || !partner.partnerId) {
                      //patient not present
                      partner.partnerInfo.active = false
                      partner.partnerInfo.id = partner.id
                      partner.partnerId = partner.id

                      this.api.patient().newInstance(this.user, partner.partnerInfo).then(p => this.api.register(p,'patient')).then(np => {
                          this.api.patient().createPatient(np).then(p => this.api.register(p,'patient')).then(np => {
                              this.set('patient.partnerships.' + index + '.partnerInfo', np)
                          })
                      })
                  } else {
                      //patient present
                      this.api.patient().modifyPatient(partner.partnerInfo).then(p => this.api.register(p,'patient')).then(partner => {
                          this.set('patient.partnerships.' + index + '.partnerInfo', partner)
                      })

                  }
              })
          }

          this.api.patient().modifyPatient(patient).then(p => this.api.register(p,'patient')).then(p => {
              this.patient && (this.patient.rev = p.rev)
          }).catch(e =>
              this.patient && this.api.patient().getPatient(this.patient.id).then(p => {
                  this.patient = p;
                  this.saveTimeout = undefined;
                  this.saveAction = undefined;
              }));
      }.bind(this);
      this.saveTimeout = setTimeout(this.saveAction, 10000);
  }

  flushSave() {
      if (this.saveTimeout) {
          clearTimeout(this.saveTimeout);
          this.saveAction();
      }
  }

  patientDataProvider(root, rootPath, id) {


      const getValue = function (key) {
          return root ? _.get(root, key) : null;
      };
      const setValue = function (key, value) {
          if (root && _.get(root, key) !== value) {
              root === this.patient ? this.set('patient.' + key, value) : _.set(root, key, value);
              this.scheduleSave(this.patient);
          }
      }.bind(this);
      return {
          getStringValue: getValue,
          getNumberValue: getValue,
          getMeasureValue: getValue,
          getDateValue: getValue,
          getBooleanValue: key => root ? _.get(root, key) && _.get(root, key) !== 'false' : null,
          setStringValue: setValue,
          setNumberValue: setValue,
          setMeasureValue: setValue,
          setDateValue: setValue,
          setBooleanValue: setValue,
          getSubForms: key => {
              return (root[key] || []).map((a, idx) => {
                  return {
                      dataMap: a,
                      dataProvider: this.patientDataProvider(a, (rootPath.length ? rootPath + '.' : '') + key + '.' + idx, a.id || (a.id = this.api.crypto().randomUuid())),
                      template: key === 'addresses' ? this.addressForm : key === 'telecoms' ? this.telecomForm : key === 'partnerships' ? this.partnershipsForm : this.insuranceForm
                  };
              });
          },
          getId: () => id,
          deleteSubForm: (key, id) => {
              this.flushSave();
              _.remove(root[key], root[key].find(a => a.id === id));
              this.$[this.root.activeElement.id].notify((rootPath.length ? rootPath + '.' : '') + key + '.*');
              this.scheduleSave(this.patient);
          },
          addSubForm: (key, guid) => {
              this.flushSave(); //Important
              (root[key] || (root[key] = [])).push({});
              this.$[this.root.activeElement.id].notify((rootPath.length ? rootPath + '.' : '') + key + '.*');
              this.scheduleSave(this.patient);

          },
          filter: (data, text, id) => {
              if (data.source === 'insurances') {
                  return (text || '').length >= 2 ?
                      (text.match(/^[0-9]+$/) ? this.api.insurance().listInsurancesByCode(text) : this.api.insurance().listInsurancesByName(text))
                          .then(res => res.map(i => ({
                              'id': i.id,
                              'name': this.localizeContent(i.name, this.language)
                          }))) : id ? this.api.insurance().getInsurance(id)
                          .then(i => ({
                              'id': i.id,
                              'name': this.localizeContent(i.name, this.language)
                          })) : Promise.resolve([]);
              } else if (data.source === 'users') {
                  const s = text && text.toLowerCase()
                  return Promise.resolve(s ? Object.values(this.api.users).filter(u => (u.login && u.login.toLowerCase().includes(s.toLowerCase())) ||
                      (u.name && u.name.toLowerCase().includes(s.toLowerCase())) || (u.email && u.email.toLowerCase().includes(s.toLowerCase())))
                      .map(u => ({id: u.id, name: u.name || u.login || u.email})) : [])
              } else if (data.source === "codes" && data.types.length && (id || (text && text.length > 1))) {
                  return id ?
                      Promise.all(data.types.map(ct => this.api.code().getCodeWithParts(ct.type, id, '1')))
                          .then(x => _.compact(x)[0])
                          .then(c => {
                              const typeLng = this.api.code().languageForType(c.type, this.language)
                              return {id: c.code, name: c.label[typeLng]}
                          }) :
                      Promise.all(data.types.map(ct => {
                          const typeLng = this.api.code().languageForType(ct.type, this.language)
                          const words = text.toLowerCase().split(/\s+/)
                          const sorter = x => [x.name && x.name.toLowerCase().startsWith(words[0]) ? 0 : 1, x.name]

                          return this.api.code().findPaginatedCodesByLabel('be', ct.type, typeLng, words[0], null, null, 200).then(results => _.sortBy(results.rows.filter(c => c.label[typeLng] && words.every(w => c.label[typeLng].toLowerCase().includes(w))).map(code => ({
                              id: code.code, name: code.label[typeLng]
                          })), sorter))
                      })).then(responses => _.flatMap(responses))
              }
              return Promise.resolve(id ? null : [])
          }
      };
  }

  localizeContent(e, lng) {
      return this.api && this.api.contact().localize(e, lng) || "";
  }

  showAddPersonToCareTeam() {
      this.$['add-person-to-care-team'].open()
      this.set('currentHcp', _.values(this.api.hcParties))
  }

  showAddNewPersonToCareTeamForm() {
      this.$['add-person-to-care-team'].close()
      this.$['add-new-person-to-care-team'].open()
  }

  addNewExternalPersonToCareTeam() {
      const careProvider = this.newHcpCareTeam

      this.api.hcparty().createHealthcareParty({
          "name": careProvider.LastName + " " + careProvider.FirstName,
          "lastName": careProvider.LastName,
          "firstName": careProvider.FirstName,
          "nihii": careProvider.Nihii,
          "ssin": careProvider.Niss
      }).then(hcp => {
          this.api.user().createUser({
              "healthcarePartyId": hcp.id,
              "name": careProvider.LastName + " " + careProvider.FirstName,
              "email": careProvider.Email,
              "applicationTokens": {"tmpFirstLogin": this.api.crypto().randomUuid()},
              "status": "ACTIVE",
              "type": "database"
          }).then(usr => {
              this.api.patient().getPatient(this.patient.id).then(patient => {
                  var phcp = patient.patientHealthCareParties
                  var newPhcp = {}
                  newPhcp.healthcarePartyId = hcp.id
                  newPhcp.referral = false
                  newPhcp.sendFormats = {}
                  phcp.push(newPhcp)

                  this.api.patient().modifyPatient(patient).then(p => this.api.register(p,'patient')).then(() => {
                      this.set('patient.patientHealthCareParties', phcp)
                      this.$['add-new-person-to-care-team'].close()
                      this.initCurrentCareTeam()

                      if (careProvider.Invite === true) {
                          this.$['ht-invite-hcp-link'].open()
                          this.invitedHcpLink = window.location.origin + window.location.pathname + '/?userId=' + usr.id + '&token=' + usr.applicationTokens.tmpFirstLogin
                      }
                  })
              })
          })
      })
  }

  initCurrentCareTeam() {
      var internalTeam = []
      var externalTeam = []
      var dmgOwner = []

      this.api.patient().getPatient(this.patient.id).then(patient => {

          const internalHcp = patient.delegations
          const externalHcp = patient.patientHealthCareParties

          //internal team
          Promise.all(
              _.keys(internalHcp).map(hcpId =>
                  this.api.hcparty().getHealthcareParty(hcpId).then(hcp =>
                      internalTeam.push(hcp)
                  )
              )
          ).then(() => this.set('currentInternalCareTeam', internalTeam))

          //external team
          Promise.all(
              externalHcp.map(patientHcp =>
                  this.api.hcparty().getHealthcareParty(patientHcp.healthcarePartyId).then(hcp =>
                      externalTeam.push(hcp)
                  )
              )
          ).then(() => this.set('currentExternalCareTeam', externalTeam))

          //DMG owner
          this.set('currentDMGOwner', dmgOwner)
          if (this.patient.ssin && this.api.tokenId) {
              return this.api.hcparty().getHealthcareParty(this.user.healthcarePartyId)
                  .then(hcp =>
                      this.api.fhc().DMGcontroller().consultDmgUsingGET(this.api.keystoreId, this.api.tokenId, this.api.credentials.ehpassword, hcp.nihii, hcp.ssin, hcp.firstName, hcp.lastName, this.patient.ssin)
                  ).then(dmgConsultResp => {
                      var hcp = {
                          nihii: dmgConsultResp.hcParty.ids.find(id => id.s === 'ID_HCPARTY').value,
                          firstName: dmgConsultResp.hcParty.firstname,
                          lastName: dmgConsultResp.hcParty.familyname,
                          name: dmgConsultResp.hcParty.name,
                          isDmg: true,
                          referral: true,
                          referralPeriods: [{
                              startDate: dmgConsultResp.from,
                              endDate: dmgConsultResp.to
                          }]
                      }

                      dmgOwner.push(hcp);

                  }).then(() => this.set('currentDMGOwner', dmgOwner))
          }

      })
  }

  getHcpName(hcp){
      if(hcp.name && hcp.name !== '') {
          return hcp.name
      }
      else{
          return hcp.firstName + ' ' + hcp.lastName
      }
  }

  formatDate(date){
      if(date){
          return this.api.moment(date).format('DD/MM/YYYY')
      }
      else {
          return ''
      }
  }

  getStartDate(item){
      if(item.referralPeriods && item.referralPeriods[0]){
          return this.formatDate(item.referralPeriods[0].startDate)
      }
      else
      {
          return null;
      }
  }

  getEndDate(item){
      if(item.referralPeriods && item.referralPeriods[0]){
          return this.formatDate(item.referralPeriods[0].endDate)
      }
      else
      {
          return null;
      }
  }

  showInfoSelectedHcp(e){

	    this.$['showHcpInfo'].open()

      const pphcTab = this.patient.patientHealthCareParties
      const pphcTarget = pphcTab.find(pphc => pphc.healthcarePartyId === this.selectedCareProvider.id)

      //Comparer les dates pour le detenteur du dmg
      if(pphcTarget){
          pphcTarget.referralPeriods.map(rp => rp)
      }

      if(this.selectedCareProvider.isDmg)
      {

          this.selectedCareProvider.pphc = _.cloneDeep(this.selectedCareProvider);
          this.set('selectedPerson', this.selectedCareProvider)
      }
      else
      {
          this.selectedCareProvider.pphc = pphcTarget
          this.set('selectedPerson', this.selectedCareProvider)
      }

  }

  _timeFormat(date) {
      return date && this.api.moment(date).format(date > 99991231 ? 'DD/MM/YYYY HH:mm' : 'DD/MM/YYYY') || '';
  }

  _sharingHcp(item){
      if (item) {
          const mark = this.hcpSelectedForTeam.find(m => m.id === item.id)
          return mark && mark.check
      } else {
          return false
      }
  }

  _checkHcp(e){
      if (e.target.id !== "") {
          const mark = this.hcpSelectedForTeam.find(m => m.id === e.target.id)
          if (!mark) {
              this.push('hcpSelectedForTeam',{id:e.target.id, check:true})
          } else {
              mark.check = !mark.check
              this.notifyPath('hcpSelectedForTeam.*')
          }
      }

  }

  confirmSharing() {
      let pPromise = Promise.resolve([])
      const hcpId = this.user.healthcarePartyId

      pPromise = pPromise.then(pats =>
              this.api.patient().share(this.patient.id, hcpId, this.hcpSelectedForTeam.filter(hcp =>
                  hcp.check && hcp.id).map(hcp => hcp.id))
                  .then(pat => {
                          _.concat(pats, pat)
                          this.initCurrentCareTeam()
                      }
                  )
          )
      return pPromise
  }

  formatNihiiNumber(nihii) {
      return nihii ? ("" + nihii).replace(/([0-9]{1})([0-9]{5})([0-9]{2})([0-9]{3})/, '$1-$2-$3-$4') : ''
  }

  formatNissNumber(niss) {
      return niss ? ("" + niss).replace(/([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{3})([0-9]{2})/, '$1.$2.$3-$4.$5') : ''
  }

  chckInvite(e){
      if(e.target.checked){
          this.newHcpCareTeam.Invite = true
      }else{
          this.newHcpCareTeam.Invite = false
      }
  }
}

customElements.define(HtPatAdminCard.is, HtPatAdminCard);
