/**
@license
Copyright (c) 2016 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/
import './elements/ht-msg/ht-msg-detail.js';

import './elements/ht-msg/ht-msg-menu.js';
import './elements/ht-msg/ht-msg-list.js';
import './shared-styles.js';
import {PolymerElement, html} from '@polymer/polymer';
class HtMsg extends PolymerElement {
  static get template() {
    return html`
        <style include="shared-styles">
            :host {
                display: block;
                height: calc(100% - 20px);
                /*padding: 10px;*/
            }
            .container{
                width: 100%;
                height: calc(100vh - 64px);
                display: grid;
                grid-template-columns: 15% 35% 50%;
                grid-template-rows: 100%;
                position: fixed;
                top: 64px;
                left: 0;
                bottom: 0;
                right: 0;
            }
        </style>
        <div class="container">
            <ht-msg-menu api="[[api]]" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" user="[[user]]" on-selection-change="handleMenuChange"></ht-msg-menu>
            <ht-msg-list api="[[api]]" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" user="[[user]]" select-list="[[selectList]]" on-selection-messages-change="handleMessageChange"></ht-msg-list>
            <ht-msg-detail api="[[api]]" i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" user="[[user]]" select-message="[[selectMessage]]"></ht-msg-detail>
        </div>
`;
  }

  static get is() {
      return 'ht-msg';
  }

  static get properties() {
      return {
          api: {
              type: Object
          },
          user: {
              type: Object
          },
          selectList: {
              type: Object
          }
      };
  }

  constructor() {
      super();
  }

  ready() {
      super.ready()
  }

  handleMenuChange(e) {
      this.set('selectList', e.detail)
  }

  handleMessageChange(e) {
      this.set('selectMessage', e.detail)
  }
}

customElements.define(HtMsg.is, HtMsg);
