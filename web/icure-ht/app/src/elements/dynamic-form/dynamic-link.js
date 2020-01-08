import {PolymerElement, html} from '@polymer/polymer';
class DynamicLink extends TkLocalizerMixin(PolymerElement) {
  static get template() {
    return html`
        <style include="icpc-styles">
            paper-menu-button {
                padding: 0;
                color: grey;
            }

            paper-icon-button {
                padding: 0 4px 8px 4px;
                width: 20px;
                height: 20px;
            }

            .link .colour-code span {
                content: '';
                display: inline-block;
                height: 6px;
                width: 6px;
                border-radius: 3px;
                margin-right: 3px;
                margin-bottom: 1px;
            }

            paper-item {
                font-size: 14px;
                min-height: 30px;
            }

            .dropdown_icon{
                height: 12px;
            }

            .status_icon{
                height: 8px;
            }

            .stat_act{
                color: var( --paper-green-400);
            }

            .stat_pass_rev{
                color: var(--paper-orange-400);
            }

            .stat_pass_n_rev{
                color: var( --paper-red-400);
            }

            .stat_not_pres{
                color: var(--paper-grey-400);
            }


        </style>


        <template is="dom-if" if="[[linkables]]">

            <paper-menu-button id="mb" horizontal-align="left" dynamic-align="true" allow-outside-scroll="">
                <paper-icon-button id="hc_menu" class="form-title-bar-btn" icon="link" slot="dropdown-trigger" alt="menu" on-tap="_show"></paper-icon-button>
                <paper-listbox slot="dropdown-content">
                    <template is="dom-repeat" items="[[linkables]]" as="he">
                        <paper-item id="[[he.id]]" class="link" on-tap="link"><label class\$="colour-code [[he.colour]]"><span></span></label>[[he.descr]]</paper-item>
                    </template>
                </paper-listbox>
            </paper-menu-button>

            <paper-menu-button id="stat" horizontal-align="left" dynamic-align="true" allow-outside-scroll="">
                <paper-icon-button id="stat_menu" class="form-title-bar-btn" icon="more-horiz" slot="dropdown-trigger" alt="menu" on-tap="_show"></paper-icon-button>
                <paper-listbox slot="dropdown-content">
                    <paper-item id="plan_act" class="link" on-tap="showPlanActionForm">
                        <iron-icon icon="vaadin:flag" class="dropdown_icon"></iron-icon>
                        [[localize('plan_act','Plan an action',language)]]
                    </paper-item>
                </paper-listbox>
                <paper-listbox slot="dropdown-content">
                    <paper-item id="stat_act" class="link" on-tap="_setStatus">
                        <iron-icon icon="vaadin:circle" class="status_icon stat_act"></iron-icon>
                        [[localize('stat_act','Plan an action',language)]]
                    </paper-item>
                </paper-listbox>
                <paper-listbox slot="dropdown-content">
                    <paper-item id="stat_pas_rev" class="link" on-tap="_setStatus">
                        <iron-icon icon="vaadin:circle" class="status_icon stat_pass_rev"></iron-icon>
                        [[localize('stat_pas_rev','Plan an action',language)]]
                    </paper-item>
                </paper-listbox>
                <paper-listbox slot="dropdown-content">
                    <paper-item id="stat_pas_n_rev" class="link" on-tap="_setStatus">
                        <iron-icon icon="vaadin:circle" class="status_icon stat_pass_n_rev"></iron-icon>
                        [[localize('stat_pas_n_rev','Plan an action',language)]]
                    </paper-item>
                </paper-listbox>
                <paper-listbox slot="dropdown-content">
                    <paper-item id="stat_n_pres" class="link" on-tap="_setStatus">
                        <iron-icon icon="vaadin:circle" class="status_icon stat_not_pres"></iron-icon>
                        <del>[[localize('stat_exclude','Plan an action',language)]]</del>
                    </paper-item>
                </paper-listbox>
            </paper-menu-button>
        </template>
`;
  }

  static get is() {
      return 'dynamic-link';
  }

  static get properties() {
      return {
          api: {
              type: Object
          },
          linkables: {
              type: Array
          },
          representedObject: {
              type: Object
          }

      };
  }

  static get observers() {
      return [];
  }

  ready() {
      super.ready();
  }


  _show(e) {
      e.preventDefault();
      e.stopPropagation();

      if(e.target.id === "stat_menu"){
          this.root.querySelector('#stat').open();
      }else{
          this.root.querySelector('#mb').open();
      }

  }

  link(e, target) {
      const he = this.linkables && this.linkables.find(he => he.id === e.target.id) || null
      if (!he) {
          return;
      }
      this.dispatchEvent(new CustomEvent('link-to-health-element', { bubbles: true, composed: true, detail: { representedObject: this.representedObject, healthElement: he } }));
  }

  showPlanActionForm(e){

      this.dispatchEvent(new CustomEvent('dynamically-event', { bubbles: true, composed: true, detail: { representedObject: this.representedObject} }));
  }

  _setStatus(e){
      console.log("set status: "+e.target.id)

      this.dispatchEvent(new CustomEvent('status-changed', { bubbles: true, composed: true, detail: { representedObject: this.representedObject, status: e.target.id} }));
  }

  _getCssColorStatus(){
/*
      const services = this.api.contact().services(this.currentContact, this.representedObject)
      let status = ""

      if(services[0] && services[0].status) {
          status = services[0].status
      }

      return 'stat_act'
*/
  }
}
customElements.define(DynamicLink.is, DynamicLink);
