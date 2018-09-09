import '../dynamic-form/ckmeans-grouping.js';
import '../dynamic-form/dynamic-form.js';

class HtPatDetailList extends Polymer.TkLocalizerMixin(Polymer.Element) {
  static get template() {
    return Polymer.html`
        <style>
            .form-title-bar-btn {
                height: 20px;
                width: 20px;
                padding: 2px;
            }
            .horizontal vaadin-date-picker {
                height: 90px;
                padding-bottom: 0px;
                @apply --padding-right-left-16
            }


            .link .ICD-10 span {
                content: '';
                display: inline-block;
                height: 6px;
                width: 6px;
                border-radius: 3px;
                margin-right: 3px;
                margin-bottom: 1px;
            }

            paper-listbox {
                min-width: 200px;
            }

            paper-menu-button {
                padding: 0;
            }

            vaadin-grid{
                height: unset;
				max-height:calc(100% - 104px);
				margin: 32px 32px 0;
				box-shadow: var(--app-shadow-elevation-1);
				border: none;
                border-radius: 3px;
                overflow-y: scroll;
			}

            
        </style>
        <vaadin-grid id="dynamic-list" size="10" multi-sort="[[multiSort]]" active-item="{{activeItem}}" items="[[_services(contacts,contacts.*)]]" on-tap="click">
            <vaadin-grid-column flex-grow="2" width="80px">
                <template class="header">
                    <vaadin-grid-sorter path="label">[[localize('lab','Label',language)]]</vaadin-grid-sorter>
                </template>
                <template>[[item.label]]</template>
            </vaadin-grid-column>
            <vaadin-grid-column flex-grow="1">
                <template class="header">
                    <vaadin-grid-sorter path="function{[[_name()]]}">Name</vaadin-grid-sorter>
                </template>
                <template>[[_name()]]</template>
            </vaadin-grid-column>
            <vaadin-grid-column flex-grow="1">
                <template class="header">
                    <vaadin-grid-sorter path="modified">[[localize('dat','Date',language)]]</vaadin-grid-sorter>
                </template>
                <template>[[_date(item)]]</template>
            </vaadin-grid-column>
            <vaadin-grid-column flex-grow="4">
                <template class="header">
                    Value
                </template>
                <template>[[_shortDescription(item)]]</template>
            </vaadin-grid-column>
            <vaadin-grid-column flex-grow="2" width="80px">
                <template class="header">
                    <vaadin-grid-sorter path="function{[[_author(item)]]}">[[localize('aut','Author',language)]]</vaadin-grid-sorter>
                </template>
                <template>[[_author(item)]]</template>
            </vaadin-grid-column>
        </vaadin-grid>
`;
  }

  static get is() {
      return 'ht-pat-detail-list';
  }

  static get properties() {
      return {
          api: {
              type: Object
          },
          user: {
              type: Object
          },
          patient: {
              type: Object,
              value: null
          },
          contact: {
              type: Object,
              value: null
          },
          contacts: {
              type: Array,
              value: []
          },
          activeItem: {
              type: Object
          },
          healthElements: {
              type: Array,
              value: function () {
                  return [];
              }
          },
          currentContact: {
              type: Object,
              value: null
          },
          lastColumnSort: {
              type: String,
              value: null
          }
      };
  }

  constructor() {
      super();
  }

  _isNotInCurrentContact(currentContact, contact) {
      return currentContact === null || contact !== currentContact;
  }

  _services(contacts) {
      return _.sortBy(_.flatMap(contacts, c => c.services), ['modified']);
  }

  _date(item) {
      return item ? this.api.moment(item.modified).format(item > 99991231 ? 'DD/MM/YYYY HH:mm' : 'DD/MM/YYYY') : '';
  }

  _author(item) {
      return this.api.getAuthor(item.author);
  }

  _shortDescription(svc) {
      return this.api.contact().shortServiceDescription(svc, this.language);
  }
}

customElements.define(HtPatDetailList.is, HtPatDetailList);
