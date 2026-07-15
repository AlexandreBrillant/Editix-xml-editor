// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
//
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.action.dtdschema;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.japisoft.framework.descriptor.ActionModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.xmlpad.XMLContainer;

import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.input.InputFormat;
import com.thaiopensource.relaxng.input.dtd.DtdInputFormat;
import com.thaiopensource.relaxng.input.parse.sax.SAXParseInputFormat;
import com.thaiopensource.relaxng.output.LocalOutputDirectory;
import com.thaiopensource.relaxng.output.OutputDirectory;
import com.thaiopensource.relaxng.output.OutputFormat;
import com.thaiopensource.relaxng.output.dtd.DtdOutputFormat;
import com.thaiopensource.relaxng.output.rnc.RncOutputFormat;
import com.thaiopensource.relaxng.output.rng.RngOutputFormat;
import com.thaiopensource.relaxng.output.xsd.XsdOutputFormat;
import com.thaiopensource.util.UriOrFile;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class ConvertAction extends AbstractAction {

	private static final String DEFAULT_OUTPUT_ENCODING = "ISO-8859-1";
	private static final int DEFAULT_LINE_LENGTH = 72;
	private static final int DEFAULT_INDENT = 2;

	private final ErrorHandlerImpl eh = new ErrorHandlerImpl();

	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if (container == null)
			return;

		if ( EditixFactory.mustSaveDialog( container ) ) {
			return;
		}

		ActionModel.activeActionById(ActionModel.SAVE, e);
		if (!ActionModel.LAST_ACTION_STATE)
			return;

		String type = container.getDocumentInfo().getType();
		boolean rng = "RNG".equals(type);

		InputFormat in = null;
		if (rng)
			in = new SAXParseInputFormat();
		else
			in = new DtdInputFormat();

		String param = (String) getValue("param");
		if (param == null)
			return;

		OutputFormat of = null;

		if (param.equalsIgnoreCase("dtd"))
			of = new DtdOutputFormat();
		else if (param.equalsIgnoreCase("rng"))
			of = new RngOutputFormat();
		else if (param.equalsIgnoreCase("xsd"))
			of = new XsdOutputFormat();
		else if (param.equalsIgnoreCase("rnc"))
			of = new RncOutputFormat();
		else {
			System.err.println("UNKNOWN OUTPUT = " + param);
			return;
		}

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Choose your result file");

		final String EXT = param;
		final String DES = "(*." + EXT + ")";

		
		chooser.setFileFilter(
			new FileFilter() {

				public boolean accept(File f) {
					return f.isDirectory() || f.toString().endsWith( EXT );
				}

				public String getDescription() {
					return DES;
				}
			} );
		
		File outputFile = null;
		if (chooser.showSaveDialog(container.getView()) == JFileChooser.APPROVE_OPTION) {
			outputFile = chooser.getSelectedFile();
		} else
			return;

		try {
			if ( container.hasErrorMessage() ) {
				container.getErrorManager().notifyError( "Cannot convert if the current document contains errors" );
				return;
			}

			SchemaCollection sc;
			sc =
				in
					.load(
						UriOrFile.toUri(container.getCurrentDocumentLocation()),
						new String[] {
			}, param, eh);

			OutputDirectory od =
				new LocalOutputDirectory(
					sc.getMainUri(),
					outputFile,
					param,
					DEFAULT_OUTPUT_ENCODING,
					DEFAULT_LINE_LENGTH,
					DEFAULT_INDENT);
			
			of.output(sc, od, new String[] {
			}, container.getDocumentInfo().getType().toLowerCase(), eh);

			ActionModel.activeActionById(
				ActionModel.OPEN,
				e,
				outputFile.toString() );

		} catch (Exception exc) {
			container.getErrorManager().initErrorProcessing();
			container.getErrorManager().notifyError( "Can't convert to " + param + " : Check your schema" );
			container.getErrorManager().stopErrorProcessing();
		}
	}

}
