package org.taktik.icure.services.external.rest.v1.mapper.gui.layout

import org.mapstruct.Mapper
import org.taktik.icure.services.external.rest.v1.dto.gui.Editor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.ActionButton
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.Audiometry
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.CheckBoxEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.DashboardEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.DateTimeEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.HealthcarePartyEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.IntegerSliderEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.Label
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.MeasureEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.MedicationEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.MedicationTableEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.NumberEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.PopupMenuEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.SchemaEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.StringEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.StringTableEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.StyledStringEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.SubFormEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.TokenFieldEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.editor.TypeValueStringEditor
import org.taktik.icure.services.external.rest.v1.dto.gui.layout.FormLayout

@Mapper(componentModel = "spring")
interface FormLayoutMapper {
	fun map(formLayoutDto: FormLayout):org.taktik.icure.dto.gui.layout.FormLayout
	fun map(formLayout: org.taktik.icure.dto.gui.layout.FormLayout):FormLayout

    fun map(editorDto: ActionButton): org.taktik.icure.dto.gui.editor.ActionButton
    fun map(editorDto: Audiometry): org.taktik.icure.dto.gui.editor.Audiometry
    fun map(editorDto: CheckBoxEditor): org.taktik.icure.dto.gui.editor.CheckBoxEditor
    fun map(editorDto: DashboardEditor): org.taktik.icure.dto.gui.editor.DashboardEditor
    fun map(editorDto: DateTimeEditor): org.taktik.icure.dto.gui.editor.DateTimeEditor
    fun map(editorDto: HealthcarePartyEditor): org.taktik.icure.dto.gui.editor.HealthcarePartyEditor
    fun map(editorDto: IntegerSliderEditor): org.taktik.icure.dto.gui.editor.IntegerSliderEditor
    fun map(editorDto: Label): org.taktik.icure.dto.gui.editor.Label
    fun map(editorDto: MeasureEditor): org.taktik.icure.dto.gui.editor.MeasureEditor
    fun map(editorDto: MedicationEditor): org.taktik.icure.dto.gui.editor.MedicationEditor
    fun map(editorDto: MedicationTableEditor): org.taktik.icure.dto.gui.editor.MedicationTableEditor
    fun map(editorDto: NumberEditor): org.taktik.icure.dto.gui.editor.NumberEditor
    fun map(editorDto: PopupMenuEditor): org.taktik.icure.dto.gui.editor.PopupMenuEditor
    fun map(editorDto: SchemaEditor): org.taktik.icure.dto.gui.editor.SchemaEditor
    fun map(editorDto: StringEditor): org.taktik.icure.dto.gui.editor.StringEditor
    fun map(editorDto: StringTableEditor): org.taktik.icure.dto.gui.editor.StringTableEditor
    fun map(editorDto: StyledStringEditor): org.taktik.icure.dto.gui.editor.StyledStringEditor
    fun map(editorDto: SubFormEditor): org.taktik.icure.dto.gui.editor.SubFormEditor
    fun map(editorDto: TokenFieldEditor): org.taktik.icure.dto.gui.editor.TokenFieldEditor
    fun map(editorDto: TypeValueStringEditor): org.taktik.icure.dto.gui.editor.TypeValueStringEditor

    fun map(editorDto: Editor):org.taktik.icure.dto.gui.Editor {
        return when (editorDto) {
            is ActionButton -> map(editorDto)
            is Audiometry -> map(editorDto)
            is CheckBoxEditor -> map(editorDto)
            is DashboardEditor -> map(editorDto)
            is DateTimeEditor -> map(editorDto)
            is HealthcarePartyEditor -> map(editorDto)
            is IntegerSliderEditor -> map(editorDto)
            is Label -> map(editorDto)
            is MeasureEditor -> map(editorDto)
            is MedicationEditor -> map(editorDto)
            is MedicationTableEditor -> map(editorDto)
            is NumberEditor -> map(editorDto)
            is PopupMenuEditor -> map(editorDto)
            is SchemaEditor -> map(editorDto)
            is StringEditor -> map(editorDto)
            is StringTableEditor -> map(editorDto)
            is StyledStringEditor -> map(editorDto)
            is SubFormEditor -> map(editorDto)
            is TokenFieldEditor -> map(editorDto)
            is TypeValueStringEditor -> map(editorDto)
            else -> throw IllegalArgumentException("Unsupported filter class")
        }
    }

