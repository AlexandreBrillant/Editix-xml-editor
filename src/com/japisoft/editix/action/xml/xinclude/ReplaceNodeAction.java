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

package com.japisoft.editix.action.xml.xinclude;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.tools.XMLToolkit;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.tree.parser.InnerXMLParser;

public class ReplaceNodeAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null ) {
			EditixFactory.buildAndShowErrorDialog( "No document" );
			return;
		}
		
		int index = container.getDocument().getDefaultRootElement().getElementIndex( container.getCaretPosition() );
		if ( index == -1 ) {
			EditixFactory.buildAndShowErrorDialog( "Please select XInclude node before" );
			return;
		}

		InnerXMLParser p = new InnerXMLParser();
		p.setFlatView( true );
		try {
			Document d = p.parseContent( container.getText() );
			FPNode node = XMLToolkit.getNodeForOffset( d, container.getCaretPosition() );
			if ( !node.matchContent( "include" ) ) {
				EditixFactory.buildAndShowErrorDialog( "Please select XInclude node" );
				return;
			}
			
			boolean textMode = false;
			textMode = "text".equals( node.getAttribute( "parse" ) ) ||
					"text/plain".equals( node.getAttribute( "parse" ) );
			
			File f = new File( node.getAttribute( "href" ) );
			if ( !f.exists() && container.getCurrentDocumentLocation() != null ) {
				f = new File( new File( container.getCurrentDocumentLocation() ).getParentFile(), node.getAttribute( "href" ) );
			}
			if ( !f.exists() ) {
				EditixFactory.buildAndShowErrorDialog( "Can't find the document " + f.toString() );
				return;
			}

			String newContent = null;

			String encoding = node.getAttribute( "encoding", "UTF-8" );
			
			BufferedReader r = new BufferedReader( 
						new InputStreamReader( 
								new FileInputStream( f ), 
									encoding ) );
			try {
				String l = null;
				StringBuffer sb = new StringBuffer();
				while ( ( l = r.readLine() ) != null ) {
					sb.append( l ).append( "\n" );
				}
				newContent = sb.toString();
				newContent = newContent.replaceAll( "<\\?.*\\?>", "" );
			} finally {
				r.close();
			}
			
			int start = node.getStartingOffset();
			int end = node.getStoppingOffset();
			container.getEditor().select( start, end );
			container.getEditor().replaceSelection( newContent );
			
		} catch( Exception exc ) {
			EditixFactory.buildAndShowErrorDialog( "Invalid XML content" + exc.getMessage() );
			return;
		}

	}
}

