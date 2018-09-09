class FilterPanel extends Polymer.TkLocalizerMixin(Polymer.mixinBehaviors([Polymer.IronResizableBehavior], Polymer.Element)) {
  static get template() {
    return Polymer.html`
		<style>

			paper-card {
				width: calc(100% - 64px);
				margin: 0 32px 32px;

			}

			.pat-details-card > .card-content {
				padding: 16px 16px 32px !important;
			}

			.filters-panel {
				background: var(--app-light-color);
				@apply --padding-right-left-32;
				overflow: hidden;
				max-height: 80%;
				@apply --transition;
			}

			.filters-panel--collapsed {
				max-height: 0;
			}

			.filters-bar {
				background: var(--app-secondary-color);
				height: 40px;
				text-align: center;
				padding: 4px 0;
				width: var(--panel-width, 100%);
			}

			.hide-filters-btn {
				height: 40px;
				margin: 0 auto;
				width: 100%;
				font-size: 12px;
				font-weight: 500;
			}

			.hide-filters-btn:hover {
				background: var(--app-dark-color-faded);
				@apply --transition;
			}

			.show-filters-btn {
				height: 40px;
				font-size: 12px;
				font-weight: 500;
			}

			.show-filters-btn:hover {
				background: var(--app-dark-color-faded);
				@apply --transition;
			}

			.filters-bar--small {
				display: flex;
				flex-direction: row;
				justify-content: space-between;
				align-items: center;
				margin: 0 32px;

			}

			.filters-bar--small-icon.selected {
				color: var(--app-primary-color-dark);
			}

			.filters-bar--small-icon{
				color: var(--app-text-color-disabled);
			}

			paper-item.iron-selected {
				background-color: var(--app-primary-color);
				color: var(--app-text-color-light);
			}

			paper-input.search-input {
				--paper-input-container-color: var(--app-text-color-disabled);
				--paper-input-container-focus-color: var(--app-primary-color);
				--paper-input-container-input-color: var(--app-text-color);
			}

			.search-icon {
				height: 20px;
				width: 20px;
				color: var(--app-text-color);
			}

			.clear-search-button-icon {
				height: 26px;
				width: 26px;
				padding: 2px;
				margin-bottom: 6px;
				color: var(--app-text-color);
			}

			@media screen and (max-width:1025px){
				.filters-bar--small{
					margin: 0 16px;
				}
			}
		</style>

		<div id="filtersPanel" is="dom-if" class="filters-panel filters-panel--collapsed">
			<paper-input id="searchInput" label="[[localize('sch','Search',language)]]" class="search-input" value="{{searchString}}">
				<iron-icon class="search-icons" icon="icons:search" prefix=""></iron-icon>
				<paper-icon-button suffix="" on-click="clearInput" icon="clear" alt="clear" title="clear" class="clear-search-button-icon"></paper-icon-button>
			</paper-input>
			<paper-listbox selected-values="[[_selectedFilterIndexes(selectedFilters, selectedFilters.*)]]" focused="" on-selected-items-changed="selectMenu" multi="">
				<template id="filterPanelMenu" is="dom-repeat" items="[[items]]" as="menu">
					<paper-item>
						<iron-icon class="filters-panel-icon" icon="[[menu.icon]]"></iron-icon>
						[[itemTitle(menu)]]
					</paper-item>
				</template>
			</paper-listbox>
		</div>
		<div class="filters-bar">
			<paper-button is="dom-if" hidden\$="{{!showFiltersPanel}}" class="hide-filters-btn" on-tap="toggleFiltersPanel" name="hide-filters" role="button" tabindex="0" aria-disabled="false" elevation="0">
				[[localize('hid_fil','Hide Filters',language)]]
				<iron-icon icon="icons:expand-less"></iron-icon>
			</paper-button>
			<div is="dom-if" hidden\$="{{showFiltersPanel}}">
				<div class="filters-bar--small">
					<div>
						<template id="filterPanelIcons" is="dom-repeat" items="[[visibleItems]]" as="menu">
							<paper-icon-button id="[[menu.id]]-btn" class\$="filters-bar--small-icon [[_isFilterSelected(menu.filter,selectedFilters,selectedFilters.*)]]" icon="[[menu.icon]]" on-tap="_filterSelected"></paper-icon-button>
						</template>
					</div>
					<paper-button class="show-filters-btn" on-tap="toggleFiltersPanel" name="show-filters" role="button" tabindex="0" aria-disabled="false" elevation="0">
						[[localize('sho_fil','Show Filters',language)]]
						<iron-icon icon="icons:expand-more"></iron-icon>
					</paper-button>
				</div>
			</div>
		</div>
		<div>
			<template is="dom-repeat" items="[[visibleItems]]" as="menu">
				<paper-tooltip for="[[menu.id]]-btn">[[iconTitle(icon)]]</paper-tooltip>
			</template>
		</div>
`;
  }

  static get is() {
      return 'filter-panel';
	}

  static get properties() {
      return {
          items: {
              type: Array,
              value: []
          },
          visibleItems: {
              type: Array,
              computed: "computeVisibleItems(panelWidth,items)"
          },
          showFiltersPanel: {
              type: Boolean,
              value: false
          },
          panelWidth: {
              type: Number,
              value: 200
          },
          searchString: {
              type: String,
              notify: true,
              value: null
          },
          selectedFilters: {
              type: Object,
              notify: true,
              value: null
          }
      };
	}

  ready() {
      super.ready();
      this.addEventListener('iron-resize', () => this.onWidthChange());
	}

  attached() {
      super.attached();
      this.async(this.notifyResize, 1);
	}

  onWidthChange() {
      this.set('panelWidth', this.parentElement.offsetWidth - Array.from(this.parentElement.children).filter(el => el !== this).map(x => x.offsetWidth).reduce((sum, w) => sum + w, 0));
	}

  refreshIcons() {
      this.onWidthChange();
	}

  computeVisibleItems(width, items) {
      return items.filter((it, idx) => it.icon && idx * 40 < width - 255);
	}

  clearInput() {
      this.$.searchInput.value = "";
	}

  toggleFiltersPanel() {
      this.showFiltersPanel = !this.showFiltersPanel;
      this.root.querySelector('#filtersPanel').classList.toggle('filters-panel--collapsed');
	}

  iconTitle(icon){
	    return icon.title[this.language]
	}

  itemTitle(item){
	    return item.title[this.language]
	}

  _isFilterSelected(filter) {
	    return this.selectedFilters.includes(filter) ? 'selected' : ''
	}

  _filterSelected(e) {
	    const filter = this.$.filterPanelIcons.modelForElement(e.target).menu.filter
	    const currentIndex = this.selectedFilters.indexOf(filter)
      if (currentIndex >= 0) {
          this.splice('selectedFilters', currentIndex, 1)
      } else {
          this.push('selectedFilters', filter)
      }
	}

  selectMenu(e) {
      if(e.detail.value.length !== 0) {
          this.set('selectedFilters',e.detail.value.map(
              val => this.$.filterPanelMenu.modelForElement(val).menu.filter
        	))
      } else {
          this.set('selectedFilters',[])
      }
	}

  _selectedFilterIndexes(){
	    return this.selectedFilters ? this.selectedFilters.map(f=> this.items.map(i=>i.filter).indexOf(f)) : []

	}
}

customElements.define(FilterPanel.is, FilterPanel);
