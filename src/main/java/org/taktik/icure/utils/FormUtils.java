/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.utils;

import com.thoughtworks.xstream.XStream;
import org.taktik.icure.dto.gui.Code;
import org.taktik.icure.dto.gui.CodeType;
import org.taktik.icure.dto.gui.Editor;
import org.taktik.icure.dto.gui.FormAction;
import org.taktik.icure.dto.gui.FormDataOption;
import org.taktik.icure.dto.gui.FormLabel;
import org.taktik.icure.dto.gui.FormPlanning;
import org.taktik.icure.dto.gui.Formula;
import org.taktik.icure.dto.gui.LabelPosition;
import org.taktik.icure.dto.gui.SubForm;
import org.taktik.icure.dto.gui.Tag;
import org.taktik.icure.dto.gui.editor.ActionButton;
import org.taktik.icure.dto.gui.editor.CheckBoxEditor;
import org.taktik.icure.dto.gui.editor.DateTimeEditor;
import org.taktik.icure.dto.gui.editor.HealthcarePartyEditor;
import org.taktik.icure.dto.gui.editor.Label;
import org.taktik.icure.dto.gui.editor.MeasureEditor;
import org.taktik.icure.dto.gui.editor.MedicationEditor;
import org.taktik.icure.dto.gui.editor.MedicationTableEditor;
import org.taktik.icure.dto.gui.editor.NumberEditor;
import org.taktik.icure.dto.gui.editor.PopupMenuEditor;
import org.taktik.icure.dto.gui.editor.SchemaEditor;
import org.taktik.icure.dto.gui.editor.StringEditor;
import org.taktik.icure.dto.gui.editor.StringTableEditor;
import org.taktik.icure.dto.gui.editor.StyledStringEditor;
import org.taktik.icure.dto.gui.editor.SubFormEditor;
import org.taktik.icure.dto.gui.editor.TokenFieldEditor;
import org.taktik.icure.dto.gui.editor.Audiometry;
import org.taktik.icure.dto.gui.layout.FormLayout;
import org.taktik.icure.dto.gui.layout.FormLayoutData;
import org.taktik.icure.dto.gui.type.Array;
import org.taktik.icure.dto.gui.type.Data;
import org.taktik.icure.dto.gui.type.Dictionary;
import org.taktik.icure.dto.gui.type.Measure;
import org.taktik.icure.dto.gui.type.MedicationTable;
import org.taktik.icure.dto.gui.type.MenuOption;
import org.taktik.icure.dto.gui.type.Schema;
import org.taktik.icure.dto.gui.type.StringTable;
import org.taktik.icure.dto.gui.type.primitive.AttributedString;
import org.taktik.icure.dto.gui.type.primitive.PrimitiveBoolean;
import org.taktik.icure.dto.gui.type.primitive.PrimitiveDate;
import org.taktik.icure.dto.gui.type.primitive.PrimitiveNumber;
import org.taktik.icure.dto.gui.type.primitive.PrimitiveString;
import org.taktik.icure.services.external.rest.xstream.NumberConverter;


import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

/**
 * Created by aduchate on 19/11/13, 11:31
 */
public class FormUtils {
    private static Class[] formClasses = new Class[] {
            AttributedString.class,PrimitiveBoolean.class,PrimitiveDate.class,Label.class,PrimitiveNumber.class,PrimitiveString.class,
            Array.class,Dictionary.class,Measure.class,Schema.class,Formula.class, MedicationEditor.class, StringTableEditor.class,
            FormLayoutData.class, FormLayout.class, FormDataOption.class, FormLabel.class, FormPlanning.class, FormAction.class,
            Editor.class, LabelPosition.class, SubForm.class, Tag.class, Code.class, CodeType.class, MedicationTable.class, MedicationTableEditor.class,
            ActionButton.class, CheckBoxEditor.class, MeasureEditor.class, HealthcarePartyEditor.class, DateTimeEditor.class, PopupMenuEditor.class, StringTableEditor.class,
            SchemaEditor.class, StyledStringEditor.class, StringEditor.class, SubFormEditor.class, TokenFieldEditor.class, Audiometry.class,
            Label.class, NumberEditor.class, PrimitiveBoolean.class, AttributedString.class, org.taktik.icure.dto.gui.type.primitive.Label.class,
            PrimitiveBoolean.class, PrimitiveDate.class, PrimitiveNumber.class, PrimitiveString.class, Data.class, Array.class, Dictionary.class,
            Measure.class, MedicationTable.class, MenuOption.class, StringTable.class
    };

    public Reader getLegacyXmlReader(Reader reader) throws IOException, TransformerConfigurationException {
        final Source xmlSource = new StreamSource(reader);
        Source xsltSource = new StreamSource(FormUtils.class.getResourceAsStream("FormLegacyToNew.xml"));

        final PipedWriter sink = new PipedWriter();
        final Result result = new javax.xml.transform.stream.StreamResult(sink);

        PipedReader pipe = new PipedReader(sink);

        TransformerFactory transFact = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",null);
        final Transformer trans = transFact.newTransformer(xsltSource);

        new Thread (() -> {
            try {
                trans.transform(xmlSource, result);
            } catch (TransformerException e) {
                throw new IllegalStateException(e);
            } finally {
                try {
                    sink.close();
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        }).start();

        return pipe;
    }

    protected XStream getXStream() {
        XStream stream = new XStream();

        for (Class clz:formClasses) {
            stream.processAnnotations(clz);
        }
        return stream;
    }

    public FormLayout parseXml(Reader reader) {
        XStream stream = getXStream();

        return (FormLayout) stream.fromXML(reader);
    }

    public List<FormLayout> parseLegacyXml(Reader reader) throws TransformerException, IOException {
        XStream stream = getXStream();

         Array<FormLayout> forms = (Array<FormLayout>) stream.fromXML(getLegacyXmlReader(reader));

        return forms.getValue();
    }

    public FormLayout parseLegacyFormTemplateXml(Reader reader) throws TransformerException, IOException {
        XStream stream = getXStream();

        FormLayout form = (FormLayout) stream.fromXML(getLegacyXmlReader(reader));

        return form;
    }


    public void marshalForm(FormLayout fs,Writer w) {
        XStream stream = new XStream();

        stream.autodetectAnnotations(true);
        stream.registerConverter(new NumberConverter());

        stream.toXML(fs,w);
    }
}
