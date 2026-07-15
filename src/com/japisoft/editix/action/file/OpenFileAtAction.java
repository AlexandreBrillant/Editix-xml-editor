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

package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URLDecoder;

import javax.swing.AbstractAction;

import com.japisoft.editix.document.DocumentModel;
import com.japisoft.framework.descriptor.ActionModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;

/**
 * Open file at current location
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor) */
public class OpenFileAtAction extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;
		XMLPadDocument doc = container.getXMLDocument();
		String word = doc.getWordAt(
			container.getEditor().getCaretPosition() );
		
		if ( word == null ) {
			EditixFactory.buildAndShowErrorDialog( "No file found" );
			return;
		}

		String fileName = null;

		if ( word.startsWith( "file://" ) ) {
			fileName = URLDecoder.decode( word.substring( 7 ) );
		} else
			if ( word.startsWith( "file:" ) ) {
				fileName = URLDecoder.decode( word.substring( 5 ) );
			} else {
				String loc = container.getCurrentDocumentLocation();
				if ( loc == null ) {
					fileName = word;
				} else {
					File f = new File( new File( loc ).getParentFile(), word );
					if ( f.exists() ) {
						fileName = f.toString();
					} else
						fileName = word;
				}
			}
		
		String type = DocumentModel.getDocumentByFileName( fileName ).getType();

		if ( fileName.indexOf( "://" ) == -1 ) {
			if (!OpenAction.openFile( type, true, new File( fileName ), null )) {
				EditixFactory.buildAndShowErrorDialog("Can't open " + fileName );
			}
		}
		else
			ActionModel.activeActionById( ActionModel.OPEN, e, fileName, type );
	}

}
