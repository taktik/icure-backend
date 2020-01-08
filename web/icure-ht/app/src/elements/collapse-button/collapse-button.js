import {PolymerElement, html} from '@polymer/polymer';

class CollapseButton extends PolymerElement {
  static get template() {
    return html`
    <style is="custom-style">
      /* You could do your own style def here */
      .sublist {
        padding-left: 30px;
      }
    </style>
    <!-- This slot is your main item. The one the users click to get to the submenu -->
    <slot name="sublist-collapse-item" on-tap="toggle"></slot>
    <iron-collapse class="sublist" id="collapse" horizontal\$="[[horizontal]]" no-animation\$="[[noAnimation]]" opened\$="[[opened]]">
      <!-- This slot does not have a name, therefor everything you will add in the collapse-button will go here and will be in the iron-collapse element -->
      <slot></slot>
    </iron-collapse>
`;
  }

  static get is() {
    return 'collapse-button';
  }
  static get properties() {
    // Support for some of the iron-collapse attributes
    // Check their source code for more information.
    return {
      horizontal: {
        type: Boolean,
        value: false,
        reflectToAttribute: true
      },
      noAnimation: {
        type: Boolean,
        value: false,
        reflectToAttribute: true
      },
      opened: {
        type: Boolean,
        value: false,
        reflectToAttribute: true,
        notify: true
      }
    };
  }
  constructor() {
    super();
    this.unselectable = 'on';
  }
  toggle(e) {
    e && e.stopPropagation();
    // This is what makes your submenu hidden or visible.
    this.opened = !this.opened;
  }
}
window.customElements.define(CollapseButton.is, CollapseButton);
