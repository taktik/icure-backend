import "polymer/polymer.html";
import "../../../../bower_components/paper-toolbar/paper-toolbar"
import "../../../../bower_components/paper-icon-button/paper-icon-button"
import "../../../../bower_components/iron-icons/iron-icons"
import "../../../../bower_components/iron-icons/editor-icons"
import "../../../../bower_components/paper-dropdown-menu/paper-dropdown-menu"
import "../../../../bower_components/paper-listbox/paper-listbox"
import "../../../../bower_components/paper-item/paper-item"
import "../../../../bower_components/neon-animation/web-animations"

import "../utils/color-picker.ts"
import "../utils/heading-picker.ts"
import "../utils/font-picker.ts"
import "../utils/font-size-picker.ts"


import './prose-editor.html'

import {customElement, property} from 'taktik-polymer-typescript';
import {keymap} from 'prosemirror-keymap'
import {EditorState, TextSelection, Transaction} from 'prosemirror-state'
import {EditorView} from 'prosemirror-view'
import {Schema, DOMParser, NodeSpec, Node, MarkType, MarkSpec, ParseRule, Mark} from 'prosemirror-model'
import {schema} from 'prosemirror-schema-basic'
import {baseKeymap, toggleMark, setBlockType} from "prosemirror-commands";
import {Plugin} from "prosemirror-state"
import {ReplaceStep} from "prosemirror-transform";
import {history, undo, redo} from "prosemirror-history";
import Element = Polymer.Element;
import {columnResizing, goToNextCell, tableEditing, TableMap, tableNodes} from "prosemirror-tables";



/**
 * MyApp main class.
 *
 */
@customElement('prose-editor')
export class ProseEditor extends Polymer.Element {
  $: { editor: HTMLElement, content: HTMLElement } | any

  @property({type: Number})
  pageHeight: number = 846

  @property({type: Number, observer: '_zoomChanged'})
  zoomLevel = 120

  _zoomChanged() {
    if (this.$.container) {
      this.$.container.style.transform = "translateX(-50%)  translateY(" + (this.zoomLevel - 100) / 2 + "%) scale(" + (this.zoomLevel / 100) + ")"
    }
  }

  docNodeSpec: NodeSpec = {
    content: "page+"
  }

  pageNodeSpec: NodeSpec = {
    inline: false,
    draggable: false,
    attrs: {
      id: {default: 0}
    },
    content: "block+",

    toDOM: (node: any) => ["div", {class: "page", id: "page_" + node.attrs.id}, 0],
    parseDOM: [{
      tag: "div.page", getAttrs(dom) {
        return (dom instanceof HTMLDivElement) && {
          id: parseInt(dom.getAttribute("id")!!.substr(5))
        } || {}
      }
    }]
  }

  tabNodeSpec: NodeSpec = {
    inline: true,
    group: "inline",
    draggable: false,

    toDOM: (node: any) => ["span", {style: "padding-left:100px", class: "tab"}],
    parseDOM: [{tag: "span.var", getAttrs(dom) { return {expr: (dom as HTMLElement).dataset.expr} }}]
  }

  varNodeSpec: NodeSpec = {
    inline: true,
    group: "inline",
    draggable: true,
    attrs: { expr: {default: ''} },

    toDOM: (node: any) => ["span", {style: "padding-left:100px", class: "var", "data-expr":node.attrs.expr}],
    parseDOM: [{
      tag: "span.tab"
    }]
  }

