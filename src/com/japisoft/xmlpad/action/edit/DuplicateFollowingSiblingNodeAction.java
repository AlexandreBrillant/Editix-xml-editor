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

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.action.XMLAction;

public class DuplicateFollowingSiblingNodeAction extends XMLAction {

	public boolean notifyAction() {
		FPNode node = container.getCurrentNode();
		if ( node == null ) {
			JOptionPane.showMessageDialog( container.getView(), "Can't find a following sibling node", "Error", JOptionPane.ERROR_MESSAGE );
			return false;
		}

		int caret = container.getEditor().getCaretPosition();

		FPNode followingSiblingCandidate = null;

		for ( int i = 0; i < node.childCount(); i++ ) {
			FPNode child = node.childAt( i );
			if ( child.getStartingOffset() > caret ) {
				followingSiblingCandidate = child;
				break;
			}
		}

		if ( followingSiblingCandidate != null ) {

			Point n = CopyNodeAction.getNodeOffset( followingSiblingCandidate );
			try {
				String mustCopy = container.getEditor().getText( n.x, n.y - n.x + 1 );
				
				container.getEditor().insertText( mustCopy );
				
			} catch (BadLocationException e) {
				JOptionPane.showMessageDialog( container.getView(), "Can't find a following sibling node", "Error", JOptionPane.ERROR_MESSAGE );							
			}
			
		} else {

			JOptionPane.showMessageDialog( container.getView(), "Can't find a following sibling node", "Error", JOptionPane.ERROR_MESSAGE );			

		}

		return false;
	}

}
 