const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<custom-style>
	<style is="custom-style">
		html {
		--app-primary-color: var(--paper-blue-grey-700);
		--app-primary-color-light: #718792;
		--app-primary-color-dark: #1c313a;
		--app-secondary-color:#ffb300;
		--app-secondary-color-dark: #ff8f00;
		--app-secondary-color-highlight:#ffd740;

		--app-error-color: #E53935;

		--app-status-color-ok: #07f87f;
		--app-status-color-pending: #fcdf35;
		--app-status-color-nok: #ff4d4d;

		--app-text-color: rgba(0, 0, 0, 0.70);
		--app-text-color-disabled: rgba(0, 0, 0, 0.40);
		--app-text-color-light: rgba(255, 255, 255, 1);

		--app-light-color: rgba(255,255,255,1);
		--app-light-color-faded: rgba(255,255,255,.2);

		--app-dark-color-faded: rgba(0,0,0,0.05);

		--app-background-color-darker: #d6d7d8;
		--app-background-color-dark: #eceef0;
		--app-background-color: var(--paper-grey-50);
		--app-background-color-light: rgba(252,252,252,1);

		--app-shadow-elevation-1: 	0 0px 2px 0 rgba(0,0,0,0.14),
									0 2px 2px 0 rgba(0,0,0,0.12),
									0 1px 3px 0 rgba(0,0,0,0.20);

		--app-shadow-elevation-2: 	0 2px 4px 0 rgba(0,0,0,0.14),
									0 3px 4px 0 rgba(0,0,0,0.12),
									0 1px 5px 0 rgba(0,0,0,0.20);
	}

	</style>
</custom-style>`;

document.head.appendChild($_documentContainer.content);
