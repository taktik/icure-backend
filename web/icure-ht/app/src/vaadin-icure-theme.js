const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="vaadin-checkbox-style" theme-for="vaadin-checkbox">
    <template>
        <style>
            :host([checked]) [part="checkbox"] {
                background-color: var(--app-secondary-color) !important;
            }
        </style>
    </template>
</dom-module><dom-module id="vaadin-text-field-style" theme-for="vaadin-text-field">
    <template>
        <style>
            :host([focused]:not([invalid])) [part=label] {
                color: var(--app-primary-color) !important;
            }
            [part="input-field"]::after, [part="input-field"]::before{
                background-color: var(--app-primary-color);
            }
        </style>
    </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