  @property({type: Object})
  editorSchema = new Schema({
    nodes: (schema.spec.nodes as any)
      .remove("doc").addToStart("page", this.pageNodeSpec).addToStart("doc", this.docNodeSpec)
      .update("paragraph", Object.assign((schema.spec.nodes as any).get("paragraph"), {
        attrs: { align: {default: 'inherit'} },
        parseDOM: [{tag: "p", getAttrs(value : HTMLElement) { return {align: value.style && value.style.textAlign || 'inherit'}}}],
        toDOM(node: any) { return ["p", {style: "text-align:"+(node.attrs.align || 'inherit')}, 0] }
      }))
      .update("heading", Object.assign((schema.spec.nodes as any).get("heading"), {
        attrs: Object.assign((schema.spec.nodes as any).get("heading").attrs, { align: {default: 'inherit'} }),
        parseDOM: (schema.spec.nodes as any).get("heading").parseDOM.map((r: ParseRule) => Object.assign(r, {getAttrs(value : HTMLElement) {
          return {level: parseInt(value.tagName.replace(/.+([0-9]+)/,'$1')), align: value.style && value.style.textAlign || 'inherit'}
        }})),
        toDOM(node: any) {
          return ["h" + node.attrs.level, {style: "text-align: "+(node.attrs.align || 'inherit')}, 0]
        }
      }).append(tableNodes({
        tableGroup: "block",
        cellContent: "block+",
        cellAttributes: {
          background: {
            default: null,
            getFromDOM(dom) { return (dom as HTMLElement).style.backgroundColor || null },
            setDOMAttr(value, attrs) { if (value) attrs.style = (attrs.style || "") + `background-color: ${value};` }
          }
        }
      })))
      .addBefore("image", "tab", this.tabNodeSpec),
    marks: (schema.spec.marks as any)
      .addToEnd("underlined", {
        attrs: {
          underline: {default: 'underline'}
        },
        parseDOM: [{tag: "u"}, {
          style: 'text-decoration',
          getAttrs(value:any) {
            return {underline: value}
          }
        }],
        toDOM(mark:Mark) {
          let {underline} = mark.attrs
          return ['span', {style: `text-decoration: ${underline || 'underline'}`}, 0]
        }
      }).addToEnd("color", {
        attrs: {
          color: {default: ''}
        },
        parseDOM: [
          {
            style: 'color',
            getAttrs(value:any) {
              return {color: value}
            }
          }
        ],
        toDOM(mark:Mark) {
          let {color} = mark.attrs
          return ['span', {style: `color: ${color}`}, 0]
        }
      }).addToEnd("bgcolor", {
        attrs: {
          color: {default: ''}
        },
        parseDOM: [
          {
            style: 'background',
            getAttrs(value: any) {
              return {color: value}
            }
          }
        ],
        toDOM(mark:Mark) {
          let {color} = mark.attrs
          return ['span', {style: `background: ${color}`}, 0]
        }
      }).addToEnd("font", {
        attrs: {
          font: {default: ''}
        },
        parseDOM: [
          {
            style: 'font-family',
            getAttrs(value:any) {
              return {font: value}
            }
          }
        ],
        toDOM(mark:Mark) {
          let {font} = mark.attrs
          return ['span', {style: `font-family: ${font}`}, 0]
        }
      }).addToEnd("size", {
        attrs: {
          size: {default: ''}
        },
        parseDOM: [
          {
            style: 'font-size',
            getAttrs(value:any) {
              return {size: value}
            }
          }
        ],
        toDOM(mark:Mark) {
          let {size} = mark.attrs
          return ['span', {style: `font-size: ${size}`}, 0]
        }
      }).addToEnd("var", {
        attrs: {
          expr: {default: ''}
        },
        parseDOM: [
          {
            tag: 'span.var',
            getAttrs(value:HTMLElement) {
              return {expr: value.dataset.expr}
            }
          }
        ],
        toDOM(mark:Mark) {
          let {expr} = mark.attrs
          return ['span', {class:'var', 'data-expr': expr}, 0]
        }
      })
  })

  @property({type: Object})
  editorView?: EditorView

  previousTimeout?: any

  layout() {
    const view = this.editorView
    if (view) {
      const state = view.state
      const pages: any[] = []
      state.doc.forEach((node: Node, offset: number) => pages.splice(pages.length, 0, {
        offset: offset,
        node: node
      }))
      for (const page of pages) {
        const pageDom = (view.domAtPos(0).node as Element).getElementsByClassName('page')[page.node.attrs.id] as HTMLElement
        const reverseSubNodes: any[] = []

        if (pageDom.offsetHeight > this.pageHeight) {
          page.node.forEach((node: Node, offset: number) => {
            reverseSubNodes.splice(0, 0, {
              offset: offset,
              node: node
            })
          })

          if (reverseSubNodes.length) {
            const subNode = reverseSubNodes[0];
            const start = page.offset + 1 + subNode.offset

            let tr = state.tr
            const nextPage = state.doc.nodeAt(page.offset + page.node.nodeSize)
            if (!nextPage) {
              tr = tr.insert(page.offset + page.node.nodeSize, page.node.type.create({id: (page.node.attrs.id || 0) + 1}))
            }
            tr.insert(page.offset + page.node.nodeSize + 1, subNode.node).delete(start, start + subNode.node.nodeSize)

            const pos = tr.doc.resolve(tr.steps[tr.steps.length - 1].getMap().map(page.offset + page.node.nodeSize + 1 + subNode.node.nodeSize))
            //tr.setSelection(new TextSelection(pos, pos))

            view.dispatch(tr.scrollIntoView());
          }
        }
      }
    }
  }