    fun map(editor: org.taktik.icure.dto.gui.editor.ActionButton) : ActionButton
    fun map(editor: org.taktik.icure.dto.gui.editor.Audiometry) : Audiometry
    fun map(editor: org.taktik.icure.dto.gui.editor.CheckBoxEditor) : CheckBoxEditor
    fun map(editor: org.taktik.icure.dto.gui.editor.DashboardEditor) : DashboardEditor
    fun map(editor: org.taktik.icure.dto.gui.editor.DateTimeEditor) : DateTimeEditor
    fun map(editor: org.taktik.icure.dto.gui.editor.HealthcarePartyEditor) : HealthcarePartyEditor
    fun map(editor: org.taktik.icure.dto.gui.editor.IntegerSliderEditor) : IntegerSliderEditor
    fun map(editor: org.taktik.icure.dto.gui.editor.Label) : Label
    fun map(editor: org.taktik.icure.dto.gui.editor.MeasureEditor) : MeasureEditor
    fun map(editor: org.taktik.icure.dto.gui.editor.MedicationEditor) : MedicationEditor
    fun map(editor: org.taktik.icure.dto.gui.editor.MedicationTableEditor) : MedicationTableEditor
    fun map(editor: org.taktik.icure.dto.gui.editor.NumberEditor) : NumberEditor
    fun map(editor: org.taktik.icure.dto.gui.editor.PopupMenuEditor) : PopupMenuEditor
    fun map(editor: org.taktik.icure.dto.gui.editor.SchemaEditor) : SchemaEditor
    fun map(editor: org.taktik.icure.dto.gui.editor.StringEditor) : StringEditor
    fun map(editor: org.taktik.icure.dto.gui.editor.StringTableEditor) : StringTableEditor
    fun map(editor: org.taktik.icure.dto.gui.editor.StyledStringEditor) : StyledStringEditor
    fun map(editor: org.taktik.icure.dto.gui.editor.SubFormEditor) : SubFormEditor
    fun map(editor: org.taktik.icure.dto.gui.editor.TokenFieldEditor) : TokenFieldEditor
    fun map(editor: org.taktik.icure.dto.gui.editor.TypeValueStringEditor) : TypeValueStringEditor

    fun map(editor: org.taktik.icure.dto.gui.Editor): Editor {
        return when (editor) {
            is org.taktik.icure.dto.gui.editor.ActionButton -> map(editor)
            is org.taktik.icure.dto.gui.editor.Audiometry -> map(editor)
            is org.taktik.icure.dto.gui.editor.CheckBoxEditor -> map(editor)
            is org.taktik.icure.dto.gui.editor.DashboardEditor -> map(editor)
            is org.taktik.icure.dto.gui.editor.DateTimeEditor -> map(editor)
            is org.taktik.icure.dto.gui.editor.HealthcarePartyEditor -> map(editor)
            is org.taktik.icure.dto.gui.editor.IntegerSliderEditor -> map(editor)
            is org.taktik.icure.dto.gui.editor.Label -> map(editor)
            is org.taktik.icure.dto.gui.editor.MeasureEditor -> map(editor)
            is org.taktik.icure.dto.gui.editor.MedicationEditor -> map(editor)
            is org.taktik.icure.dto.gui.editor.MedicationTableEditor -> map(editor)
            is org.taktik.icure.dto.gui.editor.NumberEditor -> map(editor)
            is org.taktik.icure.dto.gui.editor.PopupMenuEditor -> map(editor)
            is org.taktik.icure.dto.gui.editor.SchemaEditor -> map(editor)
            is org.taktik.icure.dto.gui.editor.StringEditor -> map(editor)
            is org.taktik.icure.dto.gui.editor.StringTableEditor -> map(editor)
            is org.taktik.icure.dto.gui.editor.StyledStringEditor -> map(editor)
            is org.taktik.icure.dto.gui.editor.SubFormEditor -> map(editor)
            is org.taktik.icure.dto.gui.editor.TokenFieldEditor -> map(editor)
            is org.taktik.icure.dto.gui.editor.TypeValueStringEditor -> map(editor)
            else -> throw IllegalArgumentException("Unsupported filter class")
        }
    }
}
