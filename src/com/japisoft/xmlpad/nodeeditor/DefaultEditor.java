// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.xmlpad.nodeeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.dialog.XMLPadDialogManager;

/**
 * Here the DefaultEditor for the EditorModel. This Editor will edit any
 * Text node. All other node will be refused.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class DefaultEditor implements Editor {

	/** Accept all text node : by checking <code>isText</code> */
	public boolean accept(FPNode node) {
		return ( node.isText() );
	}

	/*
	 * Edit a text node by showing a Text dialog */
	public void edit(EditorContext context) {
		XMLContainer container = context.getXMLContainer();

		TextPane pane = new TextPane();
		pane.setText( context.getEditedText() );
		pane.setPreferredSize( new Dimension( 300, 300 ) );

		if ( XMLPadDialogManager.showDialog(
				SwingUtilities.getWindowAncestor( container.getView() ),
				"Edit", 
				"Update", 
				"Edit the current selected text node", 
				null,
				pane ) == XMLPadDialogManager.OK ) {
			context.setResult( pane.getText() );
		}
	}

	//////////////////////////////////////////

	class TextPane extends JPanel {
		private JTextArea text;
		
		public TextPane() {
			init();
		}
		
		void init() {
			setLayout( new BorderLayout() );
			add( new JScrollPane( text = new JTextArea() ) );
		}

		public void addNotify() {
			super.addNotify();
			text.requestFocus();
		}

		public void setText( String content ) {
			// Replace '&amp;', '&lt;', '&gt;', '&apos;'
			content = content.replaceAll( "&lt;", "<" );
			content = content.replaceAll( "&gt;", ">" );
			text.setText( content );
		}

		public String getText() {
			String content = text.getText();
			content = content.replaceAll( "<", "&lt;" );
			content = content.replaceAll( ">", "&gt;" );
			return content;
		}
	}

}