  ready() {
    super.ready()

    const proseEditor = this

    let selectionTrackingPlugin = new Plugin({
      view(view) {
        return {
          update: function (view, prevState) {
            var state = view.state;

            if (!(prevState && prevState.doc.eq(state.doc) && prevState.selection.eq(state.selection))) {
              let {$anchor, $cursor} = state.selection as TextSelection, index = $anchor.pos
              let node = state.doc.nodeAt(index)

              if (!node && !$cursor) {
                proseEditor.set('currentHeading', null)
                proseEditor.set('currentFont', null)
                proseEditor.set('currentSize', null)
                return
              }

              if (node && node.type.name === 'heading') {
                proseEditor.set('currentHeading', 'Heading ' + node.attrs.level)
              } else {
                proseEditor.set('currentHeading', 'Normal')
              }
              const marks = (node && node.marks || $cursor && $cursor.marks() || [])


              let {from, to} = state.selection
              let align : string | null = null
              state.doc.nodesBetween(from, to, (node, pos) => {
                if ((node.type === proseEditor.editorSchema.nodes.paragraph || node.type === proseEditor.editorSchema.nodes.heading) && node.attrs.align != align) {
                  align = node.attrs.align
                }
              })

              const fontMark = marks.find(m => m.type === proseEditor.editorSchema.marks.font)
              const sizeMark = marks.find(m => m.type === proseEditor.editorSchema.marks.size)
              const strongMark = marks.find(m => m.type === proseEditor.editorSchema.marks.strong)
              const emMark = marks.find(m => m.type === proseEditor.editorSchema.marks.em)
              const underlinedMark = marks.find(m => m.type === proseEditor.editorSchema.marks.underlined)
              const colorMark = marks.find(m => m.type === proseEditor.editorSchema.marks.color)
              const bgcolorMark = marks.find(m => m.type === proseEditor.editorSchema.marks.bgcolor)
              const varMark = marks.find(m => m.type === proseEditor.editorSchema.marks.var)

              proseEditor.set('currentFont', fontMark && fontMark.attrs.font || 'Roboto')
              proseEditor.set('currentSize', sizeMark && sizeMark.attrs.size || '11px')
              proseEditor.set('isStrong', !!strongMark )
              proseEditor.set('isEm', !!emMark )
              proseEditor.set('isUnderlined', !!underlinedMark )
              proseEditor.set('currentColor', colorMark && colorMark.attrs.color || '#000000' )
              proseEditor.set('currentBgColor', bgcolorMark && bgcolorMark.attrs.color || '#000000' )
              proseEditor.set('isCode',  varMark && varMark.attrs.expr &&  varMark && varMark.attrs.expr.length)
              proseEditor.set('codeExpression',  varMark && varMark.attrs.expr || '')

              proseEditor.set('isLeft', align && align === 'left')
              proseEditor.set('isCenter',  align && align === 'center' )
              proseEditor.set('isRight',  align && align === 'right' )
              proseEditor.set('isJustify',  align && align === 'justify' )
            }
          }
        }
      }
    });

    let paginationPlugin = new Plugin({
      appendTransaction(tr, oldState, newState) {
        setTimeout(() => proseEditor.layout(), 0)
        if (oldState.doc.childCount > newState.doc.childCount && tr[0].steps[0] instanceof ReplaceStep) {
          const loc: number = (tr[0].steps[0] as any).from
          const lastNode = newState.doc.nodeAt(loc)
          if (lastNode && lastNode.type.name === 'paragraph') {
            return newState.tr.join(loc)
          }
        }
        return null
      },
      props: {
        //nodeViews: { page(node, view, getPos, decorations) { return new PageView(node, view, getPos, decorations) } }
      }
    });

    let paragraphPlugin = new Plugin({
      view: ((view: EditorView) => {
        return {
          update: (view, state) => {
            view.state.doc.descendants((n, pos, parent) => {
              if (n.type == view.state.schema.nodes.paragraph) {
                const p = view.domAtPos(pos).node as HTMLParagraphElement
                Array.from(p.getElementsByClassName('tab')).forEach(span => {
                  if (span instanceof HTMLSpanElement) {
                    const prev = span.previousSibling
                    const delta = prev && (prev instanceof HTMLSpanElement) && prev.classList.contains('tab') ? 1 : 0
                    const desiredPadding = (200 - (span.offsetLeft + delta - p!!.offsetLeft) % 200) + 'px'
                    if (desiredPadding !== span.style.paddingLeft) {
                      span.style.paddingLeft = desiredPadding
                    }
                  }
                })
              } else if (n.type == view.state.schema.nodes.tab) {
              }
              return true
            })
            return true
          },
          destroy: () => {
          }
        }
      })
    });

    let state = EditorState.create({
      doc: DOMParser.fromSchema(this.editorSchema).parse(this.$.content),
      plugins: [
        keymap({
          "Tab": (state: EditorState, dispatch: any, editorView: EditorView) => {
            let tabType = this.editorSchema.nodes.tab
            let {$from} = state.selection, index = $from.index()
            if (!$from.parent.canReplaceWith(index, index, this.editorSchema.nodes.tab))
              return false
            if (dispatch)
              dispatch(state.tr.replaceSelectionWith(tabType.create()))
            return true
          }
        }),
        keymap(baseKeymap),
        history(),
        columnResizing({}),
        tableEditing(),
        keymap({
          "Tab": goToNextCell(1),
          "Shift-Tab": goToNextCell(-1)
        }),
        selectionTrackingPlugin,
        paginationPlugin,
        paragraphPlugin
      ]
    })

    this.editorView = new EditorView(this.$.editor, {
      state: state
    })

  }

