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

package com.japisoft.editix.plugin;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.xmlpad.IXMLPanel;

/**
 * Here a collection of editor, you can access to any opened editor
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class EditixDocumentModel {

	EditixDocumentModel() {
	}
	
	/** 
	 * @return The number of opened documents
	 */
	public int getDocumentCount() {
		return EditixFrame.THIS.getXMLContainerCount();
	}

	/**
	 * @param index A number between 0 and the number of opened documents - 1
	 * @return An access to the document at this index */
	public EditixDocument getDocument( int index ) {
		if ( index >= getDocumentCount() || index < 0 ) {
			throw new RuntimeException( "Invalid index, must be between [0-" + ( getDocumentCount() - 1 ) );
		}
		return new EditixDocument( index );
	}

	/**
	 * @return The current document or <code>null</code> if there's no current document
	 */
	public EditixDocument getCurrentDocument() {
		if ( EditixFrame.THIS.getCurrentXMLContainerIndex() == -1 )
			return null;
		return new EditixDocument( EditixFrame.THIS.getCurrentXMLContainerIndex() );
	}

	/**
	 * Create and add a new document
	 * @param type the document type XML, DTD, XSD, CSS...
	 * @return a new document
	 */
	public EditixDocument newDocument( String type ) {
		IXMLPanel c = EditixFactory.buildNewContainer( type, ( String )null );
		EditixFrame.THIS.addContainer( c );
		return new EditixDocument( getDocumentCount() - 1 );
	}
	
}
