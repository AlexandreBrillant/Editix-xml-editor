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

package com.japisoft.xmlpad.action.toolkit;

import com.japisoft.xmlpad.action.XMLAction;
import com.japisoft.xmlpad.editor.XMLPadDocument;

/**
 *  Action for inserting text. It will disable the syntax popup, so user can
 * insert any text containing tag
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class InsertAction extends XMLAction {
	protected String text;

	/** Insert the following text */
	public InsertAction( String text ) {
		super();
		this.text = text;	
		setPopable(false);
		setToolbarable(false);
	}

	public InsertAction() {}

	public boolean notifyAction() {
		if ( getValue( "param" ) != null )
			text = (String)getValue( "param" );
		if ( text == null ) {
			return INVALID_ACTION;
		 }else {
			XMLPadDocument doc = ( XMLPadDocument )container.getEditor().getDocument();
			boolean sp = doc.isSyntaxPopup();
			doc.setSyntaxPopup( false );
			container.getEditor().insertText( text );
			doc.setSyntaxPopup( sp );
		}
		return VALID_ACTION;
	}

}
