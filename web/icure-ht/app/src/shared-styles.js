const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="shared-styles">
	<template>
		<style is="custom-style">
			:host {
				--padding-32: {
					padding: 32px;
				};

				--padding-16: {
					padding: 16px;
				};

				--padding-right-left-32:{
					padding-right: 32px;
					padding-left: 32px;
				};
				--padding-right-left-24: {
					padding-right: 24px;
					padding-left: 24px;
				};
				--padding-right-left-16:{
					padding-right: 16px;
					padding-left: 16px;
				};
				--padding-right-left-8: {
					padding-right: 8px;
					padding-left: 8px;
				};
				--padding-left-8: {
					padding-left: 8px;
				};
				--padding-left-16: {
					padding-left: 16px;
				};

				--transition: {
					transition: all 0.28s cubic-bezier(0.4, 0, 0.2, 1);
				};

				--text-shadow: {
					text-shadow:0 1px 1px rgba(0,0,0,0.4);
				};

				--form-font-size: 14px;
			}

			::selection{
				background: var(--app-secondary-color);
			}

			::-moz-selection{
				background: var(--app-secondary-color);
			}

			*:focus{
				outline: 0!important;
			}


			.page-title {
				@apply --paper-font-display2;
			}

			@media (max-width: 600px) {
				.page-title {
					font-size: 24px!important;
				}
			}
			.scrollable {
				box-sizing: border-box;
				height: 100%;
				padding: 10px 0;
				overflow-y: auto;
			}
			paper-listbox {
				background: #fff;
			}
			iron-pages {
				height:100%;
				padding: 0;
				margin: 0;
			}
			section {
				height:100%;
				padding: 0;
				margin: 0;
			}
			menu-bar paper-button {
				padding: 0;
			}
			menu-bar a {
				color: #fff;
				text-decoration: none;
				height: 48px;
				display: block;
				vertical-align: middle;
				padding: 15px;
				box-sizing: border-box;
			}

			paper-listbox.menu-content {padding-left: 30px;}
			.view {height: 100%;}
			.tree {
				width: 30%;
				min-width: 300px;
				padding-right: 20px;
				height: 100%;
			}
			paper-material {margin:0; height:calc(100% - 30px); background: #fcfcfc;}
			.assets>paper-material, .network>paper-material, .settings>paper-material {
				width: calc(100% - 30px);
			}
			.assets>paper-material paper-material {
				height:auto;
				margin: 10px;
				width: calc(100% - 30px);
				box-sizing: border-box;
				padding: 5px;
			}
			device-list .logo img {
				width: 120px;
			}

			.tree paper-listbox {background: transparent;}
			.tree paper-item {background: red;}
			.channels {
				height: 100%;
			}


			.menu-tree paper-item {
				@apply --shadow-elevation-4dp;
				width: 180px;
				margin: 10px;
			}
			.menu-tree .menu-content {
				padding: 0 0 0 20px;
			}
			.side {
				/*padding-left: 10px;
				box-sizing: border-box;*/
				background: #f6F6F6;
			}

			paper-material.side {height: 100%;padding: 0px 5px 0px 5px;width:100%;margin:0;box-sizing: border-box;}
			.side paper-material{height: auto;padding: 0px 5px 0px 5px;margin:10px 15px 10px 5px;width: 96%;}
			.side paper-toolbar {background: #fff;color:#555;}

			.media {
				width: 120px;
				height: 90px;
				position: relative;
				@apply --shadow-elevation-4dp;
				margin: 5px;
				background: #555;
				color:#ddd;
				border: 3px solid transparent;
				font-size: .8em;
			}


			.media .logo{
				width: 100%;
				height: 70px;
				position: relative;
				border-bottom: 1px solid rgba(220,220,220,0.5);
			}
			.media img {
				position: absolute;
				top:50%;
				left:50%;
				transform: translate(-50%, -50%);
				max-width: 90%;
				max-height: 90%;
			}
			.media .name {
				text-align:center;
				padding-top: 2px;
				font-size: .9em;
			}
			.media.iron-selected, .media.selected {
				border: 3px solid #12c6fc;
				border-radius: 3px;
			}

			.sortable-ghost {
				opacity: .2;
			}
			.inherited #menu {
				opacity: 0.6;
			}

			#selectChannelDialog {
				width: 70%;
				height: 80%;
			}


			.circle {
				display: inline-block;

				width: 64px;
				height: 64px;

				text-align: center;

				color: #555;
				border-radius: 50%;
				background: #ddd;

				font-size: 30px;
				line-height: 64px;
			}

			h1 {
				margin: 16px 0;

				color: #212121;

				font-size: 22px;
			}
		</style>
	</template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);

/* shared styles for all elements and index.html */
/*
  FIXME(polymer-modulizer): the above comments were extracted
  from HTML and may be out of place here. Review them and
  then delete this comment!
*/
;
