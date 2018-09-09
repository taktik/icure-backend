class MenuBar extends Polymer.mixinBehaviors([Polymer.IronMenubarBehavior], Polymer.Element) {
  static get template() {
    return Polymer.html`
		<style>
			.content ::slotted(iron-selector) {
				color: white;
				text-decoration: none;
				height:36px;
				@apply --paper-font-subhead;
			}

			.content ::slotted(paper-button.iron-selected)  {
				color:  var(--app-text-color-light);
				/*background: rgba(255,255,255, var(--light-disabled-opacity));*/
				border-bottom:1px solid var(--app-text-color-light-faded);
				border-radius:0;
			}
			.content ::slotted(*) {
				display: inline-block;
			}

			.content ::slotted(*:focus){
				outline:0;
			}

			.content ::slotted(*) iron-icon{
				height:20px;
				width:20px;
				margin-right:8px;
			}
		</style>

		<div class="content">
			<slot></slot>
		</div>
`;
  }

  static get is() {
      return 'menu-bar';
	}

  static get properties() {
      return {};
	}

  constructor() {
      super();
	}
}

customElements.define(MenuBar.is, MenuBar);
