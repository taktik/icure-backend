/**
@license
Copyright (c) 2016 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/
import './app-theme.js';

import './shared-styles.js';
import './vaadin-icure-theme.js';
import './elements/tk-localizer.js';
import './elements/splash-screen/splash-screen.js';
import './elements/ht-tools/ht-export-key.js';
import './elements/ht-tools/ht-import-keychain.js';
import './elements/ht-tools/ht-access-log.js';
import './elements/ht-tools/ht-my-profile.js';
import './elements/ht-app/ht-app-login-dialog.js';
import './elements/ht-app/ht-app-first-login-dialog.js';
import './elements/ht-app/ht-app-register-keypair-dialog.js';
import './elements/menu-bar/menu-bar.js';
import './ht-main.js';
import './ht-pat.js';
import './ht-hcp.js';
import './ht-msg.js';
import './ht-view404.js';
import './elements/icc-api/icc-api.js';
import moment from 'moment/src/moment'
import Worker from 'worker-loader!./workers/ehboxWebworker.js'

import {PolymerElement, html} from '@polymer/polymer';
class HtApp extends TkLocalizerMixin(PolymerElement) {
  static get template() {
    return html`
        <style include="shared-styles">
            :host {
                display: block;
            }

            app-header {
                color: var(--app-text-color-light);
                background-color: var(--app-primary-color-dark);
                height: 64px;
                @apply --shadow-elevation-4dp;
            }

            app-header paper-icon-button {
                --paper-icon-button-ink-color: white;
            }

            app-toolbar {
                padding-right: 0;
                height: 64px;
                display: flex;
                flex-flow: row nowrap;
                justify-content: space-between;
                align-items: stretch;

            }

            :host iron-pages {
                height: calc(100% - 64px);
            }

            :host app-header-layout {
                height: 100%;
            }

            iron-icon {
                max-height: 20px;
                width: 20px;
                margin-right: 8px;
                color: rgba(255, 255, 255, 0.5);
            }

            iron-icon.smaller {
                height: 16px !important;
                width: 16px !important;
            }

            .icure-logo {
                float: left;
                height: 64px;
                margin-right: 8px;
            }

            paper-menu-button {
                position: relative;
                top: 50%;
                transform: translateY(-50%);
                --paper-menu-button-content: {
                    width: 480px;
                };
                --paper-menu-button-dropdown: {
                    padding: 0;
                }
            }

            .extra-menu {
                padding: 0;
            }

            .extra-menu-item {
                --paper-item-focused: {
                    background: white;
                }
            }

            .extra-menu-item:last-child {
                border-top: 1px solid var(--app-background-color-dark);
            }

            .extra-menu-item iron-icon {
                color: var(--app-text-color-disabled);
            }

            paper-tabs {
                --paper-tabs-selection-bar-color: var(--app-secondary-color);
                height: 64px;
            }

            paper-tab {
                --paper-tab-ink: var(--app-secondary-color);
            }

            paper-tab.iron-selected {
                font-weight: bold;
            }

            paper-tab.iron-selected > iron-icon {
                color: var(--app-secondary-color);
            }

            .mobile-menu-btn {
                align-self: center;
                display: none;
            }

            .mobile-menu {
                position: fixed;
                top: 64px;
                left: 0;
                bottom: 0;
                background: var(--app-light-color);
                height: calc(100vh - 64px);
                overflow: hidden;
                width: 0;
                transition: width .24s cubic-bezier(0.4, 0.0, 0.2, 1);
                visibility: hidden;
                @apply --shadow-elevation-6dp;
                padding: 0 1em;
            }

            .mobile-menu.open {
                visibility: visible;
                width: 180px;
            }

            .mobile-menu paper-button {
                font-size: 14px;
                color: var(--app-text-color);
                width: 100%;
                margin: 8px 0;
                justify-content: flex-start;
                --paper-button-ink-color: var(--app-secondary-color);
            }

            .mobile-menu paper-button iron-icon {
                color: var(--app-text-color-disabled);
            }

            .mobile-menu paper-button.iron-selected {
                font-weight: 600;
            }

            .mobile-menu paper-button.iron-selected iron-icon {
                color: var(--app-secondary-color);
            }

            .mobile-menu-overlay {
                position: absolute;
                top: 64px;
                left: 0;
                width: 100vw;
                height: calc(100vh - 64px);
                display: none;
                background: var(--app-text-color-disabled);
            }

            .mobile-menu-overlay.open {
                display: block;

            }

            .mobile-menu-container {
                display: none;
            }

            .ehbox-notification-panel {
                position: fixed;
                top: 50%;
                right: 0;
                z-index: 1000;
                color: white;
                font-size: 13px;
                background: rgb(14, 255, 107);
                height: 48px;
                padding: 0 8px 0 12px;
                border-radius: 3px 0 0 3px;
                width: 0;
                opacity: 0;
            }

            .notification-panel {
                position: fixed;
                top: 50%;
                right: 0;
                z-index: 1000;
                color: white;
                font-size: 13px;
                background: rgba(255, 0, 0, 0.55);
                height: 48px;
                padding: 0 8px 0 12px;
                border-radius: 3px 0 0 3px;
                width: 0;
                opacity: 0;
            }

            .notification {
                animation: notificationAnim 7.5s ease-in;
            }

            .notificationEhbox {
                animation: notificationEhboxAnim 7.5s ease-in;
            }

            .log-info {
                align-self: center;
            }

            .log-info p {
                position: relative;
                font-family: 'Roboto', Arial, Helvetica, sans-serif;
                font-size: 12px;
                color: var(--app-text-color-light);
            }

            .log-info p .ehealth-connection-status {
                content: '';
                display: block;
                position: absolute;
                top: 50%;
                transform: translateY(-50%);
                right: -11px;
                height: 7px;
                width: 7px;
                border-radius: 8px;
            }

            .connected {
                background: var(--app-status-color-ok);
            }

            .pending {
                background: var(--app-status-color-pending);
                animation: pendingAnim .8s ease-in-out infinite alternate;
            }

            .disconnected {
                background: var(--app-status-color-nok);
            }

            @keyframes pendingAnim {
                from {
                    background: var(--app-status-color-pending);
                }
                to {
                    background: transparent;
                }
            }

            @keyframes notificationAnim {
                0% {
                    width: 0;
                    opacity: 0;
                }
                10% {
                    width: 160px;
                    opacity: 1;
                }
                12% {
                    width: 144px;
                    opacity: 1;
                }
                88% {
                    width: 144px;
                    opacity: 1;
                }
                100% {
                    width: 0;
                    opacity: 0;
                }
            }

            @keyframes notificationEhboxAnim {
                0% {
                    width: 0;
                    opacity: 0;
                }
                10% {
                    width: 160px;
                    opacity: 1;
                }
                12% {
                    width: 280px;
                    opacity: 1;
                }
                88% {
                    width: 280px;
                    opacity: 1;
                }
                100% {
                    width: 0;
                    opacity: 0;
                }
            }

            @media (max-width: 768px) {
                paper-button {
                    margin-right: 10px;
                }

                paper-tabs {
                    display: none;
                }

                .mobile-menu-btn {
                    display: block;
                }

                .mobile-menu-container {
                    display: block;
                }
            }

            .inviteHcpInput {
                width: 400px;
            }

            .formNewHcp {
                height: 350px;
                width: 450px;
            }

            .buttons {
                height: 30px;
            }

            .top-gradient {
                line-height: 0;
                font-size: 0;
                display: block;
                background: linear-gradient(90deg, var(--app-secondary-color-dark), var(--app-secondary-color));
                height: 10px;
                position: relative;
                top: 0;
                left: 0;
                right: 0;
                margin: 0;
                border-radius: 2px 2px 0 0;
            }

            .timer {
                font-size: 10px;
                width: auto;
                position: absolute;
                margin-top: -10px;

            }

        </style>

        <icc-api id="api" host="/rest/v1" fhc-host="https://fhc.icure.cloud" headers="[[headers]]" credentials="[[credentials]]"></icc-api>

         <paper-item id="noehealth" class="notification-panel noehealth">[[localize('no_ehe_con','No Ehealth connection',language)]]
             <iron-icon icon="icons:warning"></iron-icon>
         </paper-item>

         <ht-app-login-dialog id="loginDialog" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" credentials="[[credentials]]" on-login="login"></ht-app-login-dialog>
         <ht-app-first-login-dialog id="firstConnectionDialog" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" credentials="[[credentials]]" api="[[api]]" route="{{route}}" user="[[user]]"></ht-app-first-login-dialog>
         <ht-app-register-keypair-dialog id="registerKeyPairDialog" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" api="[[api]]" user="[[user]]" message="[[registerKeyPairDialogMessage]]" on-file-selected="importPrivateKey" on-key-scanned="importScannedPrivateKey"></ht-app-register-keypair-dialog>
         <ht-export-key id="export-key" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" api="[[api]]" user="[[user]]"></ht-export-key>
         <ht-import-keychain id="ht-import-keychain" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" api="[[api]]" user="[[user]]"></ht-import-keychain>
         <ht-access-log id="ht-access-log" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" api="[[api]]" user="[[user]]"></ht-access-log>
         <ht-my-profile id="ht-my-profile" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" api="[[api]]" user="[[user]]" on-user-saved="_userSaved"></ht-my-profile>


         <app-location route="{{route}}" query-param="{{queryParams}}" use-hash-as-path="true"></app-location>
         <app-route route="{{route}}" pattern="/:page" data="{{routeData}}" tail="{{subroute}}"></app-route>
         <app-route route="{{subroute}}" pattern="/:page" data="{{subrouteData}}"></app-route>


         <app-drawer-layout fullbleed>
             <!-- Main content -->
            <app-header-layout fullbleed="">
                <app-header slot="header" fixe condenses effects="waterfall">
                    <app-toolbar id="mainToolbar" class="" sticky="">
                        <!-- Mobile Menu -->
                        <paper-icon-button class="mobile-menu-btn" icon="menu" on-tap="_triggerMenu"></paper-icon-button>
                        <div class="mobile-menu-container">
                            <div id="overlayMenu" class="mobile-menu-overlay" on-tap="_triggerMenu"></div>
                            <paper-listbox id="mobileMenu" class="mobile-menu" selected="[[routeData.page]]" attr-for-selected="name">
                                <paper-button name="main" on-tap="doRoute">
                                    <iron-icon class="iron-icon" icon="home"></iron-icon>
                                    [[localize('sum')]]
                                </paper-button>
                                <paper-button name="pat" on-tap="doRoute">
                                    <iron-icon icon="vaadin:user-heart"></iron-icon>
                                    [[localize('pat')]]
                                </paper-button>
                                <paper-button name="hcp" on-tap="doRoute">
                                    <iron-icon icon="vaadin:hospital"></iron-icon>
                                    [[localize('hc_par')]]
                                </paper-button>
                                <paper-tab name="msg" on-tap="doRoute">
                                    <iron-icon class="smaller" icon="communication:email"></iron-icon>
                                    [[localize('msg')]]
                                </paper-tab>
                                <paper-button name="logout" on-tap="doRoute">
                                    <iron-icon icon="power-settings-new"></iron-icon>
                                    [[localize('log_out')]]
                                </paper-button>
                            </paper-listbox>
                        </div>
                        
                        <!-- Regular Tabs Menu -->
                        <paper-tabs selected="[[routeData.page]]" attr-for-selected="name" role="navigation">
                            <paper-tab name="main" on-tap="doRoute">
                                <iron-icon class="iron-icon" icon="home"></iron-icon>
                                [[localize('sum')]]
                            </paper-tab>
                            <paper-tab name="pat" on-tap="doRoute">
                                <iron-icon class="smaller" icon="vaadin:user-heart"></iron-icon>
                                [[localize('pat')]]
                            </paper-tab>
                            <paper-tab name="hcp" on-tap="doRoute">
                                <iron-icon class="smaller" icon="vaadin:hospital"></iron-icon>
                                [[localize('hc_par')]]
                            </paper-tab>
                            <paper-tab name="msg" on-tap="doRoute">
                                <iron-icon class="smaller" icon="communication:email"></iron-icon>
                                [[localize('msg')]]
                            </paper-tab>
                            <paper-tab name="logout" on-tap="doRoute">
                                <iron-icon icon="power-settings-new"></iron-icon>
                                [[localize('log_out')]]
                            </paper-tab>
                        </paper-tabs>
                        <div class="log-info">
                            <p>[[user.login]] – <span id="ehealth">[[localize('ehe','eHealth',language)]]</span> <span id="eHealthStatus" class="ehealth-connection-status pending"></span></p>
                            <paper-tooltip for="ehealth">
                                <template is="dom-if" if="[[api.tokenId]]">
                                    [[localize('ehe_is_con','eHealth is connected',language)]]
                                </template>
                                <template is="dom-if" if="[[!api.tokenId]]">
                                    [[localize('ehe_is_not_con','eHealth is not connected',language)]]
                                </template>
                            </paper-tooltip>
                        </div>
                        <div>
                            <svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:a="http://ns.adobe.com/AdobeSVGViewerExtensions/3.0/" x="0px" y="0px" width="92px" height="64px" viewBox="0 0 92 64" style="enable-background:new 0 0 92 64;" xml:space="preserve" class="icure-logo">
								<style type="text/css">
								.st1 {
                                    fill: #FFFFFF;
                                }

                                .st2 {
                                    fill: #66DEA1;
                                }
								</style>
								<defs>
								</defs>
								<g style="transform: translateY(-5px);">
									<g>
										<rect x="44.3" y="32.2" class="st1" width="1.3" height="12.6"></rect>
										<path class="st1" d="M45,27.6c-0.6,0-1,0.5-1,1.2c0,0.6,0.4,1.2,1,1.2h0c0.6,0,1-0.5,1-1.2C46,28.1,45.6,27.6,45,27.6z"></path>
										<path class="st1" d="M60.2,43c-1,0.5-2.3,0.8-3.7,0.8c-4.3,0-6.9-2.9-6.9-7.7c0-5,2.6-8,7-8c1.3,0,2.5,0.3,3.4,0.7l0.1,0l0.4-1.2
											l-0.1,0c-0.4-0.2-1.7-0.8-3.8-0.8c-5,0-8.4,3.7-8.4,9.2c0,6.6,4.2,9,7.9,9c2.1,0,3.7-0.5,4.5-0.9l0.1,0L60.2,43L60.2,43z"></path>
										<path class="st1" d="M72.3,41.7v-9.5H71V40c0,0.4-0.1,0.9-0.2,1.3c-0.5,1.1-1.6,2.4-3.4,2.4c-2,0-3-1.5-3-4.5v-7.1H63v7.3
											c0,5,2.9,5.6,4.1,5.6c1.9,0,3.3-1.2,4-2.3l0.1,2.1h1.3l0-0.1C72.3,43.8,72.3,42.9,72.3,41.7z"></path>
										<path class="st1" d="M80.5,31.9c-1.4,0-2.7,1-3.3,2.6l0-2.3h-1.3l0,0.1C76,33.4,76,34.6,76,36v8.8h1.3v-6.9c0-0.5,0-0.9,0.1-1.2
											c0.3-2.1,1.5-3.4,3-3.4c0.2,0,0.4,0,0.5,0l0.1,0V32L81,32C80.9,31.9,80.7,31.9,80.5,31.9z"></path>
										<path class="st1" d="M91.1,34.1c-0.8-1.4-2.1-2.2-3.8-2.2c-3.2,0-5.4,2.7-5.4,6.8c0,3.8,2.2,6.3,5.5,6.3c2.1,0,3.3-0.6,3.7-0.8
											l0.1,0L91,43.1l-0.1,0c-0.7,0.4-1.6,0.7-3.2,0.7c-1.3,0-4.3-0.5-4.4-5.3h8.6l0-0.1C92,38.1,92,38,92,37.6
											C92,37.1,91.9,35.5,91.1,34.1z M83.4,37.3c0.3-1.9,1.4-4.1,3.8-4.1c1,0,1.8,0.3,2.3,0.9c0.9,1,1.1,2.5,1.1,3.2H83.4z"></path>
									</g>
									<path class="st2" d="M36.2,38.4c-0.4-4.3-2.9-6.6-7.4-6.6l-5.5,0c-0.2,0-0.4,0.1-0.5,0.3l-0.6,1.5l-1.9-8.4c0-0.3-0.3-0.5-0.6-0.5
									c-0.3,0-0.5,0.2-0.6,0.5l-2.3,10.4L15.4,29c0-0.3-0.3-0.5-0.6-0.5c-0.3,0-0.5,0.2-0.6,0.4L13,31.8H8.3V33h5.2
									c0.3,0,0.5-0.2,0.6-0.4l0.6-1.5l1.6,7.2c0.1,0.3,0.3,0.5,0.6,0.5c0.3,0,0.5-0.2,0.6-0.5l2.2-10.3l1.7,7.5c0,0.3,0.2,0.5,0.5,0.5
									c0.2,0.1,0.5-0.1,0.6-0.3l1.2-2.7l5.1,0c3.8,0,5.8,1.5,6.3,4.7c0.3,2-0.2,3.8-1.3,5.1c-1.2,1.4-3,2-5,1.9H8.8
									c-4.1,0-7.5-3.4-7.6-7.6C1,32.8,4,29.1,8,28.8c0.3,0,0.5-0.3,0.5-0.5c0.3-2.3,1.2-4.4,2.7-6c1.8-1.9,4.2-3,6.8-3c2.6,0,5,1.1,6.8,3
									c1.2,1.3,2.3,3.7,2.7,6.4l0,0.2l0.2,0c0.2,0,0.7-0.1,0.8-0.1l0.2,0l0-0.2c-0.5-3-1.6-5.6-3-7.1c-2-2.1-4.7-3.3-7.6-3.3
									c-2.9,0-5.6,1.2-7.6,3.3c-1.6,1.7-2.6,3.9-3,6.3C3.1,28.3-0.1,32.3,0,37c0.1,2.4,1,4.6,2.7,6.4C4.4,45.1,6.6,46,8.8,46l19.7,0
									c0.1,0,0.2,0,0.4,0c2.2,0,4.2-0.9,5.6-2.4C35.7,42.2,36.3,40.4,36.2,38.4z"></path>
								</g>
							</svg>
                            <paper-menu-button horizontal-align="right" close-on-activate="" no-overlap="" no-animations="" focused="false">
                                <paper-icon-button icon="icons:more-vert" slot="dropdown-trigger" alt="menu"></paper-icon-button>
                                <paper-listbox class="extra-menu" slot="dropdown-content" stop-keyboard-event-propagation="">
                                    <div class="dropdown-content"></div><!-- workaround to fix that the fist element of the list was always focus -->

                                    <paper-item class="extra-menu-item" on-tap="_openExportKey">[[localize('tra_pri_key_/_con_tab','Transfer private key / Connect
                                        tablet',language)]]
                                    </paper-item>
                                    <paper-item class="extra-menu-item" on-tap="_importKeychain">[[localize('imp_my_ehe_key','Import my eHealth keychain',language)]]</paper-item>
                                    <paper-item class="extra-menu-item" on-tap="_inviteHCP">[[localize('inviteHCP','Invite a colleague ',language)]]</paper-item>
                                    <paper-item class="extra-menu-item" on-tap="_logList">[[localize('acc_log','Access Log',language)]]</paper-item>
                                    <paper-item class="extra-menu-item" on-tap="_myProfile">
                                        <iron-icon icon="icons:account-circle"></iron-icon>
                                        [[localize('my_pro','My profile',language)]]
                                    </paper-item>
                                </paper-listbox>
                            </paper-menu-button>
                            <div class="timer" id="timer">[[localize('time_lft','Time left:',language)]] {{disconnectionTimer}} m.</div>
                        </div>
                    </app-toolbar>

                </app-header>
                <iron-pages selected="[[view]]" attr-for-selected="name" fallback-selection="view404" role="main">
                    <ht-main name="main" api="[[api]]" user="[[user]]" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]">
                        <splash-screen></splash-screen>
                    </ht-main>
                    <ht-pat name="pat" api="[[api]]" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" user="[[user]]" route="{{subroute}}" on-user-saved="_userSaved">
                        <splash-screen></splash-screen>
                    </ht-pat>
                    <ht-hcp name="hcp" api="[[api]]" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" user="[[user]]">
                        <splash-screen></splash-screen>
                    </ht-hcp>
                    <ht-msg name="msg" api="[[api]]" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" user="[[user]]">
                        <splash-screen></splash-screen>
                    </ht-msg>
                    <ht-view404 name="view404"></ht-view404>
                </iron-pages>
            </app-header-layout>
        </app-drawer-layout>

        <paper-dialog id="ht-invite-hcp">
            <h3>[[localize('inviteHCP','Invite a colleague ',language)]]</h3>
            <div id="" class="formNewHcp">
                <paper-input class="inviteHcpInput" label="[[localize('las_nam','Last name',language)]]" value="{{lastName}}"></paper-input>
                <paper-input class="inviteHcpInput" label="[[localize('fir_nam','First name',language)]]" value="{{firstName}}"></paper-input>
                <paper-input class="inviteHcpInput" label="[[localize('ema','Email',language)]]" value="{{email}}"></paper-input>
                <paper-input class="inviteHcpInput" label="[[localize('inami','NIHII',language)]]" value="{{nihii}}"></paper-input>
                <paper-input class="inviteHcpInput" label="[[localize('ssin','SSIN',language)]]" value="{{ssin}}"></paper-input>
            </div>

            <div class="buttons">
                <paper-button dialog-dismiss="">[[localize('can','Cancel',language)]]</paper-button>
                <paper-button dialog-confirm="" autofocus="" on-tap="confirmUserInvitation">[[localize('invite','Invite',language)]]</paper-button>
            </div>
        </paper-dialog>
        <paper-dialog id="ht-invite-hcp-link">
            <h3>Lien de première connexion</h3>
            <h4>[[invitedHcpLink]]</h4>
        </paper-dialog>

        <paper-item id="ehboxInboxMessage" class="ehbox-notification-panel inboxMessage">
            <iron-icon icon="communication:email"></iron-icon>
            [[ehboxWebWorkerMessage]]
        </paper-item>
`;
  }

  static get is() {
      return 'ht-app'
  }

  static get properties() {
      return {
          api: {
              type: Object,
              value: null
          },
          user: {
              type: Object,
              value: null
          },
          language: {
              type: String,
              value: 'fr'
          },
          i18n: {
              Type: Object,
              value() {
                  moment.locale('fr')
                  const res = {
                      monthNames: moment.months(),
                      weekdays: moment.weekdays(),
                      weekdaysShort: moment.weekdaysShort(),
                      firstDayOfWeek: moment.localeData().firstDayOfWeek(),
                      week: 'Semaine',
                      calendar: 'Calendrier',
                      clear: 'Clear',
                      today: 'Aujourd\'hui',
                      cancel: 'Annuler',
                      formatDate(d) {
                          //return moment(d).format(moment.localeData().longDateFormat('L'))
                          return moment(d).format('DD/MM/YYYY')
                      },
                      parseDate(s) {
                          return moment(s, 'DD/MM/YYYY').toDate()
                      },
                      formatTitle(monthName, fullYear) {
                          return monthName + ' ' + fullYear
                      }
                  }
                  return res
              }
          },
          view: {
              type: String,
              reflectToAttribute: true,
              observer: '_viewChanged'
          },
          headers: {
              type: Object,
              value: {"Content-Type": "application/json"}
          },
          credentials: {
              type: Object,
              value: {logout: false}
          },
          lazyPages: {
              type: Object,
              value: {
                  main() {
                      //import(/* webpackChunkName: "ht-main" */ './ht-main.html')
                  },
                  pat() {
                      //import(/* webpackChunkName: "ht-pat" */ './ht-pat.html')
                  },
                  hcp() {
                      //import(/* webpackChunkName: "ht-hcp" */ './ht-hcp.html')
                  },
                  msg() {
                      //import(/* webpackChunkName: "ht-msg" */ './ht-msg.html')
                  },
                  view404() {
                      //import(/* webpackChunkName: "ht-view404" */ './ht-view404.html')
                  }
              }
          },
          resources: {
              value() {
                  return require('./elements/language/language.json')
              }
          },
          invitedHcpLink: {
              type: String,
              value: ""
          },
          disconnectionTimer: {
              type: Number,
              value: 60
          },
          connectionTime: {
              type: Number
          },
          ehboxWebWorkerMessage: {
              type: String,
              value: ""
          },
          EhboxCheckingActive: {
              type: Boolean,
              value: false
          },
          worker: {
              type: Worker
          },
          timeOutId: {
              type: String
          }
      }
  }

  static get observers() {
      return [
          '_routePageChanged(routeData.page)'
      ]
  }

  constructor() {
      super()
  }

  ready() {
      super.ready()
      if (!this.route.__queryParams.token && !this.route.__queryParams.userId) {
          let newHref = window.location.href.includes('#/') ? window.location.href : window.location.href.replace(/\/?#?$/, '/#')
          let fullHref = newHref.endsWith('/') ? newHref : newHref + '/'
          if (fullHref !== window.location.href) {
              window.location.href = fullHref
          }
      }
      window.app = this
      this.set('api', this.$.api)

      document.onmousemove = this.resetTimer.bind(this)
      document.onkeypress = this.resetTimer.bind(this)

      this._startCheckInactiveTimer()
  }

  resetTimer() {
      this.set('connectionTime', +new Date())
  }

  _userSaved(e) {
      this.set('user', e.detail)
  }


  _startCheckInactiveTimer() {
      clearInterval(this.interval)
      this.interval = setInterval(() => {
          const newTimer = Math.floor(60 - (+new Date() - this.connectionTime) / 1000 / 60)
          if (newTimer > 0) {
              this.set('disconnectionTimer', newTimer)
          } else {
              this.set('routeData.page', 'logout')
              this._triggerMenu()
          }
      }, 10000)

  }

  _timeCheck(period = 300000) {
      setTimeout(() => {
          if (this.api.tokenId) {
              this.api.fhc().Stscontroller().checkTokenUsingGET(this.api.tokenId).then(isTokenValid => {
                  if (!isTokenValid) {
                      this._getToken().then(() => {
                          this._timeCheck()
                      }).catch(() => this._timeCheck(10000))
                  } else {
                      this._timeCheck()
                  }
              }).catch(() => this._timeCheck(10000))
          } else {
              this._timeCheck()
          }
      }, period)
  }

  _inboxMessageCheck(period = 1000) {
      clearTimeout(this.timeOutId)
      if (this.EhboxCheckingActive === false) {
          this.timeOutId = setTimeout(() => {
              if (this.api.tokenId) {
                  this.set('EhboxCheckingActive', true)
                  console.log("EhboxCheckingActive "+this.EhboxCheckingActive)
                  this.checkEhboxMessage()
              }else {
                  this._inboxMessageCheck()
              }

         }, period)
      }
  }

  _routePageChanged(page) {
      if (page === 'logout') {
          sessionStorage.removeItem('auth')
          this.authenticated = false
          this.worker && this.worker.terminate()
          this.set('view', 'auth')
      } else {
          console.log("page is -> " + page)
          if (!this.authenticated && (!page || !page.startsWith('auth'))) {
              if (sessionStorage.getItem('auth') || (this.route.__queryParams.token && this.route.__queryParams.userId)) {
                  this.loginAndRedirect(page)
              } else {
                  this.set('routeData.page', 'auth/' + (!page ? 'main' : page.startsWith('logout') ? 'main' : page))
              }
          } else {
              this.set('view', page ? page.replace(/\/$/, '') : 'main')
          }
          this._startCheckInactiveTimer('refresh')
      }
  }

  _triggerMenu() {
      let menu = this.$.mobileMenu
      let overlay = this.$.overlayMenu

      overlay.classList.toggle('open')
      menu.classList.toggle('open')


  }

  _viewChanged(view) {
      if (view.startsWith('auth')) {
          this.$.loginDialog.opened = true
          return
      }
      if (this.lazyPages[view]) {
          this.lazyPages[view]()
      } else {
          this._showPage404()
      }

  }

  _showPage404() {
      this.view = 'view404'
  }

  doRoute(e) {
      this.set('routeData.page', (e.target.getAttribute('name') || e.target.parentElement.getAttribute('name')) + "/")
      this._triggerMenu()
  }

  _openExportKey() {
      this.$['export-key'].open()
  }

  _importKeychain() {
      this.$['ht-import-keychain'].open()
  }

  _inviteHCP() {
      this.$['ht-invite-hcp'].open()
  }

  _myProfile() {
      this.$['ht-my-profile'].open()
  }

  _getToken() {
      return this.api.hcparty().getHealthcareParty(this.user.healthcarePartyId).then(hcp =>
          this.api.fhc().Stscontroller().requestTokenUsingGET(this.credentials.ehpassword, hcp.ssin, this.api.keystoreId).then(res => {
              this.$.eHealthStatus.classList.remove('pending')
              this.$.eHealthStatus.classList.remove('disconnected')
              this.$.eHealthStatus.classList.add('connected')

              this.set('api.tokenId', res.tokenId)
              this.set('api.token', res.token)

              return res.tokenId
          }).catch(() => {
              this.$.eHealthStatus.classList.remove('pending')
              this.$.eHealthStatus.classList.remove('connected')
              this.$.eHealthStatus.classList.add('disconnected')
          })
      )
  }

  loginAndRedirect(page) {
      const sAuth = JSON.parse(sessionStorage.getItem('auth'))
      if (!this.credentials || (!this.credentials.password && sAuth && sAuth.password) || (!this.credentials.appToken && sAuth && sAuth.appToken)) {
          this.set('credentials', sAuth)
      }

      if (this.route.__queryParams.token && this.route.__queryParams.userId) {
          this.set('headers', _.assign(_.assign({}, this.headers),
              {Authorization: 'Basic ' + btoa(this.route.__queryParams.userId + ':' + this.route.__queryParams.token)}))
      } else if ((this.credentials.userId && this.credentials.appToken)) {
          this.set('headers', _.assign(_.assign({}, this.headers),
              {Authorization: 'Basic ' + btoa(this.credentials.userId + ':' + this.credentials.appToken)}))
      }
      else if ((this.credentials.username && this.credentials.password)) {
          this.set('headers', _.assign(_.assign({}, this.headers),
              {Authorization: 'Basic ' + btoa(this.credentials.username + ':' + this.credentials.password + (this.credentials.twofa ? '|' + this.credentials.twofa : ''))}))
      }

      //Be careful not to use this.api here as it might not have been defined yet
      //TODD debounce here
      this.$.api.user().getCurrentUser().then(u => {
          this.set('user', u)
          this.set('connectionTime', +new Date())
          this.api.hcparty().getCurrentHealthcareParty().then(hcp => {
              const language = (hcp.languages || ['fr']).find(lng => lng && lng.length === 2)
              language && this.set('language', language)
          })

          this.$.loginDialog.opened = false

          this.set('credentials.twofa', null)
          this.set('credentials.userId', u.id)
          this.set('credentials.appToken', u.applicationTokens && u.applicationTokens.ICC)

          if ((this.credentials.userId && this.credentials.appToken)) {
              this.set('credentials.password', null)
              this.set('headers.Authorization', 'Basic ' + btoa(this.credentials.userId + ':' + this.credentials.appToken))
          }
          sessionStorage.setItem('auth', JSON.stringify(this.credentials))

          if (!this.authenticated) {
              this.authenticated = true

              if (this.$.api.crypto().RSA.loadKeyPairNotImported(u.healthcarePartyId)) {
                  this.api.hcparty().getCurrentHealthcareParty().then(hcp => this.$.api.crypto().checkPrivateKeyValidity(hcp)).then(ok => {
                      if (ok) {
                          this.api.loadUsersAndHcParties()
                          if (this.credentials.ehpassword) {
                              const ehKeychain = this.$.api.crypto().loadKeychainFromBrowserLocalStorage(this.user.healthcarePartyId)
                              if (ehKeychain) {
                                  this.$.api.fhc().Stscontroller().uploadKeystoreUsingPOST(ehKeychain).then(res => {
                                      this.$.api.keystoreId = res.uuid
                                      this._getToken()
                                  })
                              } else {
                                  this.$.noehealth.classList.add("notification")

                                  this.$.eHealthStatus.classList.remove('pending')
                                  this.$.eHealthStatus.classList.remove('connected')
                                  this.$.eHealthStatus.classList.add('disconnected')
                              }
                          } else {
                              this.$.eHealthStatus.classList.remove('pending')
                              this.$.eHealthStatus.classList.remove('connected')
                              this.$.eHealthStatus.classList.add('disconnected')
                          }
                          const destPage = page || (this.routeData && this.routeData.page === 'auth' && this.subrouteData && this.subrouteData.page ? this.subrouteData.page : 'main')
                          if (!this.routeData || destPage !== this.routeData.page) {
                              this.set('routeData.page', destPage)
                          } else {
                              this._routePageChanged(destPage)
                          }
                      } else {
                          this.registerKeyPairDialogMessage = "The key registered in your browser is invalid"
                          this.$.registerKeyPairDialog.opened = true
                      }
                  })
              } else {
                  this.api.hcparty().getCurrentHealthcareParty().then(hcp => {
                      if (hcp.publicKey) {
                          this.registerKeyPairDialogMessage = ""
                          this.$.registerKeyPairDialog.opened = true
                      } else {
                          this.$.firstConnectionDialog.opened = true
                      }
                  })
              }
          }
          this._timeCheck()
          this._inboxMessageCheck()
      }).catch(function (e) {
          this.authenticated = false
          sessionStorage.removeItem('auth')
          this.set("credentials.error", "Wrong user or password")
      }.bind(this))
  }

  login(event, loginObject) /* this is called from mouseDown with 2 arguments */ {
      this.set('credentials', loginObject && loginObject.credentials)
      this.loginAndRedirect(loginObject && loginObject.page)
  }

  importPrivateKey(e, selectedRsaFile) {
      selectedRsaFile && selectedRsaFile.name && this.api.crypto().loadKeyPairsInBrowserLocalStorage(this.user.healthcarePartyId, selectedRsaFile).then(function () {
          if (this.$.api.crypto().RSA.loadKeyPairNotImported(this.user.healthcarePartyId)) {
              this.$.registerKeyPairDialog.opened = false
              this.set("registerKeyPairDialogMessage", "")
              this.set('routeData.page', 'main/')
          } else {
              this.set("registerKeyPairDialogMessage", "Invalid key file")
              this.$.registerKeyPairDialog.reset()
          }
      }.bind(this)).catch(e => console.log(e))
  }

  importScannedPrivateKey(e, jwkKey) {
      this.api.crypto().loadKeyPairsAsJwkInBrowserLocalStorage(this.user.healthcarePartyId, jwkKey).then(function () {
          if (this.$.api.crypto().RSA.loadKeyPairNotImported(this.user.healthcarePartyId)) {
              this.$.registerKeyPairDialog.opened = false
              this.set("registerKeyPairDialogMessage", "")
              this.set('routeData.page', 'main/')
          } else {
              this.set("registerKeyPairDialogMessage", "Invalid key file")
              this.$.registerKeyPairDialog.reset()
          }
      }.bind(this)).catch(e => console.log(e))
  }

  togglePanel(e) {
  }

  confirmUserInvitation() {

      this.api.hcparty().createHealthcareParty({
          "name": this.lastName + " " + this.firstName,
          "lastName": this.lastName,
          "firstName": this.firstName,
          "nihii": this.nihii,
          "ssin": this.ssin
      }).then(hcp => {
          this.api.user().createUser({
              "healthcarePartyId": hcp.id,
              "name": this.lastName + " " + this.firstName,
              "email": this.email,
              "applicationTokens": {"tmpFirstLogin": this.api.crypto().randomUuid()},
              "status": "ACTIVE",
              "type": "database"
          }).then(usr => {
              this.invitedHcpLink = window.location.origin + window.location.pathname + '/?userId=' + usr.id + '&token=' + usr.applicationTokens.tmpFirstLogin
              this.$['ht-invite-hcp-link'].open()
          })
      })
  }

  checkEhboxMessage() {
      const keyPair = this.api.crypto().RSA.loadKeyPairNotImported(this.user.healthcarePartyId)
      this.$.ehboxInboxMessage.classList.remove('notificationEhbox')

      this.worker = new Worker()

      this.worker.postMessage({
          action:             "loadEhboxMessage",
          hcpartyBaseApi:     this.api.crypto().hcpartyBaseApi,
          fhcHost:            this.api.fhc().host,
          fhcHeaders:         JSON.stringify(this.api.fhc().headers),
          iccHost:            this.api.host,
          iccHeaders:         JSON.stringify(this.api.headers),
          tokenId:            this.api.tokenId,
          keystoreId:         this.api.keystoreId,
          user:               this.user,
          ehpassword:         this.credentials.ehpassword,
          boxId:              "INBOX",
          keyPair:            keyPair
      })

      this.worker.onmessage = e => {
          this.set('ehboxWebWorkerMessage', e.data.message)
          this.$.ehboxInboxMessage.classList.add('notificationEhbox')
          console.log("Le worker à repondu " + e.data.message)
      }

  }

  _logList() {
      this.$['ht-access-log'].open()
  }
}

customElements.define(HtApp.is, HtApp)
