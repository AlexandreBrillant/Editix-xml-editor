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

/*
 * Created on 30 ao�t 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.japisoft.xmlpad.helper.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.japisoft.xmlpad.editor.XMLPadDocument;

/**
 * Popup for available entities
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class EntityPopup extends CommonPopup {
	private EntityDescriptor[] entities;
	private int offset;
	private Document doc;	
	
	public EntityPopup( EntityDescriptor[] entities, int offset, Document doc ) {
		super( "Entities" );
		this.entities = entities;
		this.offset = offset;
		this.doc = doc;
		buildit();
	}

	protected void buildit() {
		for ( int i = 0; i < entities.length; i++ ) {
			add( buildMenuItem( entities[ i ] ) );
		}
	}
	
	protected JMenuItem buildMenuItem( EntityDescriptor ed ) {
		JMenuItem item = getMenuItem();
		item.setText( ed.getValue() + " (" + ed.getName() + ")" );
		return item;
	}

	protected void notifySelection( int selection ) {
		if ( selection > 0 ) {
			EntityDescriptor ed = entities[ selection ];
			try {
				doc.insertString( offset, ed.getBuiltEntity(), null );
			} catch( BadLocationException exc ) {
			}
		} else {
			try {
				doc.insertString( offset, "&;", null );
				( ( XMLPadDocument )doc ).getCurrentEditor().setCaretPosition(
				( ( XMLPadDocument )doc ).getCurrentEditor().getCaretPosition() - 1 );
			} catch( BadLocationException exc ) {
			}
		}
	}
}