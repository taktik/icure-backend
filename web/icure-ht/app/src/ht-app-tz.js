/*
@license
Copyright (c) 2016 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/
import './app-theme-tz.js';

import './shared-styles.js';
import './vaadin-icure-theme.js';
import './elements/tk-localizer.js';
import './elements/splash-screen/splash-screen-tz.js';
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
class HtAppTz extends TkLocalizerMixin(PolymerElement) {
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

            paper-tab:hover {
                background: rgba(255, 255, 255, 0.05);
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
                top: 50%;
                transform: scaleX(1) translateY(-50%);
                margin-left: -36px;
                color: var(--app-text-color-light);
                opacity: 1;
                transition: all .24s;
            }

            .logo:hover + .timer {
                opacity: 0;
                transform: scaleX(0);
            }

            .logo {
                width: 150px;
                height: 64px;
            }

            .logo-text {
                visibility: hidden;
                transform-origin: center right;
                transform: scaleX(0.3);
                transition: transform .24s;
            }

            .logo:hover .logo-text {
                visibility: visible;
                transform: scaleX(1);
            }
        </style>

        <icc-api id="api" host="/rest/v1" fhc-host="https://fhc.icure.cloud" headers="[[headers]]" credentials="[[credentials]]"></icc-api>

        <paper-item id="noehealth" class="notification-panel noehealth">[[localize('no_ehe_con','No Ehealth connection',language)]]
            <iron-icon icon="icons:warning"></iron-icon>
        </paper-item>

        <ht-app-login-dialog id="loginDialog" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" credentials="[[credentials]]"
                             on-login="login"></ht-app-login-dialog>
        <ht-app-first-login-dialog id="firstConnectionDialog" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" credentials="[[credentials]]" api="[[api]]"
                                   route="{{route}}" user="[[user]]"></ht-app-first-login-dialog>
        <ht-app-register-keypair-dialog id="registerKeyPairDialog" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" api="[[api]]" user="[[user]]"
                                        message="[[registerKeyPairDialogMessage]]" on-file-selected="importPrivateKey"
                                        on-key-scanned="importScannedPrivateKey"></ht-app-register-keypair-dialog>
        <ht-export-key id="export-key" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" api="[[api]]" user="[[user]]"></ht-export-key>
        <ht-import-keychain id="ht-import-keychain" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" api="[[api]]" user="[[user]]"></ht-import-keychain>
        <ht-access-log id="ht-access-log" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" api="[[api]]" user="[[user]]"></ht-access-log>
        <ht-my-profile id="ht-my-profile" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" api="[[api]]" user="[[user]]"
                       on-user-saved="_userSaved"></ht-my-profile>


        <app-location route="{{route}}" query-param="{{queryParams}}" use-hash-as-path="true"></app-location>
        <app-route route="{{route}}" pattern="/:page" data="{{routeData}}" tail="{{subroute}}"></app-route>
        <app-route route="{{subroute}}" pattern="/:page" data="{{subrouteData}}"></app-route>

        <app-drawer-layout fullbleed>
            <!-- Main content -->
            <app-header-layout fullbleed>
                <app-header slot="header" fixed condenses effects="waterfall">
                    <app-toolbar id="mainToolbar" class="" sticky>
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
                            <p>[[user.login]] – <span id="ehealth">[[localize('ehe','eHealth',language)]]</span> <span id="eHealthStatus"
                                                                                                                       class="ehealth-connection-status pending"></span></p>
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
                            <!-- Generator: Adobe Illustrator 22.1.0, SVG Export Plug-In  -->
                            <svg version="1.1"
						xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:a="http://ns.adobe.com/AdobeSVGViewerExtensions/3.0/"
						x="0px" y="0px" width="204px" height="81px" viewBox="0 0 204 81" style="enable-background:new 0 0 204 81;"
						xml:space="preserve" class="logo">
							<style type="text/css">
							.st0 {
                                fill: #AAA49D;
                            }

                            .st1 {
                                fill: #F7F5F2;
                            }

                            .st2 {
                                fill: url(#SVGID_1_);
                            }

                            .st3 {
                                fill: url(#SVGID_2_);
                            }

                            .st4 {
                                fill: url(#SVGID_3_);
                            }

                            .st5 {
                                fill: url(#SVGID_4_);
                            }

                            .st6 {
                                fill: url(#SVGID_5_);
                            }

                            .st7 {
                                fill: url(#SVGID_6_);
                            }

                            .st8 {
                                fill: url(#SVGID_7_);
                            }

                            .st9 {
                                opacity: 0.1;
                                fill: #FFFFFF;
                            }

                            .st10 {
                                fill: #FFFFFF;
                            }

                            .st11 {
                                fill: url(#SVGID_8_);
                            }

                            .st12 {
                                fill: url(#SVGID_9_);
                            }

                            .st13 {
                                fill: url(#SVGID_10_);
                            }

                            .st14 {
                                fill: url(#SVGID_11_);
                            }

                            .st15 {
                                fill: url(#SVGID_12_);
                            }

                            .st16 {
                                fill: url(#SVGID_13_);
                            }

                            .st17 {
                                fill: url(#SVGID_14_);
                            }
							</style>
							<defs>
							</defs>
							<g>

							<g class="logo-text">
								<path class="st1" d="M11,32.9V31h13.8v1.9H19v17.2h-2.1V32.9H11z"/>
								<path class="st1" d="M44.9,40.7c0,3.4-0.5,5.9-1.6,7.4c-1.1,1.5-3.1,2.3-5.9,2.3c-2.9,0-4.8-0.8-5.9-2.3c-1.1-1.6-1.6-4-1.6-7.4
									c0-3.4,0.6-5.8,1.7-7.5c1.1-1.6,3.1-2.4,5.9-2.4s4.8,0.8,5.9,2.4C44.4,34.8,44.9,37.3,44.9,40.7z M32.1,40.6
									c0,2.8,0.4,4.8,1.1,6c0.7,1.2,2.2,1.8,4.3,1.8c2.1,0,3.5-0.6,4.2-1.8c0.7-1.2,1.1-3.2,1.1-6c0-2.8-0.4-4.9-1.1-6.1
									c-0.8-1.3-2.2-1.9-4.2-1.9c-2.1,0-3.5,0.6-4.2,1.9C32.4,35.8,32.1,37.8,32.1,40.6z"/>
								<path class="st1" d="M60.1,43.6h-4.8v6.4h-2.1V31h7c2.1,0,3.6,0.5,4.6,1.5c1,1,1.5,2.6,1.5,4.6C66.2,41.5,64.2,43.6,60.1,43.6z
										M55.3,41.8h4.8c2.6,0,3.9-1.5,3.9-4.6c0-1.5-0.3-2.5-0.9-3.2c-0.6-0.7-1.6-1-3-1h-4.8V41.8z"/>
								<path class="st1" d="M71.5,50.1L76.8,31h4.6l5.2,19.1h-2.1l-1.4-4.9H75l-1.4,4.9H71.5z M78.4,32.8l-2.9,10.4h7.2l-2.9-10.4H78.4
									z"/>
								<path class="st1" d="M93.2,32.9V31h12.4v2.5l-10,14.2v0.6h10v1.9H93.2v-2.5l9.9-14.2v-0.6H93.2z"/>
							</g>

								<g class="logo-icn">
										<image style="overflow:visible;opacity:0.75;" width="591" height="516" xlink:href="data:image/png;base64,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
								A/g/AQYAWn1IrbECLuYAAAAASUVORK5CYII=" transform="matrix(0.1062 0 0 0.1062 131.6836 13.1354)">
										</image>
										<path class="st1" d="M184.3,65.5h-42.4l-8.5-14.7l21.2-36.8h17l21.2,36.8L184.3,65.5z"/>
									</g>
									<linearGradient id="SVGID_1_" gradientUnits="userSpaceOnUse" x1="148.1011" y1="38.3404" x2="185.7405" y2="38.3404">
										<stop  offset="0" style="stop-color:#FFEB3B"/>
										<stop  offset="0.41" style="stop-color:#FFC107"/>
										<stop  offset="0.6602" style="stop-color:#FFA000"/>
										<stop  offset="1" style="stop-color:#FF5722"/>
									</linearGradient>
									<polygon class="st2" points="163,22.7 145,54 181.1,54 			"/>
									<linearGradient id="SVGID_2_" gradientUnits="userSpaceOnUse" x1="163.0366" y1="23.8974" x2="163.0366" y2="12.5291">
										<stop  offset="0" style="stop-color:#FFEB3B"/>
										<stop  offset="0.41" style="stop-color:#FFC107"/>
										<stop  offset="0.6602" style="stop-color:#FFA000"/>
										<stop  offset="1" style="stop-color:#FF5722"/>
									</linearGradient>
									<polygon class="st3" points="155.1,15.1 163,22.7 170.9,15.1 			"/>
									<linearGradient id="SVGID_3_" gradientUnits="userSpaceOnUse" x1="150.2408" y1="52.1868" x2="138.346" y2="57.7094">
										<stop  offset="0" style="stop-color:#FFEB3B"/>
										<stop  offset="0.41" style="stop-color:#FFC107"/>
										<stop  offset="0.6602" style="stop-color:#FFA000"/>
										<stop  offset="1" style="stop-color:#FF5722"/>
									</linearGradient>
									<polygon class="st4" points="142.4,64.5 145,54 134.5,50.8 			"/>

										<linearGradient id="SVGID_4_" gradientUnits="userSpaceOnUse" x1="-309.7088" y1="52.0368" x2="-321.6036" y2="57.5594" gradientTransform="matrix(-1 0 0 1 -134.1993 0)">
										<stop  offset="0" style="stop-color:#FFEB3B"/>
										<stop  offset="0.41" style="stop-color:#FFC107"/>
										<stop  offset="0.6602" style="stop-color:#FFA000"/>
										<stop  offset="1" style="stop-color:#FF5722"/>
									</linearGradient>
									<polygon class="st5" points="183.6,64.5 181.1,54 191.5,50.8 			"/>
									<linearGradient id="SVGID_5_" gradientUnits="userSpaceOnUse" x1="156.989" y1="41.1935" x2="143.8198" y2="32.2724">
										<stop  offset="0" style="stop-color:#FFEB3B"/>
										<stop  offset="0.41" style="stop-color:#FFC107"/>
										<stop  offset="0.6602" style="stop-color:#FFA000"/>
										<stop  offset="1" style="stop-color:#FF5722"/>
									</linearGradient>
									<polygon class="st6" points="155.1,15.1 134.5,50.8 145,54 163,22.7 			"/>
									<linearGradient id="SVGID_6_" gradientUnits="userSpaceOnUse" x1="171.3042" y1="37.8811" x2="182.4556" y2="32.3585">
										<stop  offset="0" style="stop-color:#FFEB3B"/>
										<stop  offset="0.41" style="stop-color:#FFC107"/>
										<stop  offset="0.6602" style="stop-color:#FFA000"/>
										<stop  offset="1" style="stop-color:#FF5722"/>
									</linearGradient>
									<polygon class="st7" points="191.5,50.8 170.9,15.1 163,22.7 181.1,54 			"/>
									<linearGradient id="SVGID_7_" gradientUnits="userSpaceOnUse" x1="163.0366" y1="49.6695" x2="163.0366" y2="63.5449">
										<stop  offset="0" style="stop-color:#FFEB3B"/>
										<stop  offset="0.41" style="stop-color:#FFC107"/>
										<stop  offset="0.6602" style="stop-color:#FFA000"/>
										<stop  offset="1" style="stop-color:#FF5722"/>
									</linearGradient>
									<polygon class="st8" points="145,54 142.4,64.5 183.6,64.5 181.1,54 			"/>
									<polygon class="st9" points="170.9,15.1 155.1,15.1 139.7,41.9 145,54 176.8,46.6 180.6,31.8 			"/>
									<path class="st10" d="M163,22.7c-0.1-0.1-0.3-0.2-0.4-0.3l-0.4-0.4c-0.3-0.2-0.5-0.5-0.8-0.7c-0.5-0.5-1-1-1.5-1.5l-1.5-1.5
										l-1.5-1.5l0,0l1.6,1.5l1.6,1.5c0.5,0.5,1.1,1,1.6,1.5c0.3,0.3,0.5,0.5,0.8,0.8l0.4,0.4C162.8,22.4,162.9,22.6,163,22.7L163,22.7z
										"/>
									<path class="st10" d="M163,22.7c0.1-0.1,0.2-0.3,0.4-0.4l0.4-0.4c0.2-0.3,0.5-0.5,0.8-0.8c0.5-0.5,1-1,1.6-1.5l1.6-1.5l1.6-1.5
										l0,0l-1.5,1.5l-1.5,1.5c-0.5,0.5-1,1-1.5,1.5c-0.3,0.2-0.5,0.5-0.8,0.7l-0.4,0.4C163.3,22.5,163.2,22.6,163,22.7L163,22.7z"/>
									<path class="st10" d="M163.1,22.7c-0.4,0.9-0.9,1.7-1.3,2.5c-0.4,0.8-0.9,1.7-1.4,2.5c-0.9,1.7-1.9,3.3-2.9,4.9l-2.9,4.9
										l-2.9,4.9l0,0l2.8-5l2.8-5c0.9-1.7,1.8-3.3,2.8-5c0.5-0.8,1-1.6,1.5-2.4C162,24.3,162.5,23.5,163.1,22.7L163.1,22.7z"/>
									<path class="st10" d="M163.1,22.7c0.5,0.8,1,1.6,1.5,2.4c0.5,0.8,1,1.6,1.5,2.4c1,1.6,1.9,3.3,2.8,5l2.8,5l2.8,5l0,0l-2.9-4.9
										l-2.9-4.9c-1-1.6-2-3.3-2.9-4.9c-0.5-0.8-0.9-1.7-1.4-2.5C163.9,24.4,163.4,23.6,163.1,22.7L163.1,22.7z"/>
								</g>

							</svg>
                            <div class="timer" id="timer">[[localize('time_lft','Time left:',language)]] {{disconnectionTimer}} m.</div>

                            <paper-menu-button horizontal-align="right" close-on-activate no-overlap no-animations focused="false">
                                <paper-icon-button icon="icons:more-vert" slot="dropdown-trigger" alt="menu"></paper-icon-button>
                                <paper-listbox class="extra-menu" slot="dropdown-content" stop-keyboard-event-propagation>
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
                        </div>
                    </app-toolbar>

                </app-header>
                <iron-pages
                        selected="[[view]]"
                        attr-for-selected="name"
                        fallback-selection="view404"
                        role="main">
                    <ht-main name="main" api="[[api]]" user="[[user]]" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]">
                        <splash-screen-tz></splash-screen-tz>
                    </ht-main>
                    <ht-pat name="pat" api="[[api]]" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" user="[[user]]" route="{{subroute}}"
                            on-user-saved="_userSaved">
                        <splash-screen-tz></splash-screen-tz>
                    </ht-pat>
                    <ht-hcp name="hcp" api="[[api]]" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" user="[[user]]">
                        <splash-screen-tz></splash-screen-tz>
                    </ht-hcp>
                    <ht-msg name="msg" api="[[api]]" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" user="[[user]]">
                        <splash-screen-tz></splash-screen-tz>
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
                <paper-button dialog-dismiss>[[localize('can','Cancel',language)]]</paper-button>
                <paper-button dialog-confirm autofocus on-tap="confirmUserInvitation">[[localize('invite','Invite',language)]]</paper-button>
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
        return 'ht-app-tz'
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
                } else {
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

                this.api.loadUsersAndHcParties()
                if (this.$.api.crypto().RSA.loadKeyPairNotImported(u.healthcarePartyId)) {
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
            action: "loadEhboxMessage",
            hcpartyBaseApi: this.api.crypto().hcpartyBaseApi,
            fhcHost: this.api.fhc().host,
            fhcHeaders: JSON.stringify(this.api.fhc().headers),
            iccHost: this.api.host,
            iccHeaders: JSON.stringify(this.api.headers),
            tokenId: this.api.tokenId,
            keystoreId: this.api.keystoreId,
            user: this.user,
            ehpassword: this.credentials.ehpassword,
            boxId: "INBOX",
            keyPair: keyPair
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

customElements.define(HtAppTz.is, HtAppTz)
