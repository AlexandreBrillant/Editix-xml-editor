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

package com.japisoft.editix.ui.xslt.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import com.japisoft.xmlpad.XMLContainer;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class SyntaxCompletionAction extends AbstractAction {

	private XMLContainer container;

	public SyntaxCompletionAction( XMLContainer container ) {
		putValue( Action.SHORT_DESCRIPTION, "Enabled/Disabled syntax completion" );
		putValue( Action.NAME, "Enabled/Disabled Completion" );
		resetState();
		this.container = container;
	}

	private boolean state = false;

	private void resetState() {
		putValue( 
			Action.SMALL_ICON, 
			new ImageIcon( getClass().getResource( 
				!state ? "element_stop.png" : "element.png" ) ) );
	}

	public void actionPerformed( ActionEvent e ) {
		container.setSyntaxCompletion( state );
		state = !state;
		resetState();
	}

}