  doUndo(e: CustomEvent) {
    e.stopPropagation()
    e.preventDefault()
    if (this.editorView) {
      undo(this.editorView.state, this.editorView.dispatch)
      this.editorView.focus()
    }
  }


  doRedo(e: CustomEvent) {
    e.stopPropagation()
    e.preventDefault()
    if (this.editorView) {
      redo(this.editorView.state, this.editorView.dispatch)
      this.editorView.focus()
    }
  }


  toggleBold(e: Event) {
    e.stopPropagation()
    e.preventDefault()
    if (this.editorView) {
      toggleMark(this.editorSchema.marks.strong, {})(this.editorView.state, this.editorView.dispatch)
      this.editorView.focus()
    }
  }

  toggleItalic(e: Event) {
    e.stopPropagation()
    e.preventDefault()
    if (this.editorView) {
      toggleMark(this.editorSchema.marks.em, {})(this.editorView.state, this.editorView.dispatch)
      this.editorView.focus()
    }
  }

  toggleUnderlined(e: Event) {
    e.stopPropagation()
    e.preventDefault()
    if (this.editorView) {
      toggleMark(this.editorSchema.marks.underlined, {})(this.editorView.state, this.editorView.dispatch)
      this.editorView.focus()
    }
  }

  doClear(e: Event) {
    e.stopPropagation()
    e.preventDefault()
    if (this.editorView) {
      this.clearMarks()(this.editorView.state, this.editorView.dispatch)
      this.editorView.focus()
    }
  }

  doColor(e: CustomEvent) {
    e.stopPropagation()
    e.preventDefault()
    if (this.editorView) {
      this.addMark(this.editorSchema.marks.color, {color: e.detail.color})(this.editorView.state, this.editorView.dispatch)
      this.editorView.focus()
    }
  }

  doFillColor(e: CustomEvent) {
    e.stopPropagation()
    e.preventDefault()
    if (this.editorView) {
      this.addMark(this.editorSchema.marks.bgcolor, {color: e.detail.color})(this.editorView.state, this.editorView.dispatch)
      this.editorView.focus()
    }
  }

  doFont(e: CustomEvent) {
    e.stopPropagation()
    e.preventDefault()
    if (this.editorView && e.detail && e.detail.value && e.detail.value.length) {
      this.addMark(this.editorSchema.marks.font, {font: e.detail.value})(this.editorView.state, this.editorView.dispatch)
      this.editorView.focus()
    }
  }

