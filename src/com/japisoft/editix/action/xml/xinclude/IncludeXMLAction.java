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
import java.io.File;
import java.util.Iterator;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

public class IncludeXMLAction extends AbstractAction {

	protected String getFileType() { return "xml"; }
	protected String getFileDescription() { return "XML File"; }
	protected String getParse() { return null; }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null ) {
			EditixFactory.buildAndShowErrorDialog( "No document" );
			return;
		}
		File f = FileManager.getSelectedFile( true, getFileType(), getFileDescription(), container.getCurrentDocumentLocation() );
		if ( f != null ) {
			String path = null;
			if ( container.getCurrentDocumentLocation() != null )
				path = com.japisoft.framework.app.toolkit.Toolkit.getRelativePath( f, new File( container.getCurrentDocumentLocation() ) );
			else
				path = f.toString();

			path = path.replace( '\\', '/' );
			FPNode root = container.getRootNode();
			String prefix = "xi";
			boolean nsFound = false;
			
			if ( root != null ) {
				Iterator<String> prefixes = root.getNameSpaceDeclaration();
				while ( prefixes != null && prefixes.hasNext() ) {
					String p = prefixes.next();
					String ns = root.getNameSpaceDeclarationURI( p );
					if ( XMLToolkit.NS_XINCLUDE.matches( ns ) ) {
						prefix = p;
						nsFound = true;
					}
				}
			}
			
			StringBuffer sb = new StringBuffer();
			sb.append( "<" ).append( prefix ).append( ":include" );
			if ( !nsFound )
				sb.append( " xmlns:" ).append( prefix ).append( "=\"" ).append( XMLToolkit.NS_XINCLUDE ).append( "\"" );
			sb.append( " href=\"" ).append( path ).append( "\"" );
			
			String p = getParse();
			if ( p != null )
				sb.append( " parse=\"" ).append( p ).append( "\"" );
			
			sb.append( "/>" );
			container.insertText( sb.toString() );
		}
	}
	
}

