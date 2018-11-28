import "../../../../bower_components/polymer/polymer"
import "../../../../bower_components/paper-menu-button/paper-menu-button"
import "../../../../bower_components/paper-button/paper-button"
import "../../../../bower_components/paper-item/paper-item"

import {customElement, property} from "taktik-polymer-typescript";
import './font-picker.html'


@customElement('font-picker')
export class FontPicker extends Polymer.mixinBehaviors([], Polymer.Element) {

  $: { editor: HTMLElement, content: HTMLElement } | any

  @property({type: String, notify: true, observer: '_fontChanged'})
  font?: string = ""

  @property({type: Array})
  fontList = ["Alegreya","Barlow","Barlow Condensed","Cardo","Crete Round","EB Garamond","Exo 2","Exo","Fjalla One","Great Vibes","Indie Flower","Josefin Sans","Kurale","Libre Baskerville","Lobster","Lora","Maven Pro","Monoton","Montserrat","Montserrat Alternates","Nanum Myeongjo","Neucha","Old Standard TT","Open Sans","Oswald","Pathway Gothic One","Poiret One","Poppins","Quattrocento","Quattrocento Sans","Quicksand","Raleway","Roboto","Roboto Condensed","Source Serif Pro","Spectral","Teko","Tinos","Vollkorn"]

  constructor() {
    super()
  }

  _onTap(event : any) {
    this.set('font', event.target.id)
    
    this.dispatchEvent(new CustomEvent('font-picker-selected', {detail: {value: this.font}, bubbles:true, composed:true} as EventInit) )

    this.$.fontMenuButton.opened = false
  }

  _font(font? : string) {
    return font && font.length ? font : "Font"
  }

  _fontChanged(font? : string) {
    this.$.fontButton.style.fontFamily = this.font ?  this.font : "Roboto"
  }

}


