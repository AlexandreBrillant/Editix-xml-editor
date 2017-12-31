package com.japisoft.xmlpad.nodeeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.dialog.DialogManager;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

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

		if ( DialogManager.showDialog(
				SwingUtilities.getWindowAncestor( container.getView() ),
				"Edit", 
				"Update", 
				"Edit the current selected text node", 
				null,
				pane ) == DialogManager.OK ) {
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
