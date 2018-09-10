import '../qrcode-manager/qrcode-printer.js';
import {PolymerElement, html} from '@polymer/polymer';
import {mixinBehaviors} from '@polymer/polymer/lib/legacy/class'
import {IronResizableBehavior} from '@polymer/iron-resizable-behavior/iron-resizable-behavior.js';

class HtExportKey extends TkLocalizerMixin(mixinBehaviors([IronResizableBehavior], PolymerElement)) {
  static get template() {
    return html`
        <style>
            paper-dialog {
                width: 60%;
                display:flex;
                flex-flow: row wrap;
                justify-content: space-between;
                align-items: flex-start;
            }
            #dialog{
                overflow: auto;
            }
            div {
                width : 100%;
            }
            vadin-grid {
                width : 100%;
            }
        </style>
        <paper-dialog id="dialog" opened="{{opened}}">
            <div>
                <vaadin-grid items="[[logList]]">
                    <vaadin-grid-column>
                        <template class="header">
                            Prestataires
                        </template>
                        <template>
                            [[item.user]]
                        </template>
                    </vaadin-grid-column>
                    <vaadin-grid-column>
                        <template class="header">
                            Patient
                        </template>
                        <template>
                            [[item.patient]]
                        </template>
                    </vaadin-grid-column>
                    <vaadin-grid-column>
                        <template class="header">
                            Date d'accès
                        </template>
                        <template>
                            [[convertDate(item.date)]]
                        </template>
                    </vaadin-grid-column>
                </vaadin-grid>
            </div>
        </paper-dialog>
`;
  }

  static get is() {
      return 'ht-access-log';
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
          opened: {
              type: Boolean,
              value: false
          },
          logList: {
              type : Array,
              value : []
          },
          listPatient :{
              type : Array,
              value : []
          }
      };
  }

  open() {
      this.$.dialog.open();

      this.api.patient().listPatients(this.user.healthcarePartyId).then(patients => {
          let promises = [];
          let logListTemp = [];
          this.api.user().listUsers().then( users => {
              users.rows.map(user =>{
                  promises.push(this.api.accesslog().findByUserAfterDate(user.id, 'USER_ACCESS', +new Date() - 1000 * 3600 * 24 * 7, null, null, 1000, true).then(results => {
                      results.rows.map(result => {
                          let patient = patients.rows.find(pat => result.patientId===pat.id)
                          if(patient)
                              logListTemp.push({
                                  date: result.date,
                                  patient: patient.lastName + " " + patient.firstName,
                                  user: user.name
                              })
                      })
                  }))
              })
          }).finally(x =>{
              Promise.all(promises).then(y => {
                  logListTemp.sort( (a,b) =>{
                      return b.date -a.date;
                  })
                  this.set("logList",logListTemp)
              })
          })
      })
  }

  convertDate(date){
      return this.api.moment(date).format("DD/MM/YYYY")
  }
}
customElements.define(HtExportKey.is, HtExportKey);
