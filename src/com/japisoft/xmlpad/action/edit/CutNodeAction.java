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

package com.japisoft.xmlpad.action.edit;

import java.awt.Point;

import javax.swing.text.BadLocationException;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

public class CutNodeAction extends CopyNodeAction {

	public static final String ID = CutNodeAction.class.getName();

	public boolean notifyAction() {
		boolean res = super.notifyAction();
		if ( res == VALID_ACTION ) {
			// Cut it

			return cutAction( container, container.getCurrentNode() );
			
		}

		return res;
	}
	
	public static boolean cutAction( 
		XMLContainer container, 
		FPNode node ) {

		Point n = getNodeOffset( node );
		if ( n == null )
			return INVALID_ACTION;
		container.requestFocus();

		try {
			container.getXMLDocument().replace(
				n.x, 
				( n.y - n.x + 1 ), 
				"", 
				null 
			);
		} catch (BadLocationException e) {
			return INVALID_ACTION;
		}
		return VALID_ACTION;
	}

}
