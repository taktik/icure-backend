import {PolymerElement, html} from '@polymer/polymer';
class DynamicMeasureField extends TkLocalizerMixin(PolymerElement) {
  static get template() {
    return html`
		<style>
			:host {
                position: relative;
                flex-grow: var(--dynamic-field-width, 25);
				min-width: calc(var(--dynamic-field-width-percent, '25%') - 32px);
				margin: 0 16px;
			}

            dynamic-link {
                position: absolute;
                right: 0;
				bottom: 8px;
            }

			.modified-icon {
				width: 18px;
			}

			.modified-previous-value {
				color: var(--app-text-color-disabled);
				text-decoration: line-through;
				font-style: italic;
			}

			.modified-before-out {
				color: var(--app-secondary-color-dark);
				text-align: right;
				float: right;
				font-style: italic;
				border-bottom: 1px dotted var(--app-secondary-color-dark);
			}

			.modified-after-out {
				color: var(--app-secondary-color-dark);
				text-align: right;
				float: right;
				font-style: italic;
				border-bottom: 1px dotted var(--app-secondary-color-dark);
			}

			paper-input-container {
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

			input{
				border:none;
				width: 100%;
				outline: 0;
				background:none;
				font-size: var(--form-font-size);

			}
		</style>

		<template is="dom-if" if="[[readOnly]]">
			<paper-input-container always-float-label="true">
				<label slot="label">[[label]]
					<template is="dom-if" if="[[wasModified]]">
						<span class="modified-before-out">[[localize('mod','modified',language)]] [[lastModified]] <iron-icon class="modified-icon" icon="schedule"></iron-icon></span>
					</template>
					<template is="dom-if" if="[[isModifiedAfter]]">
						<span class="modified-after-out">[[localize('obs_val','obsolete value',language)]]<iron-icon class="modified-icon" icon="report-problem"></iron-icon></span>
					</template>
				</label>
				<iron-input slot="input" bind-value="{{inputValue}}">
					<input value="{{inputValue::input}}" readonly="">
				</iron-input>
			</paper-input-container>
		</template>
		<template is="dom-if" if="[[!readOnly]]">
			<paper-input-container always-float-label="true">
				<label slot="label">[[label]]
                    <template is="dom-if" if="[[wasModified]]">
						<span class="modified-before-out">[[localize('mod','modified',language)]] [[lastModified]] <iron-icon class="modified-icon" icon="schedule"></iron-icon></span>
					</template>
					<template is="dom-if" if="[[isModifiedAfter]]">
						<span class="modified-after-out">[[localize('obs_val','obsolete value',language)]]<iron-icon class="modified-icon" icon="report-problem"></iron-icon></span>
					</template>
				</label>
				<iron-input slot="input" bind-value="{{inputValue}}">
					<input value="{{inputValue::input}}">
				</iron-input>
			</paper-input-container>
			<dynamic-link i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" linkables="[[linkables]]" represented-object="[[label]]" api="[[api]]"></dynamic-link>
        </template>
`;
  }

  static get is() {
      return 'dynamic-measure-field';
	}

  static get properties() {
      return {
          wasModified: {
              type: Boolean
          },
          isModifiedAfter: {
              type: Boolean
          },
          readOnly: {
              type: Boolean,
              value: false
          },
          lastModified: {
              type: String
          },
          label: {
              type: String
          },
          valueWithUnit: {
              type: Object,
              notify: true,
              observer: '_valueChanged'
          },
          inputValue: {
              type: String,
              observer: '_inputValueChanged'
          },
          width: {
              type: Number,
              value: 24,
              observer: '_widthChanged'
          }
      };
	}

  constructor() {
      super();
	}

  _widthChanged(width) {
      this.updateStyles({ '--dynamic-field-width': width, '--dynamic-field-width-percent': '' + width + '%' });
	}

  _valueChanged(value) {
      const normalizedInputValue = this.valueWithUnit ? (this.valueWithUnit.value != null ? this.valueWithUnit.value : '') + (this.valueWithUnit.unit ? ' ' + this.valueWithUnit.unit : '') : '';
      if ((this.inputValue || '').trim() !== normalizedInputValue) {
          this.set('inputValue', normalizedInputValue);
      }
	}

  _inputValueChanged(value) {
      if (this.inputValue !== this.value + ' ' + this.unit) {
          const match = /^ *([+-]?[0-9]+(?:[.,][0-9]*)?)(?: *([a-zA-Z°].*?))? *$/.exec(this.inputValue);
          if (!this.inputValue.match(/^ *([+-]?[0-9]+(?:[.,]0*))(?: *([a-zA-Z°].*?))? *$/) /*intermediate situation*/) {
                  this.set('valueWithUnit', {
                      value: match && (match[1] ? parseFloat(match[1].replace(/([0-9]),([0-9])/, "$1.$2")) : null) || (isNaN(parseFloat(this.inputValue)) ? null : parseFloat(this.inputValue)),
                      unit: match && match[2] || null
                  });
                  this.dispatchEvent(new CustomEvent('field-changed', { detail: { context: this.context, value: this.valueWithUnit } }));
              }
      }
	}
}

customElements.define(DynamicMeasureField.is, DynamicMeasureField);
