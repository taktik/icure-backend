/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v2.mapper.gui.layout

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.dto.gui.type.*
import org.taktik.icure.dto.gui.type.Array
import org.taktik.icure.dto.gui.type.primitive.*
import org.taktik.icure.services.external.rest.v2.dto.gui.Column
import org.taktik.icure.services.external.rest.v2.dto.gui.Editor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.ActionButton
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.Audiometry
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.CheckBoxEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.DashboardEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.DateTimeEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.HealthcarePartyEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.IntegerSliderEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.Label
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.MeasureEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.MedicationEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.MedicationTableEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.NumberEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.PopupMenuEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.SchemaEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.StringEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.StringTableEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.StyledStringEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.SubFormEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.TokenFieldEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.editor.TypeValueStringEditor
import org.taktik.icure.services.external.rest.v2.dto.gui.layout.FormLayout
import org.taktik.icure.services.external.rest.v2.dto.gui.type.Data
import org.taktik.icure.services.external.rest.v2.mapper.embed.ServiceV2Mapper

@Mapper(componentModel = "spring", uses = [ServiceV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class FormLayoutV2Mapper {
	abstract fun map(formLayoutDto: FormLayout): org.taktik.icure.dto.gui.layout.FormLayout
	abstract fun map(formLayout: org.taktik.icure.dto.gui.layout.FormLayout): FormLayout

	abstract fun map(columnDto: Column): org.taktik.icure.dto.gui.Columnabstract fun map(editorDto: ActionButton): org.taktik.icure.dto.gui.editor.ActionButton
	abstract fun map(editorDto: Audiometry): org.taktik.icure.dto.gui.editor.Audiometry
	abstract fun map(editorDto: CheckBoxEditor): org.taktik.icure.dto.gui.editor.CheckBoxEditor
	abstract fun map(editorDto: DashboardEditor): org.taktik.icure.dto.gui.editor.DashboardEditor
	abstract fun map(editorDto: DateTimeEditor): org.taktik.icure.dto.gui.editor.DateTimeEditor
	abstract fun map(editorDto: HealthcarePartyEditor): org.taktik.icure.dto.gui.editor.HealthcarePartyEditor
	abstract fun map(editorDto: IntegerSliderEditor): org.taktik.icure.dto.gui.editor.IntegerSliderEditor
	abstract fun map(editorDto: Label): org.taktik.icure.dto.gui.editor.Label
	abstract fun map(editorDto: MeasureEditor): org.taktik.icure.dto.gui.editor.MeasureEditor
	abstract fun map(editorDto: MedicationEditor): org.taktik.icure.dto.gui.editor.MedicationEditor
	abstract fun map(editorDto: MedicationTableEditor): org.taktik.icure.dto.gui.editor.MedicationTableEditor
	abstract fun map(editorDto: NumberEditor): org.taktik.icure.dto.gui.editor.NumberEditor
	abstract fun map(editorDto: PopupMenuEditor): org.taktik.icure.dto.gui.editor.PopupMenuEditor
	abstract fun map(editorDto: SchemaEditor): org.taktik.icure.dto.gui.editor.SchemaEditor
	abstract fun map(editorDto: StringEditor): org.taktik.icure.dto.gui.editor.StringEditor
	abstract fun map(editorDto: StringTableEditor): org.taktik.icure.dto.gui.editor.StringTableEditor
	abstract fun map(editorDto: StyledStringEditor): org.taktik.icure.dto.gui.editor.StyledStringEditor
	abstract fun map(editorDto: SubFormEditor): org.taktik.icure.dto.gui.editor.SubFormEditor
	abstract fun map(editorDto: TokenFieldEditor): org.taktik.icure.dto.gui.editor.TokenFieldEditor
	abstract fun map(editorDto: TypeValueStringEditor): org.taktik.icure.dto.gui.editor.TypeValueStringEditor

	fun map(editorDto: Editor): org.taktik.icure.dto.gui.Editor = when (editorDto) {
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

	abstract fun map(column: org.taktik.icure.dto.gui.Column): Columnabstract fun map(editor: org.taktik.icure.dto.gui.editor.ActionButton): ActionButton
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.Audiometry): Audiometry
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.CheckBoxEditor): CheckBoxEditor
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.DashboardEditor): DashboardEditor
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.DateTimeEditor): DateTimeEditor
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.HealthcarePartyEditor): HealthcarePartyEditor
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.IntegerSliderEditor): IntegerSliderEditor
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.Label): Label
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.MeasureEditor): MeasureEditor
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.MedicationEditor): MedicationEditor
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.MedicationTableEditor): MedicationTableEditor
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.NumberEditor): NumberEditor
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.PopupMenuEditor): PopupMenuEditor
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.SchemaEditor): SchemaEditor
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.StringEditor): StringEditor
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.StringTableEditor): StringTableEditor
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.StyledStringEditor): StyledStringEditor
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.SubFormEditor): SubFormEditor
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.TokenFieldEditor): TokenFieldEditor
	abstract fun map(editor: org.taktik.icure.dto.gui.editor.TypeValueStringEditor): TypeValueStringEditor

	fun map(editor: org.taktik.icure.dto.gui.Editor): Editor = when (editor) {
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

	abstract fun map(data: org.taktik.icure.services.external.rest.v2.dto.gui.type.Array): Array
	abstract fun map(data: org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.AttributedString): AttributedString
	abstract fun map(data: org.taktik.icure.services.external.rest.v2.dto.gui.type.Dictionary): Dictionary
	abstract fun map(data: org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.Label): org.taktik.icure.dto.gui.type.primitive.Label
	abstract fun map(data: org.taktik.icure.services.external.rest.v2.dto.gui.type.Measure): Measure
	abstract fun map(data: org.taktik.icure.services.external.rest.v2.dto.gui.type.MedicationTable): MedicationTable
	abstract fun map(data: org.taktik.icure.services.external.rest.v2.dto.gui.type.MenuOption): MenuOption
	abstract fun map(data: org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.PrimitiveBoolean): PrimitiveBoolean
	abstract fun map(data: org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.PrimitiveDate): PrimitiveDate
	abstract fun map(data: org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.PrimitiveNumber): PrimitiveNumber
	abstract fun map(data: org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.PrimitiveString): PrimitiveString
	abstract fun map(data: org.taktik.icure.services.external.rest.v2.dto.gui.type.Schema): Schema
	abstract fun map(data: org.taktik.icure.services.external.rest.v2.dto.gui.type.StringTable): StringTable

	fun map(data: Data): org.taktik.icure.dto.gui.type.Data = when (data) {
		is org.taktik.icure.services.external.rest.v2.dto.gui.type.Array -> map(data)
		is org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.AttributedString -> map(data)
		is org.taktik.icure.services.external.rest.v2.dto.gui.type.Dictionary -> map(data)
		is org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.Label -> map(data)
		is org.taktik.icure.services.external.rest.v2.dto.gui.type.Measure -> map(data)
		is org.taktik.icure.services.external.rest.v2.dto.gui.type.MedicationTable -> map(data)
		is org.taktik.icure.services.external.rest.v2.dto.gui.type.MenuOption -> map(data)
		is org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.PrimitiveBoolean -> map(data)
		is org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.PrimitiveDate -> map(data)
		is org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.PrimitiveNumber -> map(data)
		is org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.PrimitiveString -> map(data)
		is org.taktik.icure.services.external.rest.v2.dto.gui.type.Schema -> map(data)
		is org.taktik.icure.services.external.rest.v2.dto.gui.type.StringTable -> map(data)
		else -> throw IllegalArgumentException("Unsupported data class")
	}

	abstract fun map(data: Array): org.taktik.icure.services.external.rest.v2.dto.gui.type.Array
	abstract fun map(data: AttributedString): org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.AttributedString
	abstract fun map(data: Dictionary): org.taktik.icure.services.external.rest.v2.dto.gui.type.Dictionary
	abstract fun map(data: org.taktik.icure.dto.gui.type.primitive.Label): org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.Label
	abstract fun map(data: Measure): org.taktik.icure.services.external.rest.v2.dto.gui.type.Measure
	abstract fun map(data: MedicationTable): org.taktik.icure.services.external.rest.v2.dto.gui.type.MedicationTable
	abstract fun map(data: MenuOption): org.taktik.icure.services.external.rest.v2.dto.gui.type.MenuOption
	abstract fun map(data: PrimitiveBoolean): org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.PrimitiveBoolean
	abstract fun map(data: PrimitiveDate): org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.PrimitiveDate
	abstract fun map(data: PrimitiveNumber): org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.PrimitiveNumber
	abstract fun map(data: PrimitiveString): org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive.PrimitiveString
	abstract fun map(data: Schema): org.taktik.icure.services.external.rest.v2.dto.gui.type.Schema
	abstract fun map(data: StringTable): org.taktik.icure.services.external.rest.v2.dto.gui.type.StringTable

	fun map(data: org.taktik.icure.dto.gui.type.Data): Data = when (data) {
		is Array -> map(data)
		is AttributedString -> map(data)
		is Dictionary -> map(data)
		is org.taktik.icure.dto.gui.type.primitive.Label -> map(data)
		is Measure -> map(data)
		is MedicationTable -> map(data)
		is MenuOption -> map(data)
		is PrimitiveBoolean -> map(data)
		is PrimitiveDate -> map(data)
		is PrimitiveNumber -> map(data)
		is PrimitiveString -> map(data)
		is Schema -> map(data)
		is StringTable -> map(data)
		else -> throw IllegalArgumentException("Unsupported data class")
	}
}
