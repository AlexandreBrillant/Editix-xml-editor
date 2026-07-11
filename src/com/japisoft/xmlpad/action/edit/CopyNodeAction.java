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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.XMLAction;

public class CopyNodeAction extends XMLAction {

	public static final String ID = CopyNodeAction.class.getName();

	protected boolean autoRequestFocus() { return false; }

	protected Point getNodeOffset() {
		FPNode n = ( FPNode )container.getCurrentNode();
		return getNodeOffset( n );
	}

	public static Point getNodeOffset( FPNode n ) {
		if ( n == null )
			return null;
		int start = n.getStartingOffset();
		int stop = n.getStoppingOffset();
		if ( n.isTag() )
			stop++;
		else
			if ( n.isText() )
				stop--;
		return new Point( start, stop );		
	}

	public static boolean copyAction( XMLContainer container, FPNode node ) {
		Point n = getNodeOffset( node );
		if (n == null) {
			JOptionPane.showMessageDialog( container.getView(), "No Node Found", "Error", JOptionPane.ERROR_MESSAGE );
		}

		container.getEditor().requestFocus();

		try {
			String content = container.getDocument().getText( n.x, n.y - n.x + 1 );
			
			Clipboard systemClipboard =
				Toolkit
					.getDefaultToolkit()
					.getSystemClipboard();
			Transferable transferableText =
				new StringSelection( content );
			systemClipboard.setContents(
				transferableText,
				null );
			
		} catch( Exception e ) {
			return INVALID_ACTION;
		}
		
		return VALID_ACTION;
	}
	
	public boolean notifyAction() {
		
		if ( container.getTreeListeners() == null )
			return INVALID_ACTION;
		if ( container.getTree() == null )
			return INVALID_ACTION;
		
		return copyAction( 
			container, 
			container.getCurrentNode() 
		);
		
	}

}