  doSize(e: CustomEvent) {
    e.stopPropagation()
    e.preventDefault()
    if (this.editorView && e.detail && e.detail.value && e.detail.value.length) {
      this.addMark(this.editorSchema.marks.size, {size: e.detail.value.replace(/ /, '')})(this.editorView.state, this.editorView.dispatch)
      this.editorView.focus()
    }
  }

  doHeading(e: CustomEvent) {
    e.stopPropagation()
    e.preventDefault()
    if (this.editorView && e.detail && e.detail.value && e.detail.value.length) {
      setBlockType(this.editorSchema.nodes.heading, {level: parseInt(e.detail.value.replace(/.+ ([0-9]+)/, '$1'))})(this.editorView.state, this.editorView.dispatch)
      this.editorView.focus()
    }
  }

  doLeft(e: CustomEvent) {
    e.stopPropagation()
    e.preventDefault()
    if (this.editorView) {
      this.setAlignment("left")(this.editorView.state, this.editorView.dispatch)
      this.editorView.focus()
    }
  }

  doCenter(e: CustomEvent) {
    e.stopPropagation()
    e.preventDefault()
    if (this.editorView) {
      this.setAlignment("center")(this.editorView.state, this.editorView.dispatch)
      this.editorView.focus()
    }
  }

  doRight(e: CustomEvent) {
    e.stopPropagation()
    e.preventDefault()
    if (this.editorView) {
      this.setAlignment("right")(this.editorView.state, this.editorView.dispatch)
      this.editorView.focus()
    }
  }

  doJustify(e: CustomEvent) {
    e.stopPropagation()
    e.preventDefault()
    if (this.editorView) {
      this.setAlignment("justify")(this.editorView.state, this.editorView.dispatch)
      this.editorView.focus()
    }
  }

  insertOrEditCode(e: CustomEvent) {
    e.stopPropagation()
    e.preventDefault()

  }

  setAlignment(align: String) {
    const proseEditor = this
    return function(state: EditorState, dispatch?: (tr: Transaction) => void)  {
      let {from, to} = state.selection
      let hasChange = false
      let tr = state.tr
      state.doc.nodesBetween(from, to, (node, pos) => {
        if ((node.type === proseEditor.editorSchema.nodes.paragraph || node.type === proseEditor.editorSchema.nodes.heading) && node.attrs.align != align) {
          tr = state.tr.setNodeMarkup(pos, node.type, Object.assign({}, node.attrs, {align : align}))
          hasChange = true
        }
      })
      if (hasChange && dispatch) dispatch(tr.scrollIntoView())
      return true
    }
  }

  addMark(markType: MarkType, attrs?: { [key: string]: any }): (state: EditorState, dispatch?: (tr: Transaction) => void) => boolean {
    return function (state, dispatch) {
      let {empty, $cursor, ranges} = state.selection as TextSelection
      if ((empty && !$cursor)) return false
      if (dispatch) {
        if ($cursor) {
          dispatch(state.tr.addStoredMark(markType.create(attrs)))
        } else {
          const tr = state.tr
          for (let i = 0; i < ranges.length; i++) {
            let {$from, $to} = ranges[i]
            tr.addMark($from.pos, $to.pos, markType.create(attrs))
          }
          dispatch(tr.scrollIntoView())
        }
      }
      return true
    }
  }

  clearMarks(): (state: EditorState, dispatch?: (tr: Transaction) => void) => boolean {
    return function (state, dispatch) {
      let {empty, $cursor, ranges} = state.selection as TextSelection
      if ((empty && !$cursor)) return false
      if (dispatch) {
        if ($cursor) {
          $cursor.marks().forEach(m => dispatch(state.tr.removeStoredMark(m)))
        } else {
          const tr = state.tr
          for (let i = 0; i < ranges.length; i++) {
            let {$from, $to} = ranges[i]
            if ($to.pos > $from.pos) state.doc.nodesBetween($from.pos, $to.pos, node => {
              node.marks.forEach(m => tr.removeMark($from.pos, $to.pos, m))
            })
          }
          dispatch(tr.scrollIntoView())
        }
      }
      return true
    }
  }

}


