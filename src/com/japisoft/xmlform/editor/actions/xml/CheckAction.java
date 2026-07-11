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

package com.japisoft.xmlform.editor.actions.xml;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;

import org.w3c.dom.Node;

import com.japisoft.framework.internationalization.Traductor;
import com.japisoft.xmlform.UIToolkit;
import com.japisoft.xmlform.component.AbstractXMLFormComponent;
import com.japisoft.xmlform.editor.EditorFrame;
import com.japisoft.xmlform.editor.EditorModel;

public class CheckAction extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {
		
		EditorFrame frame = EditorModel.getEditorFrame();
		
		if ( frame.validateDocument() ) {

			List<Node> warning = 
				frame.checkEmptyFields();
			
			if ( warning.size() > 0 ) {
				
				Node first = warning.get( 0 );
				AbstractXMLFormComponent afc = 
					getFirstComponent( first );
				if ( afc != null ) {
					frame.setFocusTo( afc );
					UIToolkit.warn( 
							Traductor.traduce( 
								"empty",
								"This field is empty or contains whitespace characters" ) );
				} else 
					UIToolkit.info( 
							Traductor.traduce( 
									"dok",
									"Your document is correct" ) );

			} else {

				UIToolkit.info( 
						Traductor.traduce(
								"dok",
								"Your document is correct" ) );

			}

		}

	}

	private AbstractXMLFormComponent getFirstComponent( Node n ) {
		
		if ( n == null )
			return null;
		
		AbstractXMLFormComponent c = 
			( AbstractXMLFormComponent )n.getUserData( "ui" );
	
		if ( c != null )
			return c;
		
		return getFirstComponent( n.getParentNode() );

	}

}
